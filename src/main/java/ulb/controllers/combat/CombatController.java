package ulb.controllers.combat;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatResult;
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
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.item.Item;
import ulb.models.player.PlayerInputHandler;
import ulb.models.player.PlayerState;
import ulb.models.run.RunTeam;
import ulb.models.skills.SkillContext;
import ulb.services.CombatService;
import ulb.services.SkillService;
import ulb.views.ViewLoader;
import ulb.views.combat.CombatView;

/**
 * Main controller for the combat screen. Integrates both the step-by-step animation logic and the manual player input
 * logic, replacing the old ManualCombatController. * It acts as the {@link CombatStrategy} for the player, intercepting
 * the request for actions/switches from the Combat model and opening the UI menus accordingly.
 */
public class CombatController extends Controller<CombatView>
        implements CombatView.Listener, CombatView.NextListener, PlayerInputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CombatController.class);

    private final CombatService combatService;
    private final SkillService skillService;

    private ActionCallback pendingActionCallback;
    private ActionCallback pendingSwitchCallback;

    private final Queue<TurnStep> pendingSteps = new ArrayDeque<>();

    private Combat combat;
    private PlayerState playerState;

    public CombatController(MetaController metaController, CombatService combatService, SkillService skillService,
            PlayerState playerState) {
        super(metaController, ViewLoader.load(CombatView::new));
        this.view.setListener(this);
        this.view.setNextListener(this);

        this.combatService = combatService;
        this.skillService = skillService;
        this.playerState = playerState;
    }

    /**
     * Builds and initializes a manual standalone combat; the opponent team is produced by {@code opponentFactory}.
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

    // TODO: remove code dup with extracting, resetting and calling callback

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
        if (this.pendingSwitchCallback != null) { // forced switch
            ActionCallback callback = this.pendingSwitchCallback;
            this.pendingSwitchCallback = null;
            callback.onActionChosen(new SwitchAction(bugemon));
        } else { // NOT forced call back
            this.resolvePlayerAction(new SwitchAction(bugemon));
        }
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

    /** Dispatch the resolved action back to the Combat model */
    private void resolvePlayerAction(TurnAction action) {
        if (this.pendingActionCallback != null) {
            ActionCallback cb = this.pendingActionCallback;
            this.pendingActionCallback = null;
            cb.onActionChosen(action);
        }
    }

    // ── Step Iteration and Animations ─────────────────────────────────────────

    private void advanceStep() {
        // TODO: add back animation

        if (!this.pendingSteps.isEmpty()) {
            TurnStep step = this.pendingSteps.poll();
            LOG.debug("Advancing step: {}", step);
            this.view.showStepDialog(step);
            this.refreshHpForStep(step);
        } else {
            this.view.hideDialog();
            this.processEndOfTurn();
        }
    }

    private void refreshHpForStep(TurnStep step) {
        switch (step) {
            case TurnStep.AttackStep atk -> this.view.updateHp(atk.defender(), atk.defenderHpAfter());
            case KoStep(CombatBugemon koBugemon) -> this.view.updateHp(koBugemon, 0);
            case TurnStep.SwitchStep sw -> this.view.switchBugemon(sw);
            case HealBugemonStep(CombatBugemon healedBugemon) ->
                this.view.updateHp(healedBugemon, healedBugemon.getCurrentHp());
            default -> {
                /* ItemStep, HealTeamStep : pas de changement de PV individuel */ }
        }
    }

    private void processEndOfTurn() {
        if (this.combat.isFinished()) {
            this.onCombatFinished();
        } else {
            this.view.hideDialog();
            this.startTurn();
        }
    }

    private void onCombatFinished() {
        boolean won = this.combat.getResult() == CombatResult.VICTORY;
        LOG.info("Combat ended. Victory: {}", won);

        this.combatService.finalizeCombat(this.combat, this.combat.getPlayerSkillContext());
        this.metaController.onCombatFinished(won);
    }

    @Override
    public void requestActionChoice(CombatContext context, ActionCallback callback) {
        this.pendingActionCallback = callback;
        this.view.showMainActionMenu();
    }

    @Override
    public void requestSwitchChoice(CombatContext context, ActionCallback callback) {
        this.pendingSwitchCallback = callback;
        this.view.showSwitchMenu(context.allyTeam().getAvailable(), context.allyTeam().getActive().isKo());
    }
}
