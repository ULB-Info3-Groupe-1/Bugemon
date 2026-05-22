package ulb.controllers.combat;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.CombatSummary;
import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatResult;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.Efficiency;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.HealBugemonStep;
import ulb.models.combat.turn.TurnStep.HealTeamStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.item.Item;
import ulb.models.player.PlayerInputHandler;
import ulb.models.player.PlayerState;
import ulb.models.run.RunTeam;
import ulb.models.skills.SkillContext;
import ulb.services.CombatService;
import ulb.services.SaveService;
import ulb.services.SkillService;
import ulb.views.ViewLoader;
import ulb.views.combat.CombatView;

/**
 * Main controller for the combat screen. Integrates step-by-step turn animation with manual player input.
 *
 * <p>
 * Implements {@link PlayerInputHandler} so that it acts as the {@link CombatStrategy} for the player side: the
 * {@link ulb.models.combat.Combat} model calls back into this controller when it needs an action or a forced switch, at
 * which point the appropriate UI menu is opened. Once the player responds, the chosen {@link TurnAction} is dispatched
 * back to the model to resolve the turn.
 *
 * <p>
 * Turn resolution produces a list of {@link TurnStep} events that are queued and replayed one at a time; each step
 * waits for a "Next" click before advancing.
 */
public class CombatController extends Controller<CombatView>
        implements CombatView.Listener, CombatView.NextListener, PlayerInputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CombatController.class);

    private final CombatService combatService;
    private final SkillService skillService;
    private final SaveService saveService;

    private ActionCallback pendingActionCallback;
    private ActionCallback pendingSwitchCallback;

    private final Queue<TurnStep> pendingSteps = new ArrayDeque<>();

    private Combat combat;
    private PlayerState playerState;

    public CombatController(MetaController metaController, CombatService combatService, SkillService skillService,
            SaveService saveService, PlayerState playerState) {
        super(metaController, ViewLoader.load(CombatView::new));
        this.view.setListener(this);
        this.view.setNextListener(this);

        this.combatService = combatService;
        this.skillService = skillService;
        this.saveService = saveService;
        this.playerState = playerState;
    }

    /**
     * Builds and starts a new combat session via the supplied factory.
     *
     * @param playerRunTeam
     *            the player's team for this run
     * @param combatFactory
     *            factory that assembles the {@link ulb.models.combat.Combat} instance
     * @param availableBugemons
     *            pool of static Bugemon data passed to the factory for opponent generation
     */
    public void startCombat(RunTeam playerRunTeam, CombatFactory combatFactory, List<Bugemon> availableBugemons) {
        SkillContext skillContext = this.skillService.buildSkillContext(this.playerState.getSkillTreeState());
        this.initialize(
                combatFactory.create(playerRunTeam, this.playerState.getInventory(), skillContext, availableBugemons));
    }

    /**
     * Initializes a new combat session.
     *
     * @param combat
     *            the new Combat model instance
     */
    public void initialize(Combat newCombat) {
        this.combat = newCombat;
        this.pendingSteps.clear();
        this.pendingActionCallback = null;
        this.pendingSwitchCallback = null;

        this.view.displayBugemons(this.combat.getActivePlayerBugemon(), this.combat.getActiveOpponentBugemon());
        this.view.refresh();

        this.startTurn();
    }

    private void startTurn() {
        this.combat.requestActions(this::onBothActionsReady);
    }

    private void onBothActionsReady(TurnAction playerAction, TurnAction opponentAction) {
        this.combat.resolveTurn(playerAction, opponentAction, steps -> {
            this.pendingSteps.addAll(steps);
            this.advanceStep();
        });
    }

    @Override
    public void onNext() {
        this.advanceStep();
    }

    // ── View Listener (Player Input) ──────────────────────────────────────────

    @Override
    public void onAttack() {
        this.view.showAttackMenu(this.combat.getPlayerTeam().getActive().getAttacks());
    }

    @Override
    public void onAttackHovered(Attack attack) {
        Efficiency efficiency = this.combatService.previewEfficiency(attack, this.combat.getOpponentTeam().getActive());
        this.view.showAttackPreview(attack, efficiency);
    }

    @Override
    public void onAttackChosen(Attack attack) {
        this.resolvePlayerAction(new AttackAction(attack));
    }

    @Override
    public void onSwitch() {
        this.view.showSwitchMenu(this.combat.getPlayerTeam().getAvailable(), false);
    }

    @Override
    public void onSwitchChosen(CombatBugemon bugemon) {
        this.resolveSwitchAction(new SwitchAction(bugemon));
    }

    @Override
    public void onInventory() {
        this.view.showInventory(this.combat.getPlayerInventory());
    }

    @Override
    public void onItemHovered(Item item) {
        this.view.showItemPreview(item);
    }

    @Override
    public void onItemChosen(Item item) {
        this.resolvePlayerAction(new ItemAction(item));
    }

    @Override
    public void onForfeit() {
        this.resolvePlayerAction(new ForfeitAction());
    }

    /**
     * Dispatches the resolved player action back to the {@link ulb.models.combat.Combat} model via the stored callback.
     * Clears the pending callback before invoking it to prevent double-invocation.
     */
    private void resolvePlayerAction(TurnAction action) {
        if (this.pendingActionCallback != null) {
            ActionCallback cb = this.pendingActionCallback;
            this.pendingActionCallback = null;
            cb.onActionChosen(action);
        }
    }

    /**
     * Dispatch a resolved switch action taking into account a pending switch callback. If there is no pending switch
     * callback the action is forwarded to the regular player action resolver.
     */
    private void resolveSwitchAction(TurnAction action) {
        if (this.pendingSwitchCallback != null) {
            ActionCallback cb = this.pendingSwitchCallback;
            this.pendingSwitchCallback = null;
            cb.onActionChosen(action);
        } else {
            this.resolvePlayerAction(action);
        }
    }

    // ── Step Iteration ─────────────────────────────────────────

    /**
     * Pops the next {@link TurnStep} from the queue and shows it, or finalises the turn if the queue is empty.
     */
    private void advanceStep() {
        if (!this.pendingSteps.isEmpty()) {
            TurnStep step = this.pendingSteps.poll();
            LOG.debug("Advancing step: {}", step);
            this.view.showStep(step);
            this.refreshHpForStep(step);
        } else {
            this.view.hideDialog();
            this.processEndOfTurn();
        }
    }

    /**
     * Updates the HP display in the view for the step that was just shown, if that step mutates HP.
     *
     * @param step
     *            the step whose HP changes are to be reflected in the view
     */
    private void refreshHpForStep(TurnStep step) {
        switch (step) {
            case TurnStep.AttackStep atk -> this.view.updateHp(atk.defender(), atk.defenderHpAfter());
            case KoStep(CombatBugemon koBugemon) -> this.view.updateHp(koBugemon, 0);
            case TurnStep.SwitchStep sw -> this.view.switchBugemon(sw);
            case HealBugemonStep(CombatBugemon healedBugemon, int hpAfterHeal) ->
                this.view.updateHp(healedBugemon, hpAfterHeal);
            case HealTeamStep(CombatTeam healedTeam, int activeHpAfterHeal) ->
                this.view.updateHp(healedTeam.getActive(), activeHpAfterHeal);
            default -> {
                /* ItemStep */ }
        }
    }

    /**
     * Checks the post-turn state and either requests the next turn, triggers a forced switch for a KO'd Bugemon, or
     * finalises the combat.
     */
    private void processEndOfTurn() {
        if (this.combat.isFinished()) {
            this.onCombatFinished();
        } else if (this.combat.getActivePlayerBugemon().isKo()) {
            this.combat.requestForcedSwitch(true, this::onForcedSwitchResolved);
        } else if (this.combat.getActiveOpponentBugemon().isKo()) {
            this.combat.requestForcedSwitch(false, this::onForcedSwitchResolved);
        } else {
            this.startTurn();
        }
    }

    /**
     * Finalises XP, persists the player state, and delegates to
     * {@link ulb.controllers.MetaController#onCombatFinished(boolean, CombatSummary)}.
     */
    private void onCombatFinished() {
        boolean won = this.combat.getResult() == CombatResult.VICTORY;
        LOG.info("Combat ended. Victory: {}", won);

        CombatSummary summary = this.combatService.finalizeCombat(this.combat, this.combat.getPlayerSkillContext());
        this.saveService.save(this.playerState);
        this.metaController.onCombatFinished(won, summary);
    }

    @Override
    public void requestActionChoice(CombatContext context, ActionCallback callback) {
        this.pendingActionCallback = callback;
        this.view.showMainActionMenu();
    }

    @Override
    public void requestSwitchChoice(CombatContext context, ActionCallback callback) {
        this.pendingSwitchCallback = callback;
        this.view.showSwitchMenu(context.allyTeam().getAvailable(), true);
    }

    /**
     * Callback invoked when a forced switch (KO replacement) has been resolved. Adds the resulting steps to the queue
     * and advances.
     */
    private void onForcedSwitchResolved(List<TurnStep> steps) {
        this.pendingSteps.addAll(steps);
        this.advanceStep();
    }
}
