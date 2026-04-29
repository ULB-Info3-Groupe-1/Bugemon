package ulb.controllers;

import ulb.services.TeamService;
import ulb.views.MainMenuView;
import ulb.views.ViewLoader;

/**
 * Controller for the combat menu screen.
 */
public class MainMenuController extends Controller<MainMenuView> implements MainMenuView.Listener {
    TeamService teamService;

    public MainMenuController(MetaController metaController, TeamService teamService) {
        super(metaController, ViewLoader.load(MainMenuView::new));
        this.teamService = teamService;
        this.view.setListener(this);
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
    public void onTower() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.onTower();
        }
    }

    @Override
    public void onReturnToSaveMenu() {
        this.metaController.onSaveMenu();
    }

    private boolean isActiveTeamEmpty() {
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
            return true;
        }
        return false;
    }
}
