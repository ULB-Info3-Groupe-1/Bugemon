package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.ViewLoader;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller for the automatic combat screen.
 *
 * Drives the {@link Combat} loop one step at a time, gated by the player's "Next" clicks. Each step triggers its
 * animation and dialog before the next one is unlocked. Implements {@link AutomaticCombatView.Listener} to receive
 * those events.
 */
public class AutomaticCombatController extends CombatController<AutomaticCombatView>
        implements AutomaticCombatView.Listener {

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

        AutoTrainer autoPlayer = new AutoTrainer(this.playerService.getActiveTeam());
        this.playerTrainer = autoPlayer;
        AutoTrainer opponentTrainer = this.createRandomOpponent(autoPlayer.getTeamSize());
        this.combat = new Combat(this.playerTrainer, opponentTrainer);

        this.view.setModel(autoPlayer, opponentTrainer);
        this.view.refresh();
        this.startTurn();
    }

    // ── AutomaticCombatView.Listener ──────────────────────────────────────────

    @Override
    public void onNext() {
        this.advanceStep();
    }

    // ── CombatController hooks ────────────────────────────────────────────────

    @Override
    protected void onStepsExhausted() {
        this.startTurn();
    }

    @Override
    protected void onCombatEnded(Trainer winner) {
        this.handleCombatResult(winner);
    }
}
