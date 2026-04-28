package ulb.models.trainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    private final int maxDepth;

    public MiniMax(int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be > 0");
        }
        this.maxDepth = maxDepth;
    }

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
        int damage = CombatService.calculateDamage(attack, attacker, defender, 1.0);
        defender.takeDamage(damage);

        this.applySelfHealFromAttack(attack, attacker);
    }

    private void applySelfHealFromAttack(Attack attack, Bugemon attacker) {
        attack.effects().forEach(effect -> {
            if (effect instanceof EffectHeal heal && heal.amount() > 0) {
                attacker.apply(heal);
            }
        });
    }

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

    private double switchSuitability(Bugemon candidate, Bugemon opponent) {
        if (opponent == null) {
            return candidate.getHp();
        }

        int bestDamage = this.bestAttackDamage(candidate, opponent);
        int incomingDamage = this.bestAttackDamage(opponent, candidate);

        double hpRatio = this.safeRatio(candidate.getHp(), candidate.getMaxHp());
        return bestDamage - incomingDamage + (hpRatio * 200.0);
    }

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

    private TurnAction fallbackAction(AITrainer aiTrainer) {
        List<Attack> attacks = aiTrainer.getCurrentBugemonAttackList();
        if (attacks.isEmpty()) {
            throw new IllegalStateException("AITrainer current bugemon has no available attack");
        }
        return new TurnAction.AttackAction(attacks.get(0));
    }

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

    private int bestAttackDamage(Bugemon attacker, Bugemon defender) {
        int best = 0;
        for (Attack attack : attacker.getAttackList()) {
            int damage = CombatService.calculateDamage(attack, attacker, defender, 1.0);
            if (damage > best) {
                best = damage;
            }
        }
        return best;
    }

    private int totalHp(List<Bugemon> team) {
        int total = 0;
        for (Bugemon bugemon : team) {
            total += Math.max(0, bugemon.getHp());
        }
        return total;
    }

    private int totalMaxHp(List<Bugemon> team) {
        int total = 0;
        for (Bugemon bugemon : team) {
            total += Math.max(1, bugemon.getMaxHp());
        }
        return total;
    }

    private int aliveCount(List<Bugemon> team) {
        int alive = 0;
        for (Bugemon bugemon : team) {
            if (bugemon.isAlive()) {
                alive++;
            }
        }
        return alive;
    }

    private double safeRatio(int value, int max) {
        if (max <= 0) {
            return 0.0;
        }
        return (double) value / max;
    }

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

    private boolean isValidAliveIndex(List<Bugemon> team, int idx) {
        return idx >= 0 && idx < team.size() && team.get(idx).isAlive();
    }

    private enum SimActionKind {
        ATTACK,
        SWITCH,
        ITEM,
        NONE
    }

    private record SimAction(SimActionKind kind, int index, Item item) {
        static SimAction attack(int attackIndex) {
            return new SimAction(SimActionKind.ATTACK, attackIndex, null);
        }

        static SimAction switchTo(int teamIndex) {
            return new SimAction(SimActionKind.SWITCH, teamIndex, null);
        }

        static SimAction useItem(Item item) {
            return new SimAction(SimActionKind.ITEM, -1, item);
        }

        static SimAction none() {
            return new SimAction(SimActionKind.NONE, -1, null);
        }
    }

    private static final class CombatState {
        private final List<Bugemon> aiTeam;
        private final List<Bugemon> opponentTeam;
        private final Map<Item, Integer> aiInventory;
        private final Map<Item, Integer> opponentInventory;

        private int aiCurrentIdx;
        private int opponentCurrentIdx;

        private CombatState(List<Bugemon> aiTeam, List<Bugemon> opponentTeam, int aiCurrentIdx, int opponentCurrentIdx,
                Map<Item, Integer> aiInventory, Map<Item, Integer> opponentInventory) {
            this.aiTeam = aiTeam;
            this.opponentTeam = opponentTeam;
            this.aiCurrentIdx = aiCurrentIdx;
            this.opponentCurrentIdx = opponentCurrentIdx;
            this.aiInventory = aiInventory;
            this.opponentInventory = opponentInventory;
        }

        static CombatState from(AITrainer aiTrainer, Trainer opponentTrainer) {
            if (opponentTrainer == null) {
                throw new IllegalStateException("MiniMax requires a known opponent trainer");
            }

            List<Bugemon> aiTeam = copyTeam(aiTrainer.getBugemons());
            List<Bugemon> oppTeam = copyTeam(opponentTrainer.getBugemons());

            int aiCurrentIdx = findCurrentIndex(aiTrainer.getCurrentBugemon(), aiTrainer.getBugemons());
            int oppCurrentIdx = findCurrentIndex(opponentTrainer.getCurrentBugemon(), opponentTrainer.getBugemons());

            Map<Item, Integer> aiInventory = new HashMap<>(aiTrainer.getInventoryMap());
            Map<Item, Integer> opponentInventory = extractInventoryMap(opponentTrainer);

            return new CombatState(aiTeam, oppTeam, aiCurrentIdx, oppCurrentIdx, aiInventory, opponentInventory);
        }

        CombatState copy() {
            return new CombatState(copyTeam(this.aiTeam), copyTeam(this.opponentTeam), this.aiCurrentIdx,
                    this.opponentCurrentIdx, new HashMap<>(this.aiInventory), new HashMap<>(this.opponentInventory));
        }

        Bugemon aiCurrent() {
            return this.aiTeam.get(this.aiCurrentIdx);
        }

        Bugemon opponentCurrent() {
            return this.opponentTeam.get(this.opponentCurrentIdx);
        }

        boolean isAiDefeated() {
            for (Bugemon bugemon : this.aiTeam) {
                if (bugemon.isAlive()) {
                    return false;
                }
            }
            return true;
        }

        boolean isOpponentDefeated() {
            for (Bugemon bugemon : this.opponentTeam) {
                if (bugemon.isAlive()) {
                    return false;
                }
            }
            return true;
        }

        private static List<Bugemon> copyTeam(List<Bugemon> originalTeam) {
            List<Bugemon> copy = new ArrayList<>();
            originalTeam.forEach(bugemon -> copy.add(new Bugemon(bugemon)));
            return copy;
        }

        private static int findCurrentIndex(Bugemon current, List<Bugemon> team) {
            int idx = team.indexOf(current);
            if (idx < 0) {
                throw new IllegalStateException("Current bugemon is not in its team");
            }
            return idx;
        }

        private static Map<Item, Integer> extractInventoryMap(Trainer trainer) {
            if (trainer instanceof AITrainer aiTrainer) {
                return new HashMap<>(aiTrainer.getInventoryMap());
            }
            if (trainer instanceof ManualTrainer manualTrainer) {
                return new HashMap<>(manualTrainer.getInventoryMap());
            }
            return new HashMap<>();
        }
    }
}
