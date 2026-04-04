package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.ViewLoader;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller for the automatic combat screen.
 *
 * Drives the {@link Combat} loop one action at a time, gated by the player's "Next" clicks. Each action triggers its
 * animation and dialog before the next one is unlocked; implements {@link AutomaticCombatView.Listener} to receive
 * those events.
 */
public class AutomaticCombatController extends CombatController<AutomaticCombatView>
        implements AutomaticCombatView.Listener {

    private Combat combat;
    private AutoTrainer playerTrainer;

    /** The turn result currently awaiting full display; {@code null} when idle. */
    private TurnResult pendingResult;

    /** {@code true} once the second action of {@link #pendingResult} has been shown. */
    private boolean secondShown;

    /**
     * Constructs an {@code AutomaticCombatController} and initialises its {@link AutomaticCombatView}.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public AutomaticCombatController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) throws IOException {
        super(metaController, playerService, bugemonService, ViewLoader.load(AutomaticCombatView::new));
        this.view.setListener(this);
    }

    @Override
    public void startCombat(boolean shouldRestoreHp) {
        this.restoreHpAfterCombat = shouldRestoreHp;
        this.pendingResult = null;
        this.secondShown = false;

        this.playerTrainer = new AutoTrainer(this.playerService.getActiveTeam());
        AutoTrainer opponentTrainer = this.createRandomOpponent(this.playerTrainer.getTeamSize());
        this.combat = new Combat(this.playerTrainer, opponentTrainer);

        this.view.setModel(this.playerTrainer, opponentTrainer);
        this.view.refresh();
        this.runNextTurn();
    }

    @Override
    public void onNext() {
        if (this.pendingResult == null) {
            return;
        }

        boolean hasSecond = this.pendingResult.second().isPresent()
                && this.pendingResult.second().orElseThrow().wasAttack();

        if (hasSecond && !this.secondShown) {
            TurnResult.AttackResult second = this.pendingResult.second().orElseThrow();
            this.animationController.playSecondAction(this.pendingResult, this.playerTrainer, () -> {
                this.view.refresh();
                this.view.showSecondAttackResult(second);
            });
            this.secondShown = true;
            return;
        }

        this.pendingResult = null;
        this.secondShown = false;

        if (this.combat.getWinner().isPresent()) {
            this.handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
            return;
        }

        this.runNextTurn();
    }

    private void runNextTurn() {
        TurnResult result = this.combat.turn();
        this.pendingResult = result;
        this.secondShown = false;

        this.animationController.playFirstAction(result, this.playerTrainer, () -> {
            this.view.refresh();

            if (!result.first().wasAttack()) {
                if (this.combat.getWinner().isPresent()) {
                    this.handleCombatResult(this.combat.getWinner().orElseThrow(), this.playerTrainer);
                } else {
                    this.runNextTurn();
                }
                return;
            }

            this.view.showFirstAttackResult(result.first());
        });
    }
}
