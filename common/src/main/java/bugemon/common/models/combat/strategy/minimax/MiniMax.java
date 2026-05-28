package bugemon.common.models.combat.strategy.minimax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.damage.DamageCalculator;
import bugemon.common.models.combat.snapshot.CombatBugemonSnapshot;
import bugemon.common.models.combat.snapshot.CombatSnapshot;
import bugemon.common.models.combat.snapshot.TeamSnapshot;
import bugemon.common.models.effect.HealEffect;
import bugemon.common.models.item.Item;

/**
 * Alpha-beta pruning MiniMax search that selects the best combat action from a {@link CombatSnapshot}.
 *
 * <p>
 * The search operates entirely on immutable snapshot objects so it produces no side effects on the live combat model.
 * Supported action types are attacks, voluntary switches, forced switches, and item usage (healing items only). Damage
 * is computed via {@link DamageCalculator} using the effective stats exposed by each snapshot.
 *
 * <p>
 * The search depth is bounded by the {@code maxDepth} value supplied at construction time (see
 * {@link bugemon.common.Configuration.Game#MIN_MAX_MAXIMAL_DEPTH}). A terminal win state is scored at
 * ±{@value #WIN_SCORE}; leaf nodes use a simple total-HP difference heuristic.
 */
public class MiniMax {
    private static final int WIN_SCORE = 1_000_000;
    private static final double NEG_INF = -1.0e12;
    private static final double POS_INF = 1.0e12;

    private final int maxDepth;

    private final DamageCalculator damageCalculator;

