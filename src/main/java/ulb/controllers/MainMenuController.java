package ulb.controllers;

import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.views.MainMenuView;
import ulb.views.ViewLoader;

/**
 * Controller for the combat menu screen.
 */
public class MainMenuController extends Controller<MainMenuView> implements MainMenuView.Listener {

    BugemonService bugemonService;
    TeamService teamService;
    TowerService towerService;

    public MainMenuController(MetaController metaController, BugemonService bugemonService, TeamService teamService,
            TowerService towerService) {
        super(metaController, ViewLoader.load(MainMenuView::new));
        this.bugemonService = bugemonService;
        this.teamService = teamService;
        this.towerService = towerService;
        this.view.setListener(this);
    }

    @Override
    public void onNewGame() {
        this.bugemonService.clearAllPlayerBugemons();
        this.teamService.clearTeamsAndActiveTeam();
        this.towerService.clearTowerProgress();
    }

    @Override
    public void onContinue() {
        this.teamService.loadTeamsAndActiveTeam();
        this.towerService.loadTowerProgress();
        this.metaController.onCombatMenu();
    }

    @Override
    public void onCreateTeam() {
        this.metaController.onCreateTeam();
    }

    @Override
    public void onEditTeam() {
        this.metaController.onEditTeam();
    }

    @Override
    public void onCreateBugemon() {
        this.metaController.onCreateBugemon();
    }

    /** Starts an automatic combat session. */
    @Override
    public void onStartAutomaticCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.onStartAutomaticCombat();
        }
    }

    /** Starts a manual combat session. */
    @Override
    public void onStartManualCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.onStartManualCombat();
        }
    }

    @Override
    public void onNoTower() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.onTower();
        }
    }

    @Override
    public void onReturnToSaveMenu() {
        this.metaController.onSaveMenu();
    }
}
