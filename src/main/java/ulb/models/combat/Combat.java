package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.DamageResult;
import ulb.models.bugemon.Attack;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;
import ulb.models.combat.turn.TurnActionVisitor;
import ulb.models.combat.turn.TurnActionsReadyCallback;
import ulb.models.combat.turn.TurnResolvedCallback;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.ItemStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.skills.SkillContext;

/**
 * Drives a single turn-based combat session between a player team and an opponent team.
 *
 * <p>
 * A combat proceeds through repeated cycles of action request and turn resolution. Each turn, both sides choose a
 * {@link ulb.models.combat.turn.TurnAction} via their respective {@link ulb.models.combat.strategy.CombatStrategy};
 * {@link #resolveTurn} then executes those actions in initiative order and produces a sequence of
 * {@link ulb.models.combat.turn.TurnStep} events for the controller to animate.
 *
 * <p>
 * The combat ends when one team is fully KO'd or the player forfeits, at which point {@link #getResult()} returns
 * {@link CombatResult#VICTORY} or {@link CombatResult#DEFEAT}. Calling {@link #getResult()} before the combat is
 * finished throws {@link IllegalStateException}.
 */
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

    /**
     * Constructs a {@code Combat} from a fully configured {@link CombatBuilder}.
     *
     * <p>
     * Skill-derived stat effects are applied to every living player Bugemon immediately upon construction.
     *
     * @param builder
     *            the builder supplying all mandatory combat parameters
     */
    public Combat(CombatBuilder builder) {
        this.playerTeam = builder.getPlayerTeam();
        this.opponentTeam = builder.getOpponentTeam();

        this.floor = builder.getFloor();
        this.bossMode = builder.isBossMode();

        this.playerInventory = builder.getPlayerInventory();
        this.opponentInventory = builder.getOpponentInventory();

        this.playerStrategy = builder.getPlayerStrategy();
        this.opponentStrategy = builder.getOpponentStrategy();

        this.damageCalculator = builder.getDamageCalculator();
        this.effectProcessor = builder.getEffectProcessor();
        this.playerSkillContext = builder.getPlayerSkillContext();

        this.applyStatSkills(this.playerSkillContext);

        this.result = null;
        this.finished = false;
    }

    private void applyStatSkills(SkillContext context) {
        for (CombatBugemon bugemon : this.playerTeam.getAlive()) {
            for (StatusEffect effect : context.getStatEffects()) {
                bugemon.addEffect(effect);
            }
        }
    }

    private CombatContext makePlayerContext() {
        return new CombatContext(this.playerTeam, this.opponentTeam, this.playerInventory, this.opponentInventory);
    }

    private CombatContext makeOpponentContext() {
        return new CombatContext(this.opponentTeam, this.playerTeam, this.opponentInventory, this.playerInventory);
    }

    /**
     * Asks both strategies to choose their action for the upcoming turn, then notifies {@code callback} once both
     * choices are available.
     *
     * <p>
     * The two strategies may respond synchronously or asynchronously; the callback is guaranteed to be invoked exactly
     * once, after both actions are ready.
     *
     * @param callback
     *            receives both actions when the turn is ready to be resolved
     */
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

    /**
     * Resolves both actions for the current turn in initiative order, producing the resulting sequence of
     * {@link TurnStep} events.
     *
     * <p>
     * The method handles early exits: if the combat ends after the first action, or if the second actor is KO'd before
     * it can act, the callback is invoked immediately with the partial step list. End-of-turn effect ticks are applied
     * before every callback invocation.
     *
     * @param playerAction
     *            the action chosen by the player
     * @param opponentAction
     *            the action chosen by the opponent
     * @param callback
     *            receives the complete (or partial) list of turn steps
     */
    public void resolveTurn(TurnAction playerAction, TurnAction opponentAction, TurnResolvedCallback callback) {
        List<TurnStep> steps = new ArrayList<>();

        List<TurnAction> actions = this.computeActionOrder(playerAction, opponentAction);

        // use == to avoid edge case in which both player and opponent choose the same
        // action.
        boolean firstIsPlayer = actions.get(0) == playerAction;
        boolean secondIsPlayer = !firstIsPlayer;

        // retrieve the bugemon corresponding to the second action to later check if it died from the first
        // action.
        CombatTeam secondTeam = this.teamFor(!firstIsPlayer);
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

        // handle potential Ko of second actor. Controller will request forced switch
        // after displaying steps
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

        CombatTeam firstTeam = this.teamFor(firstIsPlayer);
        CombatBugemon firstActor = firstTeam.getActive();

        // handle potential Ko of first actor. Controller will request forced switch
        // after displaying steps
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

    private CombatTeam teamFor(boolean isPlayer) {
        return isPlayer ? this.playerTeam : this.opponentTeam;
    }

    /**
     * Asks the relevant strategy to choose a replacement Bugemon after the active one is KO'd mid-turn, then resolves
     * and returns the resulting switch steps.
     *
     * @param isPlayer
     *            {@code true} to force a switch for the player, {@code false} for the opponent
     * @param onDone
     *            receives the switch step(s) once the choice is made
     */
    public void requestForcedSwitch(boolean isPlayer, TurnResolvedCallback onDone) {
        CombatStrategy strategy = isPlayer ? this.playerStrategy : this.opponentStrategy;
        CombatContext ctx = isPlayer ? this.makePlayerContext() : this.makeOpponentContext();
        strategy.chooseSwitch(ctx, action -> onDone.onTurnResolved(this.resolveAction(action, isPlayer)));
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
                return Combat.this.playerInventory.useItem(itemAction.item()).map(item -> {
                    List<TurnStep> steps = new ArrayList<>();
                    steps.add(new ItemStep());
                    steps.addAll(Combat.this.effectProcessor.applySingleEffect(item.effect(), actor,
                            opposingTeam.getActive(), actingTeam));
                    return steps;
                }).orElse(new ArrayList<>());
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
        int playerInitiative = this.playerTeam.getActive().getEffectiveInitiative();
        int opponentInitiative = this.opponentTeam.getActive().getEffectiveInitiative();

        return (playerInitiative >= opponentInitiative) ? List.of(playerAction, opponentAction)
                : List.of(opponentAction, playerAction);
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

    /**
     * Advances all active status effects on {@code bugemon} by one tick if it is not KO. Expired effects are removed by
     * {@link CombatBugemon#tickEffects()}.
     *
     * @param bugemon
     *            the Bugemon whose effects should be ticked
     */
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

    /**
     * Returns the outcome of the combat.
     *
     * @return {@link CombatResult#VICTORY} if the opponent team was defeated, {@link CombatResult#DEFEAT} if the player
     *         team was defeated or the player forfeited
     * @throws IllegalStateException
     *             if the combat has not yet finished
     */
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
