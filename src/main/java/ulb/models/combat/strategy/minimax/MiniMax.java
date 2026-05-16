package ulb.models.combat.strategy.minimax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.services.CombatService;

/**
 * MiniMax with alpha-beta pruning used by {@link AITrainer}. The search explores attack/switch/item actions and
 * simulates turns deterministically using existing combat formulas.
 */
public class MiniMax {
    private static final int WIN_SCORE = 1_000_000; // Arbitrary large score to represent a guaranteed win
    private static final int KO_BONUS = 3_000;
    private static final double NEGATIVE_INF = -1.0e15;
    private static final double POSITIVE_INF = 1.0e15;

    private final CombatService combatService = new CombatService();
    private final int maxDepth;

    public MiniMax(int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be > 0");
        }
        this.maxDepth = maxDepth;
    }

    /**
     * Chooses the best action for the AI trainer given the current state of the combat.
     *
     * @param aiTrainer
     *            the AI trainer for whom we are choosing the action.
     * @param opponentTrainer
     *            the opponent trainer, required to know their current bugemon and inventory for accurate simulation.
     * @return the best action to take this turn, as a {@link TurnAction}.
     */
    public TurnAction chooseBestAction(AITrainer aiTrainer, Trainer opponentTrainer) {
        CombatState root = CombatState.from(aiTrainer, opponentTrainer);
        List<SimAction> myActions = this.generateActions(root, true);
        if (myActions.isEmpty()) {
            return this.fallbackAction(aiTrainer);
        }

        List<SimAction> opponentActions = this.generateActions(root, false);

        double bestValue = NEGATIVE_INF;
        SimAction bestAction = myActions.get(0);

        double alpha = NEGATIVE_INF;
        double beta = POSITIVE_INF;

        for (SimAction myAction : myActions) {
            double candidateValue = this.evaluateAgainstOpponentResponses(root, myAction, opponentActions, alpha, beta,
                    this.maxDepth - 1);
            if (candidateValue > bestValue) {
                bestValue = candidateValue;
                bestAction = myAction;
            }
            alpha = Math.max(alpha, bestValue);
        }

        return this.toTurnAction(bestAction, aiTrainer);
    }

    /**
     * Chooses the best switch action for the AI trainer after a KO, given the current state of the combat.
     *
     * @param aiTrainer
     *            the AI trainer for whom we are choosing the switch action.
     * @param opponentTrainer
     *            the opponent trainer.
     * @return the best bugemon to switch to.
     */
    public Bugemon chooseBestSwitchAfterKo(AITrainer aiTrainer, Trainer opponentTrainer) {
        CombatState root = CombatState.from(aiTrainer, opponentTrainer);
        List<SimAction> switchActions = this.generateForcedSwitchActions(root, true);
        if (switchActions.isEmpty()) {
            return null;
        }

        double bestValue = NEGATIVE_INF;
        int bestSwitchIdx = switchActions.get(0).index();

        for (SimAction switchAction : switchActions) {
            CombatState switched = root.copy();
            switched.aiCurrentIdx = switchAction.index();
            double value = this.solve(switched, this.maxDepth - 1, NEGATIVE_INF, POSITIVE_INF);
            if (value > bestValue) {
                bestValue = value;
                bestSwitchIdx = switchAction.index();
            }
        }

        return aiTrainer.getBugemons().get(bestSwitchIdx);
    }

    /**
     * Core MiniMax recursive solver with alpha-beta pruning.
     *
     * @param state
     *            the current combat state to evaluate from.
     * @param depth
     *            the remaining depth to explore.
     * @param alpha
     *            the current alpha value for pruning.
     * @param beta
     *            the current beta value for pruning.
     * @return the evaluated score of the given state, assuming optimal play from both sides down to the given depth.
     */
    private double solve(CombatState state, int depth, double alpha, double beta) {
        if (state.isAiDefeated()) {
            return -WIN_SCORE - depth;
        }
        if (state.isOpponentDefeated()) {
            return WIN_SCORE + depth;
        }
        if (depth <= 0) {
            return this.evaluateState(state);
        }

        List<SimAction> myActions = this.generateActions(state, true);
        if (myActions.isEmpty()) {
            return this.evaluateState(state);
        }

        List<SimAction> opponentActions = this.generateActions(state, false);

        double best = NEGATIVE_INF;
        for (SimAction myAction : myActions) {
            double worstForMe = this.evaluateAgainstOpponentResponses(state, myAction, opponentActions, alpha, beta,
                    depth - 1);
            best = Math.max(best, worstForMe);
            alpha = Math.max(alpha, best);
            if (alpha >= beta) {
                break;
            }
        }

        return best;
    }

    /**
     * Evaluates the given action for the AI trainer against all possible opponent responses, returning the worst-case
     * score.
     *
     * @param baseState
     *            the combat state before the AI trainer's action is applied.
     * @param myAction
     *            the action chosen by the AI trainer to evaluate.
     * @param opponentActions
     *            the list of possible actions the opponent could take in response.
     * @param alpha
     *            the current alpha value for pruning.
     * @param beta
     *            the current beta value for pruning.
     * @param nextDepth
     *            the depth to explore after this action and opponent response are applied.
     * @return the worst-case score for the AI trainer after applying myAction and any of the opponentActions, assuming
     *         optimal play from both sides down to nextDepth.
     */
    private double evaluateAgainstOpponentResponses(CombatState baseState, SimAction myAction,
            List<SimAction> opponentActions, double alpha, double beta, int nextDepth) {
        if (opponentActions.isEmpty()) {
            CombatState next = this.simulateTurn(baseState, myAction, SimAction.none());
            return this.solve(next, nextDepth, alpha, beta);
        }

        double worstForMe = POSITIVE_INF;
        for (SimAction opponentAction : opponentActions) {
            CombatState next = this.simulateTurn(baseState, myAction, opponentAction);
            double value = this.solve(next, nextDepth, alpha, beta);
            worstForMe = Math.min(worstForMe, value);

            if (worstForMe <= alpha) {
                break;
            }
            beta = Math.min(beta, worstForMe);
        }
        return worstForMe;
    }

    /**
     * Generates all valid actions for the given state and perspective.
     *
     * @param state
     *            the combat state to generate actions from.
     * @param forAi
     *            if true, generates actions for the AI trainer; if false, generates actions for the opponent trainer.
     * @return a list of valid actions that the specified trainer could take from the given state.
     */
    private List<SimAction> generateActions(CombatState state, boolean forAi) {
        List<Bugemon> team = forAi ? state.aiTeam : state.opponentTeam;
        int currentIdx = forAi ? state.aiCurrentIdx : state.opponentCurrentIdx;

        if (team.isEmpty()) {
            return List.of();
        }

        if (currentIdx < 0 || currentIdx >= team.size()) {
            return List.of();
        }

        Bugemon active = team.get(currentIdx);
        if (!active.isAlive()) {
            return this.generateForcedSwitchActions(state, forAi);
        }

        List<SimAction> actions = new ArrayList<>();

        List<Attack> attacks = active.getAttackList();
        for (int i = 0; i < attacks.size(); i++) {
            actions.add(SimAction.attack(i));
        }

        actions.addAll(this.generateVoluntarySwitchActions(state, forAi));

        Map<Item, Integer> inventory = forAi ? state.aiInventory : state.opponentInventory;
        actions.addAll(this.generateItemActions(inventory));

        return actions;
    }

    /**
     * Generates switch actions if the active bugemon is KO'd and must be switched out.
     *
     * @param state
     *            the combat state to generate actions from.
     * @param forAi
     *            if true, generates actions for the AI trainer; if false, generates actions for the opponent trainer.
     * @return a list of valid switch actions that the specified trainer could take from the given state.
     */
    private List<SimAction> generateForcedSwitchActions(CombatState state, boolean forAi) {
        List<Bugemon> team = forAi ? state.aiTeam : state.opponentTeam;
        int currentIdx = forAi ? state.aiCurrentIdx : state.opponentCurrentIdx;

        List<SimAction> switches = new ArrayList<>();
        for (int i = 0; i < team.size(); i++) {
            if (i == currentIdx) {
                continue;
            }
            if (team.get(i).isAlive()) {
                switches.add(SimAction.switchTo(i));
            }
        }
        return switches;
    }

    /**
     * Generates voluntary switch actions that the trainer could take even if not KO'd.
     *
     * @param state
     *            the combat state to generate actions from.
     * @param forAi
     *            if true, generates actions for the AI trainer; if false, generates actions for the opponent trainer.
     * @return a list of valid voluntary switch actions that the specified trainer could take from the given state.
     */
    private List<SimAction> generateVoluntarySwitchActions(CombatState state, boolean forAi) {
        List<Bugemon> team = forAi ? state.aiTeam : state.opponentTeam;
        int currentIdx = forAi ? state.aiCurrentIdx : state.opponentCurrentIdx;

        List<SimAction> switches = new ArrayList<>();
        for (int i = 0; i < team.size(); i++) {
            if (i == currentIdx) {
                continue;
            }
            if (team.get(i).isAlive()) {
                switches.add(SimAction.switchTo(i));
            }
        }
        return switches;
    }

    /**
     * Generates item use actions for all items in the trainer's inventory that could be used.
     *
     * @param inventory
     *            the inventory to generate item actions from.
     * @return a list of valid item use actions that could be taken with the given inventory.
     */
    private List<SimAction> generateItemActions(Map<Item, Integer> inventory) {
        if (inventory.isEmpty()) {
            return List.of();
        }

        List<Map.Entry<Item, Integer>> entries = inventory.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                .sorted(Comparator.comparing(entry -> entry.getKey().id())).collect(Collectors.toList());

        List<SimAction> actions = new ArrayList<>();
        entries.forEach(entry -> actions.add(SimAction.useItem(entry.getKey())));
        return actions;
    }

    /**
     * Simulates a turn of combat given the specified actions for both trainers, applying all combat rules and formulas
     * to produce the resulting combat state.
     *
     * @param baseState
     *            the combat state before the turn is applied
     * @param myAction
     *            the action taken by the AI trainer
     * @param opponentAction
     *            the action taken by the opponent trainer
     * @return the resulting combat state after applying the given actions and simulating the turn according to combat
     *         rules and formulas.
     */
    private CombatState simulateTurn(CombatState baseState, SimAction myAction, SimAction opponentAction) {
        CombatState state = baseState.copy();

        this.applyItemAction(state, true, myAction);
        this.applyItemAction(state, false, opponentAction);

        this.applySwitchAction(state, true, myAction);
        this.applySwitchAction(state, false, opponentAction);

        this.resolveAttacks(state, myAction, opponentAction);

        this.autoSwitchIfKo(state, true);
        this.autoSwitchIfKo(state, false);

        return state;
    }

    /**
     * Applies an item action to the given combat state for the specified trainer, if the action is valid and the item
     * is available in the inventory.
     *
     * @param state
     *            the combat state to apply the item action to.
     * @param forAi
     *            if true, applies the item action for the AI trainer; if false, applies the
     * @param action
     *            the item action to apply.
     */
    private void applyItemAction(CombatState state, boolean forAi, SimAction action) {
        if (action.kind() != SimActionKind.ITEM || action.item() == null) {
            return;
        }

        Map<Item, Integer> inventory = forAi ? state.aiInventory : state.opponentInventory;
        Integer quantity = inventory.get(action.item());
        if (quantity == null || quantity <= 0) {
            return;
        }

        Bugemon target = forAi ? state.aiCurrent() : state.opponentCurrent();
        target.apply(action.item().effect());

        if (quantity == 1) {
            inventory.remove(action.item());
        } else {
            inventory.put(action.item(), quantity - 1);
        }
    }

    /**
     * Applies a switch action to the given combat state for the specified trainer.
     *
     * @param state
     *            the combat state to apply the switch action to.
     * @param forAi
     *            if true, applies the switch action for the AI trainer; if false, applies the switch action for the
     *            opponent trainer.
     * @param action
     *            the switch action to apply.
     */
    private void applySwitchAction(CombatState state, boolean forAi, SimAction action) {
        if (action.kind() != SimActionKind.SWITCH) {
            return;
        }

        if (forAi) {
            if (this.isValidAliveIndex(state.aiTeam, action.index())) {
                state.aiCurrentIdx = action.index();
            }
        } else {
            if (this.isValidAliveIndex(state.opponentTeam, action.index())) {
                state.opponentCurrentIdx = action.index();
            }
        }
    }

    /**
     * Resolves the attack actions for both trainers according to combat rules.
     *
     * @param state
     *            the combat state to apply the attacks to.
     * @param myAction
     *            the action taken by the AI trainer, which may or may not be an attack.
     * @param opponentAction
     *            the action taken by the opponent trainer, which may or may not be an attack.
     */
    private void resolveAttacks(CombatState state, SimAction myAction, SimAction opponentAction) {
        boolean aiAttacks = myAction.kind() == SimActionKind.ATTACK;
        boolean oppAttacks = opponentAction.kind() == SimActionKind.ATTACK;

        if (aiAttacks && oppAttacks) {
            this.resolveDualAttack(state, myAction, opponentAction);
            return;
        }
        if (aiAttacks) {
            this.applyAttack(state, true, myAction);
            return;
        }
        if (oppAttacks) {
            this.applyAttack(state, false, opponentAction);
        }
    }

    /**
     * Resolves a turn where both trainers have chosen attack actions.
     *
     * @param state
     *            the combat state to apply the attacks to.
     * @param myAction
     *            the attack action taken by the AI trainer.
     * @param opponentAction
     *            the attack action taken by the opponent trainer.
     */
    private void resolveDualAttack(CombatState state, SimAction myAction, SimAction opponentAction) {
        Bugemon aiCurrent = state.aiCurrent();
        Bugemon oppCurrent = state.opponentCurrent();

        boolean aiFirst = aiCurrent.getInitiative() > oppCurrent.getInitiative();
        if (aiFirst) {
            this.applyAttack(state, true, myAction);
            if (state.opponentCurrent().isAlive()) {
                this.applyAttack(state, false, opponentAction);
            }
        } else {
            this.applyAttack(state, false, opponentAction);
            if (state.aiCurrent().isAlive()) {
                this.applyAttack(state, true, myAction);
            }
        }
    }

    /**
     * Applies the given attack action to the combat state for the specified trainer.
     *
     * @param state
     *            the combat state to apply the attack to.
     * @param fromAi
     *            if true, applies the attack from the AI trainer's perspective; if false, applies the attack from the
     *            opponent trainer's perspective.
     * @param action
     *            the attack action to apply.
     */
    private void applyAttack(CombatState state, boolean fromAi, SimAction action) {
        Bugemon attacker = fromAi ? state.aiCurrent() : state.opponentCurrent();
        Bugemon defender = fromAi ? state.opponentCurrent() : state.aiCurrent();

        if (!attacker.isAlive() || !defender.isAlive()) {
            return;
        }

        List<Attack> attacks = attacker.getAttackList();
        if (action.index() < 0 || action.index() >= attacks.size()) {
            return;
        }

        Attack attack = attacks.get(action.index());
        int damage = this.combatService.calculateDamage(attack, attacker, defender, 1.0);
        defender.takeDamage(damage);

        this.applySelfHealFromAttack(attack, attacker);
    }

    /**
     * Applies any self-healing effects from the given attack to the attacker, if present.
     *
     * @param attack
     *            the attack whose effects to check for self-healing.
     * @param attacker
     *            the bugemon to apply the self-healing to if applicable.
     */
    private void applySelfHealFromAttack(Attack attack, Bugemon attacker) {
        attack.effects().forEach(effect -> {
            if (effect instanceof EffectHeal heal && heal.amount() > 0) {
                attacker.apply(heal);
            }
        });
    }

    /**
     * Switches the bugemon for the specified trainer if their active bugemon is KO'd, choosing the best available
     * switch option.
     *
     * @param state
     *            the combat state to check for KO and apply the auto-switch to.
     * @param forAi
     *            if true, checks and applies the auto-switch for the AI trainer; if false, checks and applies the
     *            auto-switch for the opponent trainer.
     */
    private void autoSwitchIfKo(CombatState state, boolean forAi) {
        Bugemon current = forAi ? state.aiCurrent() : state.opponentCurrent();
        if (current.isAlive()) {
            return;
        }

        if (forAi) {
            if (state.isAiDefeated()) {
                return;
            }
            int nextIdx = this.chooseAutoSwitchIndex(state.aiTeam, state.aiCurrentIdx, state.opponentCurrent());
            if (nextIdx >= 0) {
                state.aiCurrentIdx = nextIdx;
            }
        } else {
            if (state.isOpponentDefeated()) {
                return;
            }
            int nextIdx = this.chooseAutoSwitchIndex(state.opponentTeam, state.opponentCurrentIdx, state.aiCurrent());
            if (nextIdx >= 0) {
                state.opponentCurrentIdx = nextIdx;
            }
        }
    }

    /**
     * Chooses the best bugemon index to switch to for the given team and opponent.
     *
     * @param team
     *            the team of bugemon to choose from.
     * @param currentIdx
     *            the current active bugemon index, which cannot be switched to.
     * @param opponentCurrent
     *            the opponent's current bugemon.
     * @return the index of the best bugemon to switch to.
     */
    private int chooseAutoSwitchIndex(List<Bugemon> team, int currentIdx, Bugemon opponentCurrent) {
        int bestIdx = -1;
        double bestScore = NEGATIVE_INF;

        for (int i = 0; i < team.size(); i++) {
            if (i == currentIdx) {
                continue;
            }
            Bugemon candidate = team.get(i);
            if (!candidate.isAlive()) {
                continue;
            }

            double score = this.switchSuitability(candidate, opponentCurrent);
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }

        return bestIdx;
    }

    /**
     * Evaluates the suitability of switching to the given candidate bugemon against the opponent's current bugemon,
     * based on potential damage output, survivability, and overall matchup.
     *
     * @param candidate
     *            the candidate bugemon to evaluate for switching in.
     * @param opponent
     *            the opponent's current bugemon, which may be null if the opponent has no active bugemon.
     * @return a score representing how good of a switch the candidate is against the opponent's current bugemon, with
     *         higher being better.
     */
    private double switchSuitability(Bugemon candidate, Bugemon opponent) {
        if (opponent == null) {
            return candidate.getHp();
        }

        int bestDamage = this.bestAttackDamage(candidate, opponent);
        int incomingDamage = this.bestAttackDamage(opponent, candidate);

        double hpRatio = this.safeRatio(candidate.getHp(), candidate.getMaxHp());
        return bestDamage - incomingDamage + (hpRatio * 200.0);
    }

    /**
     * Converts a SimAction to a TurnAction that can be executed in the actual combat, ensuring that the action is valid
     * and adjusting it if necessary to fit the current state of the AI trainer.
     *
     * @param action
     *            the SimAction to convert, which may be an attack, switch, item use, or none.
     * @param aiTrainer
     *            the AI trainer for whom we are converting the action, used to validate and adjust the action based on
     *            their current state.
     * @return a TurnAction representing the given SimAction, adjusted as necessary to be valid for the current state of
     *         the AI trainer.
     */
    private TurnAction toTurnAction(SimAction action, AITrainer aiTrainer) {
        return switch (action.kind()) {
            case ATTACK -> {
                List<Attack> attacks = aiTrainer.getCurrentBugemonAttackList();
                int idx = Math.max(0, Math.min(action.index(), attacks.size() - 1));
                yield new TurnAction.AttackAction(attacks.get(idx));
            }
            case SWITCH -> {
                List<Bugemon> bugemons = aiTrainer.getBugemons();
                int idx = Math.max(0, Math.min(action.index(), bugemons.size() - 1));
                yield new TurnAction.SwitchAction(bugemons.get(idx));
            }
            case ITEM -> new TurnAction.UseItemAction(action.item());
            case NONE -> this.fallbackAction(aiTrainer);
        };
    }

    /**
     * Provides a fallback action in case the chosen SimAction is invalid or cannot be executed for some reason.
     *
     * @param aiTrainer
     *            the AI trainer for whom we are providing the fallback action, used to determine a valid attack action
     *            if needed.
     * @return a TurnAction that represents a safe fallback, which in this case is to use the first available attack of
     *         the current bugemon, or to do nothing if no attacks are available.
     */
    private TurnAction fallbackAction(AITrainer aiTrainer) {
        List<Attack> attacks = aiTrainer.getCurrentBugemonAttackList();
        if (attacks.isEmpty()) {
            throw new IllegalStateException("AITrainer current bugemon has no available attack");
        }
        return new TurnAction.AttackAction(attacks.get(0));
    }

    /**
     * Evaluates the given combat state from the perspective of the AI trainer.
     *
     * @param state
     *            the combat state to evaluate.
     * @return a score representing how favorable the given combat state is for the AI trainer.
     */
    private double evaluateState(CombatState state) {
        if (state.isAiDefeated()) {
            return -WIN_SCORE;
        }
        if (state.isOpponentDefeated()) {
            return WIN_SCORE;
        }

        Bugemon aiCurrent = state.aiCurrent();
        Bugemon oppCurrent = state.opponentCurrent();

        int aiTotalHp = this.totalHp(state.aiTeam);
        int oppTotalHp = this.totalHp(state.opponentTeam);
        int aiTotalMaxHp = this.totalMaxHp(state.aiTeam);
        int oppTotalMaxHp = this.totalMaxHp(state.opponentTeam);

        double aiHpRatio = this.safeRatio(aiTotalHp, aiTotalMaxHp);
        double oppHpRatio = this.safeRatio(oppTotalHp, oppTotalMaxHp);

        int aiAlive = this.aliveCount(state.aiTeam);
        int oppAlive = this.aliveCount(state.opponentTeam);

        int aiBestDamage = this.bestAttackDamage(aiCurrent, oppCurrent);
        int oppBestDamage = this.bestAttackDamage(oppCurrent, aiCurrent);

        double currentMatchup = (double) aiBestDamage - oppBestDamage;
        double initiativeEdge = aiCurrent.getInitiative() - oppCurrent.getInitiative();

        double healingReserve = this.totalHealingPotential(state.aiInventory)
                - this.totalHealingPotential(state.opponentInventory);

        double aiThreatenedPenalty = 0.0;
        if (oppBestDamage >= aiCurrent.getHp()) {
            aiThreatenedPenalty -= KO_BONUS;
        }

        double oppThreatenedBonus = 0.0;
        if (aiBestDamage >= oppCurrent.getHp()) {
            oppThreatenedBonus += KO_BONUS;
        }

        return (aiHpRatio - oppHpRatio) * 7_500.0 + (aiAlive - oppAlive) * 1_200.0 + currentMatchup * 4.0
                + initiativeEdge * 1.5 + healingReserve * 2.0 + aiThreatenedPenalty + oppThreatenedBonus;
    }

    /**
     * Calculates the best possible damage that the attacker could deal to the defender.
     *
     * @param attacker
     *            the bugemon whose attacks to evaluate for damage output.
     * @param defender
     *            the bugemon to evaluate as the target of the attacks for damage output.
     * @return the highest damage that any of the attacker's attacks could deal to the defender, according to combat
     *         formulas.
     */
    private int bestAttackDamage(Bugemon attacker, Bugemon defender) {
        int best = 0;
        for (Attack attack : attacker.getAttackList()) {
            int damage = this.combatService.calculateDamage(attack, attacker, defender, 1.0);
            if (damage > best) {
                best = damage;
            }
        }
        return best;
    }

    /**
     * Calculates the total current HP of all bugemon in the given team, treating any negative HP as 0 for the purpose
     * of this calculation.
     *
     * @param team
     *            the team of bugemon to calculate total HP for.
     * @return the sum of the current HP of all bugemon in the team, with any negative HP treated as 0.
     */
    private int totalHp(List<Bugemon> team) {
        int total = 0;
        for (Bugemon bugemon : team) {
            total += Math.max(0, bugemon.getHp());
        }
        return total;
    }

    /**
     * Calculates the total maximum HP of all bugemon in the given team.
     *
     * @param team
     *            the team of bugemon to calculate total maximum HP for.
     * @return the sum of the maximum HP of all bugemon in the team, with any bugemon with max HP less than 1 treated as
     *         having a max HP of 1.
     */
    private int totalMaxHp(List<Bugemon> team) {
        int total = 0;
        for (Bugemon bugemon : team) {
            total += Math.max(1, bugemon.getMaxHp());
        }
        return total;
    }

    /**
     * Counts the number of bugemon in the given team that are currently alive.
     *
     * @param team
     *            the team of bugemon to count alive members of.
     * @return the number of bugemon in the team that have current HP greater than 0.
     */
    private int aliveCount(List<Bugemon> team) {
        int alive = 0;
        for (Bugemon bugemon : team) {
            if (bugemon.isAlive()) {
                alive++;
            }
        }
        return alive;
    }

    /**
     * Safely calculates the ratio of value to max.
     *
     * @param value
     *            the numerator of the ratio, which can be any integer.
     * @param max
     *            the denominator of the ratio, which should be a positive integer.
     * @return the ratio of value to max, or 0.0 if max is not positive.
     */
    private double safeRatio(int value, int max) {
        if (max <= 0) {
            return 0.0;
        }
        return (double) value / max;
    }

    /**
     * Calculates the total potential healing that could be applied to the trainer's active bugemon from the items in
     * their inventory.
     *
     * @param inventory
     *            the inventory to calculate total healing potential from.
     * @return the total amount of healing that could be applied to the active bugemon from the items in the inventory.
     */
    private int totalHealingPotential(Map<Item, Integer> inventory) {
        int total = 0;
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            Item item = entry.getKey();
            Integer qty = entry.getValue();
            if (qty == null || qty <= 0) {
                continue;
            }
            if (item.effect() instanceof EffectHeal heal) {
                total += Math.max(0, heal.amount()) * qty;
            }
        }
        return total;
    }

    /**
     * Checks if the given index is a valid index for the team and that the bugemon at that index is alive.
     *
     * @param team
     *            the team of bugemon to check.
     * @param idx
     *            the index to check.
     * @return true if the index is valid and the bugemon at that index is alive, false otherwise.
     */
    private boolean isValidAliveIndex(List<Bugemon> team, int idx) {
        return idx >= 0 && idx < team.size() && team.get(idx).isAlive();
    }
}