    /**
     * Creates a {@code MiniMax} searcher with the given depth limit.
     *
     * @param maxDepth
     *            maximum number of half-turns (plies) to search; must be positive
     * @throws IllegalArgumentException
     *             if {@code maxDepth} is not positive
     */
    public MiniMax(int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be > 0");
        }
        this.maxDepth = maxDepth;
        this.damageCalculator = new DamageCalculator();
    }

    /**
     * Returns the best {@link SimAction} for the indicated side given the current combat state.
     *
     * @param state
     *            snapshot of the current combat state
     * @param chooseForAiTeam
     *            {@code true} to choose for the AI team, {@code false} for the opponent
     * @return the highest-scoring action, or {@link SimAction#none()} if no actions are available
     */
    public SimAction chooseBestAction(CombatSnapshot state, boolean chooseForAiTeam) {
        Objects.requireNonNull(state);

        List<SimAction> actions = this.generateActions(state, chooseForAiTeam);
        if (actions.isEmpty()) {
            return SimAction.none();
        }

        double best = NEG_INF;
        SimAction bestAction = actions.get(0);
        for (SimAction a : actions) {
            CombatSnapshot next = this.simulateTurn(state, a, SimAction.none(), chooseForAiTeam);
            double val = this.solve(next, this.maxDepth - 1, NEG_INF, POS_INF, !chooseForAiTeam);
            if (val > best) {
                best = val;
                bestAction = a;
            }
        }

        return bestAction;
    }

    /**
     * Recursive alpha-beta search returning a heuristic score for {@code playerState} from the AI's perspective.
     *
     * <p>
     * Terminal conditions are checked first: an AI defeat returns a large negative score, an opponent defeat returns a
     * large positive score, and a depth of zero falls back to {@link #evaluateState}. The maximising node expands the
     * AI's actions; the minimising node expands the opponent's actions.
     *
     * @param playerState
     *            the current combat snapshot
     * @param depth
     *            remaining search depth in plies
     * @param alpha
     *            current lower bound on the maximiser's guaranteed score
     * @param beta
     *            current upper bound on the minimiser's guaranteed score
     * @param maximizing
     *            {@code true} when it is the AI's turn to move (maximising player)
     * @return the minimax score for this node
     */
    private double solve(CombatSnapshot playerState, int depth, double alpha, double beta, boolean maximizing) {
        if (playerState.isAiDefeated()) {
            return (double) -WIN_SCORE - depth;
        }
        if (playerState.isOpponentDefeated()) {
            return (double) WIN_SCORE + depth;
        }
        if (depth <= 0) {
            return this.evaluateState(playerState);
        }

        // In our model we alternate attacker/opponent by swapping snapshots.
        List<SimAction> actions = this.generateActions(playerState, maximizing);
        if (actions.isEmpty()) {
            return this.evaluateState(playerState);
        }

        if (maximizing) {
            double value = NEG_INF;
            for (SimAction a : actions) {
                CombatSnapshot next = this.simulateTurn(playerState, a, SimAction.none(), maximizing);
                value = Math.max(value, this.solve(next, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        } else {
            double value = POS_INF;
            for (SimAction a : actions) {
                CombatSnapshot next = this.simulateTurn(playerState, SimAction.none(), a, maximizing);
                value = Math.min(value, this.solve(next, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        }
    }

    /**
     * Builds the list of legal actions available to one side in {@code state}.
     *
     * <p>
     * If the active Bugemon is {@code null} or fainted, only forced-switch actions are generated. Otherwise attacks,
     * voluntary switches, and available item uses are all included.
     *
     * @param state
     *            the snapshot to generate actions for
     * @param forAi
     *            {@code true} to generate actions for the AI team, {@code false} for the opponent
     * @return a mutable list of legal {@link SimAction}s; never {@code null}
     */
    private List<SimAction> generateActions(CombatSnapshot state, boolean forAi) {
        TeamSnapshot team = forAi ? state.aiTeam() : state.playerTeam();
        CombatBugemonSnapshot active = team.active();
        if (active == null || !active.isAlive()) {
            return this.generateForcedSwitchActions(state, forAi);
        }

        List<SimAction> res = new ArrayList<>();
        List<Attack> attacks = active.attacks();
        for (int i = 0; i < attacks.size(); i++) {
            res.add(SimAction.attack(i));
        }

        // switches
        res.addAll(this.generateVoluntarySwitchActions(state, forAi));

        // items
        Map<Item, Integer> inventory = forAi ? state.aiInventory() : state.opponentInventory();
        res.addAll(this.generateItemActions(inventory));

        return res;
    }

    /**
     * Generates one {@link SimActionKind#ITEM} action per item that still has remaining quantity in {@code inventory}.
     * Items are sorted by ID for deterministic ordering.
     *
     * @param inventory
     *            the current inventory map; may be {@code null} or empty
     * @return a list of item actions; empty if no usable items remain
     */
    private List<SimAction> generateItemActions(Map<Item, Integer> inventory) {
        if (inventory == null || inventory.isEmpty()) {
            return List.of();
        }

        List<Map.Entry<Item, Integer>> entries = inventory.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() > 0)
                .sorted(Comparator.comparing(e -> e.getKey().id())).toList();

        List<SimAction> res = new ArrayList<>();
        entries.forEach(e -> res.add(SimAction.useItem(e.getKey())));
        return res;
    }

    /**
     * Generates switch actions for every alive bench Bugemon when the active Bugemon is KO'd.
     *
     * @param state
     *            the current snapshot
     * @param forAi
     *            {@code true} to generate for the AI team
     * @return list of forced-switch {@link SimAction}s by roster index
     */
    private List<SimAction> generateForcedSwitchActions(CombatSnapshot state, boolean forAi) {
        TeamSnapshot team = forAi ? state.aiTeam() : state.playerTeam();
        List<CombatBugemonSnapshot> list = team.bugemons();
        int currentIdx = list.indexOf(team.active());

        List<SimAction> switches = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == currentIdx) {
                continue;
            }
            if (list.get(i).isAlive()) {
                switches.add(SimAction.switchTo(i));
            }
        }
        return switches;
    }

    /**
     * Delegates to {@link #generateForcedSwitchActions} since voluntary and forced switch candidate sets are identical
     * in the current simulation model.
     *
     * @param state
     *            the current snapshot
     * @param forAi
     *            {@code true} to generate for the AI team
     * @return list of voluntary-switch {@link SimAction}s
     */
    private List<SimAction> generateVoluntarySwitchActions(CombatSnapshot state, boolean forAi) {
        return this.generateForcedSwitchActions(state, forAi);
    }

    /**
     * Simulates a full half-turn by applying items, switches, and attack resolution in sequence and returning the
     * resulting snapshot.
     *
     * <p>
     * When {@code primaryIsAi} is {@code true}, the primary action belongs to the AI and the secondary to the opponent;
     * items and switches are applied for the AI first. Attack ordering is determined by a maximum-power heuristic (see
     * {@link #resolveAttacks}).
     *
     * @param state
     *            the snapshot before the turn
     * @param primaryAction
     *            the action taken by the primary combatant
     * @param secondaryAction
     *            the action taken by the secondary combatant (may be {@link SimAction#none()})
     * @param primaryIsAi
     *            {@code true} if the primary action belongs to the AI
     * @return the updated snapshot after the turn
     */
    private CombatSnapshot simulateTurn(CombatSnapshot state, SimAction primaryAction, SimAction secondaryAction,
            boolean primaryIsAi) {
        // Apply items first
        if (primaryIsAi) {
            state = this.applyItemAction(state, true, primaryAction);
            state = this.applyItemAction(state, false, secondaryAction);
        } else {
            state = this.applyItemAction(state, true, secondaryAction);
            state = this.applyItemAction(state, false, primaryAction);
        }

        // Apply switches
        if (primaryIsAi) {
            state = this.applySwitchAction(state, true, primaryAction);
            state = this.applySwitchAction(state, false, secondaryAction);
        } else {
            state = this.applySwitchAction(state, true, secondaryAction);
            state = this.applySwitchAction(state, false, primaryAction);
        }

        // Resolve attacks according to initiative heuristic
        if (primaryIsAi) {
            state = this.resolveAttacks(state, primaryAction, secondaryAction);
        } else {
            state = this.resolveAttacks(state, secondaryAction, primaryAction);
        }

        // Auto-switch if ko
        state = this.autoSwitchIfKo(state, true);
        state = this.autoSwitchIfKo(state, false);

        return state;
    }

    /**
     * Applies a single attack from one side against the other, reducing the defender's HP accordingly. Returns the
     * state unchanged if either combatant is absent or fainted, or if the action index is out of bounds.
     *
     * @param state
     *            the snapshot before the attack
     * @param fromAi
     *            {@code true} if the AI is the attacker
     * @param action
     *            the attack action containing the move index
     * @return the updated snapshot with the defender's HP reduced
     */
    private CombatSnapshot applyAttack(CombatSnapshot state, boolean fromAi, SimAction action) {
        TeamSnapshot attackerTeam = fromAi ? state.aiTeam() : state.playerTeam();
        TeamSnapshot defenderTeam = fromAi ? state.playerTeam() : state.aiTeam();

        CombatBugemonSnapshot attacker = attackerTeam.active();
        CombatBugemonSnapshot defender = defenderTeam.active();

        if (attacker == null || defender == null || !attacker.isAlive() || !defender.isAlive()) {
            return state;
        }

        List<Attack> attacks = attacker.attacks();
        if (action.index() < 0 || action.index() >= attacks.size()) {
            return state;
        }

        Attack attack = attacks.get(action.index());
        int damage = this.damageCalculator.calculateDamage(attack, attacker.effectiveAttack(),
                defender.effectiveDefense());

        // build new defender snapshot with reduced HP
        CombatBugemonSnapshot newDefender = new CombatBugemonSnapshot(Math.max(0, defender.currentHp() - damage),
                defender.attacks(), defender.maxHp(), defender.effectiveAttack(), defender.effectiveDefense(),
                defender.initiative());

        List<CombatBugemonSnapshot> newDefList = new ArrayList<>(defenderTeam.bugemons());
        int idx = newDefList.indexOf(defender);
        if (idx >= 0) {
            newDefList.set(idx, newDefender);
        }
        TeamSnapshot newDefTeam = new TeamSnapshot(List.copyOf(newDefList), newDefender);

        if (fromAi) {
            return new CombatSnapshot(state.playerTeam(), newDefTeam, state.aiInventory(), state.opponentInventory());
        } else {
            return new CombatSnapshot(newDefTeam, state.aiTeam(), state.aiInventory(), state.opponentInventory());
        }
    }

    /**
     * Applies a heal item action for one side if the action is a valid item use, the item is a {@link HealEffect}, and
     * there is remaining quantity. Returns the state unchanged for any other action kind or item type.
     *
     * @param state
     *            the snapshot before item use
     * @param forAi
     *            {@code true} to apply the item on the AI's active Bugemon
     * @param action
     *            the action to evaluate; ignored if not {@link SimActionKind#ITEM}
     * @return the updated snapshot with healed HP and decremented inventory, or the original state
     */
    private CombatSnapshot applyItemAction(CombatSnapshot state, boolean forAi, SimAction action) {
        if (action.kind() != SimActionKind.ITEM || action.item() == null) {
            return state;
        }
        Map<Item, Integer> inventory = forAi ? state.aiInventory() : state.opponentInventory();
        if (inventory == null) {
            return state;
        }
        Integer qty = inventory.get(action.item());
        if (qty == null || qty <= 0) {
            return state;
        }
        TeamSnapshot team = forAi ? state.aiTeam() : state.playerTeam();
        CombatBugemonSnapshot active = team.active();
        if (active == null) {
            return state;
        }

        // handle HealEffect only
        if (action.item().effect() instanceof HealEffect heal) {
            int healAmt = Math.max(0, heal.getAmount());
            int newHp = Math.min(active.maxHp(), active.currentHp() + healAmt);
            CombatBugemonSnapshot newActive = new CombatBugemonSnapshot(newHp, active.attacks(), active.maxHp(),
                    active.effectiveAttack(), active.effectiveDefense(), active.initiative());

            List<CombatBugemonSnapshot> newList = new ArrayList<>(team.bugemons());
            int idx = newList.indexOf(active);
            if (idx >= 0) {
                newList.set(idx, newActive);
            }
            TeamSnapshot newTeam = new TeamSnapshot(List.copyOf(newList), newActive);

            Map<Item, Integer> newInv = new HashMap<>(inventory);
            if (qty == 1) {
                newInv.remove(action.item());
            } else {
                newInv.put(action.item(), qty - 1);
            }

            if (forAi) {
                return new CombatSnapshot(state.playerTeam(), newTeam, newInv, state.opponentInventory());
            } else {
                return new CombatSnapshot(newTeam, state.aiTeam(), state.aiInventory(), newInv);
            }
        }

        return state;
    }

    /**
     * Applies a switch action for one side, updating the team's active Bugemon to the roster index in the action.
     * Returns the state unchanged if the action is not a switch, the index is out of bounds, or the target is fainted.
     *
     * @param state
     *            the snapshot before the switch
     * @param forAi
     *            {@code true} to switch for the AI team
     * @param action
     *            the action to evaluate; ignored if not {@link SimActionKind#SWITCH}
     * @return the updated snapshot with a new active Bugemon, or the original state
     */
    private CombatSnapshot applySwitchAction(CombatSnapshot state, boolean forAi, SimAction action) {
        if (action.kind() != SimActionKind.SWITCH) {
            return state;
        }
        TeamSnapshot team = forAi ? state.aiTeam() : state.playerTeam();
        List<CombatBugemonSnapshot> list = team.bugemons();
        int idx = action.index();
        if (idx < 0 || idx >= list.size()) {
            return state;
        }
        CombatBugemonSnapshot target = list.get(idx);
        if (!target.isAlive()) {
            return state;
        }

        TeamSnapshot newTeam = new TeamSnapshot(List.copyOf(list), target);
        if (forAi) {
            return new CombatSnapshot(state.playerTeam(), newTeam, state.aiInventory(), state.opponentInventory());
        }
        return new CombatSnapshot(newTeam, state.aiTeam(), state.aiInventory(), state.opponentInventory());
    }

    /**
     * Resolves the attack phase of a turn, ordering strikes by the active Bugemon's maximum attack power as an
     * initiative heuristic. If only one side is attacking, only that attack is applied. If neither side is attacking,
     * the state is returned unchanged.
     *
     * @param state
     *            the snapshot after items and switches have been resolved
     * @param aiAction
     *            the AI's action for this turn
     * @param oppAction
     *            the opponent's action for this turn
     * @return the updated snapshot after attacks
     */
    private CombatSnapshot resolveAttacks(CombatSnapshot state, SimAction aiAction, SimAction oppAction) {
        boolean aiAttacks = aiAction.kind() == SimActionKind.ATTACK;
        boolean oppAttacks = oppAction.kind() == SimActionKind.ATTACK;

        if (!aiAttacks && !oppAttacks) {
            return state;
        }

        CombatBugemonSnapshot aiActive = state.aiTeam().active();
        CombatBugemonSnapshot oppActive = state.playerTeam().active();
        if (aiActive == null || oppActive == null) {
            return state;
        }

        int aiMax = aiActive.attacks().stream().mapToInt(Attack::power).max().orElse(0);
        int oppMax = oppActive.attacks().stream().mapToInt(Attack::power).max().orElse(0);
        boolean aiFirst = aiMax >= oppMax;

        if (aiAttacks && oppAttacks) {
            if (aiFirst) {
                state = this.applyAttack(state, true, aiAction);
                // recompute opp active after potential change
                if (state.playerTeam().active().isAlive()) {
                    state = this.applyAttack(state, false, oppAction);
                }
            } else {
                state = this.applyAttack(state, false, oppAction);
                if (state.aiTeam().active().isAlive()) {
                    state = this.applyAttack(state, true, aiAction);
                }
            }
            return state;
        }

        if (aiAttacks) {
            return this.applyAttack(state, true, aiAction);
        }
        return this.applyAttack(state, false, oppAction);
    }

    /**
     * Automatically switches in the first alive bench Bugemon if the given side's active Bugemon is fainted. Returns
     * the state unchanged if the active Bugemon is still alive or no bench replacement exists.
     *
     * @param state
     *            the snapshot to inspect
     * @param forAi
     *            {@code true} to auto-switch for the AI team
     * @return the updated snapshot with a new active Bugemon, or the original state
     */
    private CombatSnapshot autoSwitchIfKo(CombatSnapshot state, boolean forAi) {
        TeamSnapshot team = forAi ? state.aiTeam() : state.playerTeam();
        if (team.active() != null && team.active().isAlive()) {
            return state;
        }

        List<CombatBugemonSnapshot> list = team.bugemons();
        for (CombatBugemonSnapshot b : list) {
            if (b.isAlive()) {
                TeamSnapshot newTeam = new TeamSnapshot(List.copyOf(list), b);
                if (forAi) {
                    return new CombatSnapshot(state.playerTeam(), newTeam, state.aiInventory(),
                            state.opponentInventory());
                }
                return new CombatSnapshot(newTeam, state.aiTeam(), state.aiInventory(), state.opponentInventory());
            }
        }
        return state;
    }

    /**
     * Heuristic evaluation of a non-terminal state. Returns ±{@value #WIN_SCORE} for terminal states; otherwise returns
     * the difference between the AI's total remaining HP and the opponent's total remaining HP.
     *
     * @param state
     *            the snapshot to score
     * @return a positive value when the AI is winning, negative when losing
     */
    private double evaluateState(CombatSnapshot state) {
        if (state.isAiDefeated()) {
            return -WIN_SCORE;
        }
        if (state.isOpponentDefeated()) {
            return WIN_SCORE;
        }

        int aiHp = this.totalHp(state.aiTeam());
        int oppHp = this.totalHp(state.playerTeam());
        return (aiHp - oppHp);
    }

    /**
     * Sums the current HP of every Bugemon in {@code team}, clamped to zero for fainted members.
     *
     * @param team
     *            the team snapshot to sum
     * @return total remaining HP across the entire team
     */
    private int totalHp(TeamSnapshot team) {
        return team.bugemons().stream().mapToInt(b -> Math.max(0, b.currentHp())).sum();
    }
}
