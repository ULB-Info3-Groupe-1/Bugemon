package ulb.controllers.combat;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.trainer.AutoTrainer;
import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.views.ViewLoader;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller for the automatic combat screen.
 *
 * Drives the {@link Combat} loop one step at a time, gated by the player's "Next" clicks. Each step triggers its
 * animation and dialog before the next one is unlocked.
 */
public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    /**
     * Constructs an {@code AutomaticCombatController} and initialises its {@link AutomaticCombatView}.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     */
    public AutomaticCombatController(MetaController metaController, TeamService teamService,
            BugemonService bugemonService, CombatFactory combatFactory) {
        super(metaController, teamService, bugemonService, combatFactory, ViewLoader.load(AutomaticCombatView::new));
    }

    @Override
    public void startCombat() {
        AutoTrainer autoPlayer = new AutoTrainer(this.teamService.getRequiredActiveTeam());
        this.playerTrainer = autoPlayer;
        AutoTrainer opponentTrainer = this.createRandomOpponent(autoPlayer.getTeamSize());

        this.combat = this.combatFactory.create(this.playerTrainer, opponentTrainer);

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
