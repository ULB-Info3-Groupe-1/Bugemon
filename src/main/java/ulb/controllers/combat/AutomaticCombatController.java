package ulb.controllers.combat;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.trainer.AutoTrainer;
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
public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    /**
     * Constructs an {@code AutomaticCombatController} and initialises its {@link AutomaticCombatView}.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     */
    public AutomaticCombatController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) {
        super(metaController, playerService, bugemonService, ViewLoader.load(AutomaticCombatView::new));
    }

    @Override
    public void startCombat(boolean shouldRestoreHp) {
        this.restoreHpAfterCombat = shouldRestoreHp;

        AutoTrainer autoPlayer = new AutoTrainer(this.playerService.getActiveTeam().orElseThrow(
                () -> new IllegalStateException("No active team for player when starting Automatic combat")));
        this.playerTrainer = autoPlayer;
        AutoTrainer opponentTrainer = this.createRandomOpponent(autoPlayer.getTeamSize());
        this.combat = new Combat(this.playerTrainer, opponentTrainer);

        this.view.setModel(autoPlayer, opponentTrainer);
        this.view.refresh();
    }

    /** Starts the automatic turn loop. Must be called after the view is shown. */
    public void startAutoRun() {
        this.startTurn();
    }

    // ── CombatController hooks ────────────────────────────────────────────────

    @Override
    protected void onStepsExhausted() {
        this.startTurn();
    }
}
