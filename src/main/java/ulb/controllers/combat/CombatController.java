package ulb.controllers.combat;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.bugemon.Attack;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatResult;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.item.Item;
import ulb.services.CombatService;
import ulb.services.PlayerInputHandler;
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
    private Combat combat;
    private ActionCallback pendingActionCallback;
    private ActionCallback pendingSwitchCallback;
    private final Queue<TurnStep> pendingSteps = new ArrayDeque<>();
    private final Random random = new Random();

    public CombatController(MetaController metaController, CombatService combatService) {
        super(metaController, ViewLoader.load(CombatView::new));
        this.view.setListener(this);
        this.view.setNextListener(this);

        this.combatService = combatService;
    }

    /**
     * Initializes a new combat session. 
     * @param combat the new Combat model instance
     */
    public void initialize(Combat combat) {
        this.combat = combat;

        // TODO: demeter
        this.view.displayBugemons(this.combat.getPlayerTeam().getActive(), combat.getOpponentTeam().getActive());
        this.view.refresh();

        this.startTurnPhase();
    }

    // ── Phase Flow ────────────────────────────────────────────────────────────

    private void startTurnPhase() {
        if (this.combat.isFinished()) {
            this.onCombatEnded();
            return;
        }

        this.combat.requestActions((playerAction, opponentAction) -> {
            this.combat.resolveTurn(playerAction, opponentAction, steps -> {
                this.pendingSteps = steps.iterator();
                this.advanceStep();
            });
        });
    }

    // ── CombatStrategy Implementation ─────────────────────────────────────────

    @Override
    public void chooseAction(CombatContext ctx, ActionCallback cb) {
        this.view.setModel(ctx.allyTeam(), ctx.opponentTeam(), this.playerInventory);
        this.view.refresh();

        this.pendingActionCallback = cb;
        this.view.refreshMenuState();
    }

    @Override
    public void chooseSwitch(CombatContext ctx, ActionCallback cb) {
        this.pendingActionCallback = cb;
        this.view.refreshMenuState();
    }

    // ── View Listener (Player Input) ──────────────────────────────────────────

    @Override
    public void onAttack(Attack attack) {
        this.resolvePlayerAction(new AttackAction(attack));
    }

    @Override
    public void onSwitch(CombatBugemon bugemon) {
        this.resolvePlayerAction(new SwitchAction(bugemon));
    }

    @Override
    public void onItemSelected(Item item) {
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
        if (!this.pendingSteps.hasNext()) {
            this.onStepsExhausted();
            return;
        }

        TurnStep step = this.pendingSteps.next();
        LOG.debug("Advancing step: {}", step);

        Runnable animationCallback = switch (step) {
            case AttackStep a -> () -> {
                boolean isPlayerAttacking = a.attacker() == this.combat.getPlayerTeam().getActive();
                if (isPlayerAttacking) {
                    this.view.playTrainerAttackAnimation(() -> {
                        this.view.updateOpponentInfo(a.defender());
                        this.view.showStepDialog(step);
                    });
                } else {
                    this.view.playOpponentAttackAnimation(() -> {
                        this.view.updateTrainerInfo(a.defender());
                        this.view.showStepDialog(step);
                    });
                }
            };

            case SwitchStep s -> () -> {
                if (s.isPlayer()) {
                    this.view.updateTrainerBugemon(s.bugemon());
                } else {
                    this.view.updateOpponentBugemon(s.bugemon());
                }
                this.view.showStepDialog(step);
            };

            case KoStep k -> () -> {
                boolean isPlayerKo = k.koBugemon() == this.combat.getPlayerTeam().getActive();
                if (isPlayerKo) {
                    this.view.playDeathAnimationForTrainer(() -> {
                        this.view.showStepDialog(step);
                    });
                } else {
                    this.view.playDeathAnimationForOpponent(() -> {
                        this.view.showStepDialog(step);
                    });
                }
            };

            default -> () -> this.view.showStepDialog(step);
        };

        this.view.lockNextButton();
        animationCallback.run();
    }

    @Override
    public void onNext() {
        this.advanceStep();
    }

    private void onStepsExhausted() {
        if (this.combat.isFinished()) {
            this.onCombatEnded();
        } else {
            this.view.hideDialog();
            this.startTurnPhase();
        }
    }

    private void onCombatEnded() {
        boolean won = this.combat.getResult() == CombatResult.VICTORY;
        LOG.info("Combat ended. Victory: {}", won);
        this.metaController.onCombatFinished(won);
    }

    @Override
    public void requestActionChoice(CombatContext context, ActionCallback callback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestActionChoice'");
    }

    @Override
    public void requestSwitchChoice(CombatContext context, ActionCallback callback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestSwitchChoice'");
    }
}
