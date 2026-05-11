package ulb.controllers.combat;

import java.util.List;

import ulb.controllers.MetaController;
import ulb.factories.TeamFactory;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;
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
            BugemonService bugemonService, List<Skill> statBonusSkills) {
        super(metaController, teamService, bugemonService, statBonusSkills, ViewLoader.load(AutomaticCombatView::new));
    }

    @Override
    public void startCombat(boolean shouldRestoreHp) {
        this.shouldRestoreHp = shouldRestoreHp;

        AutoTrainer autoPlayer = new AutoTrainer(this.teamService.getRequiredActiveTeam());
        this.playerTrainer = autoPlayer;
        AutoTrainer opponentTrainer = this.createRandomOpponent(autoPlayer.getTeamSize());

        this.combat = this.combatService.createUniqueCombat(this.statBonusSkills, this.playerTrainer, opponentTrainer);

        this.view.setModel(autoPlayer, opponentTrainer);
        this.view.refresh();
    }

    /** Starts the automatic turn loop. Must be called after the view is shown. */
    public void startAutoRun() {
        this.startTurn();
    }

    /**
     * Creates a random opponent team sized to match the given player's team.
     *
     * @param playerTeamSize
     *            the size of the player's team, used to size the opponent's team.
     */
    private AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(
                TeamFactory.createRandomTeam(this.bugemonService.getAllDefaultBugemons(), playerTeamSize));
    }

    // ── CombatController hooks ────────────────────────────────────────────────

    @Override
    protected void onStepsExhausted() {
        this.startTurn();
    }
}
