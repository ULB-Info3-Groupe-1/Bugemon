package ulb.controllers;

import ulb.services.TeamService;
import ulb.views.CombatMenuView;
import ulb.views.ViewLoader;

/**
 * Controller for the combat menu screen.
 */
public class CombatMenuController extends Controller<CombatMenuView> implements CombatMenuView.Listener {
    TeamService teamService;

    public CombatMenuController(MetaController metaController, TeamService teamService) {
        super(metaController, ViewLoader.load(CombatMenuView::new));
        this.teamService = teamService;
        this.view.setListener(this);
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
    public void onReturnToMainMenu() {
        this.metaController.onReturnToMainMenu();
    }

    private boolean isActiveTeamEmpty() {
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
            return true;
        }
        return false;
    }
}
