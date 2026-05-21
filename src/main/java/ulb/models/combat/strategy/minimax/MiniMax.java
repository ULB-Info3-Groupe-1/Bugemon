package ulb.models.combat.strategy.minimax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ulb.models.bugemon.Attack;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.snapshot.CombatBugemonSnapshot;
import ulb.models.combat.snapshot.CombatSnapshot;
import ulb.models.combat.snapshot.TeamSnapshot;
import ulb.models.effect.HealEffect;
import ulb.models.item.Item;

/**
 * Minimal MiniMax implementation that operates on immutable snapshots.
 *
 * Notes: - This simplified version only considers attack actions (no items/switches). - Damage is approximated by the
 * attack's `power()` value (snapshots don't expose full combatant stats required by the real DamageCalculator).
 */
public class MiniMax {
    private static final int WIN_SCORE = 1_000_000;
    private static final double NEG_INF = -1.0e12;
    private static final double POS_INF = 1.0e12;

    private final int maxDepth;

    private final DamageCalculator damageCalculator;

    public MiniMax(int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be > 0");
        }
        this.maxDepth = maxDepth;
        this.damageCalculator = new DamageCalculator();
    }

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

    private List<SimAction> generateVoluntarySwitchActions(CombatSnapshot state, boolean forAi) {
        return this.generateForcedSwitchActions(state, forAi);
    }

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

    private int totalHp(TeamSnapshot team) {
        return team.bugemons().stream().mapToInt(b -> Math.max(0, b.currentHp())).sum();
    }
}
