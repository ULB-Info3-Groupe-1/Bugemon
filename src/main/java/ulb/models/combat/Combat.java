package ulb.models.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.DamageResult;
import ulb.models.bugemon.Attack;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;
import ulb.models.combat.turn.TurnActionVisitor;
import ulb.models.combat.turn.TurnActionsReadyCallback;
import ulb.models.combat.turn.TurnPhase;
import ulb.models.combat.turn.TurnResolvedCallback;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.skills.SkillContext;

public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);
    private static final String STR_PLAYER = "player";
    private static final String STR_OPPONENT = "opponent";

    private final CombatTeam playerTeam;
    private final CombatTeam opponentTeam;

    private final Inventory playerInventory;
    private final Inventory opponentInventory;

    private final CombatStrategy playerStrategy;
    private final CombatStrategy opponentStrategy;

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;
    private final SkillContext playerSkillContext;

    private final int floor;
    private final boolean bossMode;

    private CombatResult result;
    private boolean finished;

    public static class Builder {
        private CombatTeam playerTeam;
        private CombatTeam opponentTeam;
        private int floor;
        private boolean bossMode;
        private Inventory playerInventory;
        private Inventory opponentInventory;
        private CombatStrategy playerStrategy;
        private CombatStrategy opponentStrategy;
        private DamageCalculator damageCalculator;
        private EffectProcessor effectProcessor;
        private SkillContext playerSkillContext;

        public Builder playerTeam(CombatTeam team) {
            this.playerTeam = team;
            return this;
        }

        public Builder opponentTeam(CombatTeam team) {
            this.opponentTeam = team;
            return this;
        }

        public Builder floor(int currentFloor) {
            this.floor = currentFloor;
            return this;
        }

        public Builder bossMode(boolean mode) {
            this.bossMode = mode;
            return this;
        }

        public Builder playerInventory(Inventory inventory) {
            this.playerInventory = inventory;
            return this;
        }

        public Builder opponentInventory(Inventory inventory) {
            this.opponentInventory = inventory;
            return this;
        }

        public Builder playerStrategy(CombatStrategy strategy) {
            this.playerStrategy = strategy;
            return this;
        }

        public Builder opponentStrategy(CombatStrategy strategy) {
            this.opponentStrategy = strategy;
            return this;
        }

        public Builder damageCalculator(DamageCalculator calculator) {
            this.damageCalculator = calculator;
            return this;
        }

        public Builder effectProcessor(EffectProcessor processor) {
            this.effectProcessor = processor;
            return this;
        }

        public Builder playerSkillContext(SkillContext skillContext) {
            this.playerSkillContext = skillContext;
            return this;
        }

        public Combat build() {
            Objects.requireNonNull(this.playerTeam, "Player team cannot be null");
            Objects.requireNonNull(this.opponentTeam, "Opponent team cannot be null");
            Objects.requireNonNull(this.playerInventory, "Player inventory cannot be null");
            Objects.requireNonNull(this.opponentInventory, "Opponent inventory cannot be null");
            Objects.requireNonNull(this.playerStrategy, "Player strategy cannot be null");
            Objects.requireNonNull(this.opponentStrategy, "Opponent strategy cannot be null");
            Objects.requireNonNull(this.damageCalculator, "Damage calculator cannot be null");
            Objects.requireNonNull(this.effectProcessor, "Effect processor cannot be null");
            Objects.requireNonNull(this.floor, "Floor cannot be null"); // TODO: false if we don't play Tower ?
            Objects.requireNonNull(this.bossMode, "Boss mode cannot be null");

            if (this.playerSkillContext == null) {
                this.playerSkillContext = SkillContext.NONE;
            }

            return new Combat(this);
        }
    }

    private Combat(Builder builder) {
        this.playerTeam = builder.playerTeam;
        this.opponentTeam = builder.opponentTeam;

        this.floor = builder.floor;
        this.bossMode = builder.bossMode;

        this.playerInventory = builder.playerInventory;
        this.opponentInventory = builder.opponentInventory;

        this.playerStrategy = builder.playerStrategy;
        this.opponentStrategy = builder.opponentStrategy;

        this.damageCalculator = builder.damageCalculator;
        this.effectProcessor = builder.effectProcessor;
        this.playerSkillContext = builder.playerSkillContext;

        this.result = null;
        this.finished = false;
    }

    private CombatContext makePlayerContext() {
        return new CombatContext(this.playerTeam, this.opponentTeam, this.playerInventory, this.opponentInventory);
    }

    private CombatContext makeOpponentContext() {
        return new CombatContext(this.opponentTeam, this.playerTeam, this.opponentInventory, this.playerInventory);
    }

    public void requestActions(TurnActionsReadyCallback callback) {
        // magic stuff to call onBothActionsReady only once both actions are ready

        // Single element arrays because of an odd java rule with local variables and
        // enclosing scopes
        TurnAction[] playerAction = new TurnAction[1];
        TurnAction[] opponentAction = new TurnAction[1];

        ActionCallback playerCb = action -> {
            playerAction[0] = action;
            if (opponentAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };
        ActionCallback opponentCb = action -> {
            opponentAction[0] = action;
            if (playerAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };

        LOG.debug("Requesting actions from both strategies");
        this.playerStrategy.chooseAction(this.makePlayerContext(), playerCb);
        this.opponentStrategy.chooseAction(this.makeOpponentContext(), opponentCb);
    }

    public void resolveTurn(TurnAction playerAction, TurnAction opponentAction, TurnResolvedCallback callback) {
        List<TurnStep> steps = new ArrayList<>();

        List<TurnAction> actions = this.computeActionOrder(playerAction, opponentAction);

        // use == to avoid edge case in which both player and opponent choose the same
        // action.
        boolean firstIsPlayer = actions.get(0) == playerAction;
        boolean secondIsPlayer = !firstIsPlayer;

        // TODO: fix code dup with second team/actor
        // retrieve the bugemon corresponding to the second action
        // to later check if it died from the first action.
        CombatTeam secondTeam = firstIsPlayer ? this.opponentTeam : this.playerTeam;
        CombatBugemon secondActorBefore = secondTeam.getActive();

        LOG.debug("Resolving turn: first={}", firstIsPlayer ? STR_PLAYER : STR_OPPONENT);

        // resolve first action
        steps.addAll(this.resolveAction(actions.get(0), firstIsPlayer));

        // handle potential combat end
        if (this.checkCombatFinished()) {
            LOG.info("Combat ended after first action: result={}", this.result);
            callback.onTurnResolved(steps);
            return;
        }

        // handle potential Ko of second actor — controller will request forced switch after displaying steps
        if (secondActorBefore.isKo()) {
            LOG.debug("Second actor KO - returning steps, controller will handle forced switch for {}",
                    secondIsPlayer ? STR_PLAYER : STR_OPPONENT);
            this.tickEndOfTurn();
            callback.onTurnResolved(steps);
            return;
        }

        // resolve second action
        steps.addAll(this.resolveAction(actions.get(1), secondIsPlayer));

        // handle potential combat end
        if (this.checkCombatFinished()) {
            LOG.info("Combat ended after second action: result={}", this.result);
            this.tickEndOfTurn();
            callback.onTurnResolved(steps);
            return;
        }

        CombatTeam firstTeam = firstIsPlayer ? this.playerTeam : this.opponentTeam;
        CombatBugemon firstActor = firstTeam.getActive();

        // handle potential Ko of first actor — controller will request forced switch after displaying steps
        if (firstActor.isKo()) {
            LOG.debug("First actor KO - returning steps, controller will handle forced switch for {}",
                    firstIsPlayer ? STR_PLAYER : STR_OPPONENT);
            this.tickEndOfTurn();
            callback.onTurnResolved(steps);
            return;
        }

        this.tickEndOfTurn();

        LOG.debug("Turn resolved normally with {} steps", steps.size());
        callback.onTurnResolved(steps);
    }

    public List<TurnStep> applyForcedPlayerSwitch(CombatBugemon bugemon) {
        return this.resolveAction(new SwitchAction(bugemon), true);
    }

    @SuppressWarnings("unchecked")
    public List<TurnStep> applyForcedOpponentSwitch() {
        List<TurnStep>[] steps = new List[1];
        this.opponentStrategy.chooseSwitch(this.makeOpponentContext(),
                action -> steps[0] = this.resolveAction(action, false));
        return steps[0];
    }

    private List<TurnStep> resolveAction(TurnAction action, boolean isPlayer) {
        CombatTeam actingTeam = isPlayer ? this.playerTeam : this.opponentTeam;
        CombatTeam opposingTeam = isPlayer ? this.opponentTeam : this.playerTeam;
        CombatBugemon actor = actingTeam.getActive();

        return action.accept(new TurnActionVisitor() {
            public List<TurnStep> visit(AttackAction attackAction) {
                if (actor.isKo()) {
                    return List.of();
                }
                return Combat.this.resolveAttack(actor, opposingTeam.getActive(), attackAction.attack(), actingTeam);
            }

            public List<TurnStep> visit(SwitchAction switchAction) {
                actingTeam.setActive(switchAction.target());
                return List.of(new SwitchStep(switchAction.target(), isPlayer));
            }

            public List<TurnStep> visit(ItemAction itemAction) {
                if (actor.isKo()) {
                    return List.of();
                }
                return Combat.this.playerInventory
                        .useItem(itemAction.item()).map(item -> Combat.this.effectProcessor
                                .applySingleEffect(item.effect(), actor, opposingTeam.getActive(), actingTeam))
                        .orElse(new ArrayList<>());
            }

            public List<TurnStep> visit(ForfeitAction a) {
                Combat.this.result = CombatResult.DEFEAT;
                Combat.this.finished = true;
                return List.of();
            }
        });
    }

    private List<TurnStep> resolveAttack(CombatBugemon attacker, CombatBugemon defender, Attack attack,
            CombatTeam attackerTeam) {
        if (!attacker.hasAttack(attack)) {
            throw new IllegalArgumentException(
                    String.format("%s attempted to use attack %s, but does not have this attack.", attacker, attack));
        }

        List<TurnStep> steps = new ArrayList<>();

        SkillContext ctx = (attackerTeam == this.playerTeam) ? this.playerSkillContext : SkillContext.NONE;
        DamageResult damageResult = this.damageCalculator.calculateDamage(attacker, defender, attack, ctx);

        defender.takeDamage(damageResult.damage());
        LOG.debug("{} uses {} on {} for {} damage (HP left: {})", attacker, attack.name(), defender, damageResult,
                defender.getCurrentHp());
        steps.add(new AttackStep(attacker, defender, attack, damageResult, defender.getCurrentHp()));

        steps.addAll(this.effectProcessor.applyEffects(attack, attacker, defender, attackerTeam));

        if (defender.isKo()) {
            LOG.info("{} is KO", defender);
            steps.add(new KoStep(defender));
        }

        return steps;
    }

    private List<TurnAction> computeActionOrder(TurnAction playerAction, TurnAction opponentAction) {
        // two attacks -> order by prio
        if (playerAction.phase().equals(TurnPhase.ATTACK) && opponentAction.phase().equals(TurnPhase.ATTACK)) {
            int playerInitiative = this.playerTeam.getActive().getEffectiveInitiative();
            int opponentInitiative = this.opponentTeam.getActive().getEffectiveInitiative();

            return (playerInitiative >= opponentInitiative) ? List.of(playerAction, opponentAction)
                    : List.of(opponentAction, playerAction);
        } else /* order by phases priority */ {
            List<TurnAction> actions = new ArrayList<>();
            actions.add(playerAction);
            actions.add(opponentAction);
            Collections.sort(actions);
            return actions;
        }
    }

    private boolean checkCombatFinished() {
        if (this.isFinished()) { // triggered by forfeit action
            return true;
        }
        if (this.playerTeam.isDefeated()) {
            this.result = CombatResult.DEFEAT;
            this.finished = true;
            return true;
        }
        if (this.opponentTeam.isDefeated()) {
            this.result = CombatResult.VICTORY;
            this.finished = true;
            return true;
        }

        return false;
    }

    void tickEndOfTurn(CombatBugemon bugemon) {
        if (!bugemon.isKo()) {
            bugemon.tickEffects();
        }
    }

    private void tickEndOfTurn() {
        this.tickEndOfTurn(this.playerTeam.getActive());
        this.tickEndOfTurn(this.opponentTeam.getActive());
    }

    public SkillContext getPlayerSkillContext() {
        return this.playerSkillContext;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public CombatResult getResult() {
        if (!this.finished) {
            throw new IllegalStateException("Combat not finished yet");
        }
        return this.result;
    }

    public CombatTeam getPlayerTeam() {
        return this.playerTeam;
    }

    public CombatBugemon getActivePlayerBugemon() {
        return this.playerTeam.getActive();
    }

    public CombatBugemon getActiveOpponentBugemon() {
        return this.opponentTeam.getActive();
    }

    public CombatTeam getOpponentTeam() {
        return this.opponentTeam;
    }

    public Map<Item, Integer> getPlayerInventory() {
        return this.playerInventory.getMap();
    }

    public int getOpponentTeamSize() {
        return this.opponentTeam.size();
    }

    public int getFloor() {
        return this.floor;
    }

    public boolean isBossMode() {
        return this.bossMode;
    }
}
