package ulb.controllers;

import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.views.SaveMenuView;
import ulb.views.ViewLoader;

/** Controller for the main menu screen. */
public class SaveMenuController extends Controller<SaveMenuView> implements SaveMenuView.Listener {

    BugemonService bugemonService;
    TeamService teamService;
    TowerService towerService;

    public SaveMenuController(MetaController metaController, BugemonService bugemonService, TeamService teamService,
            TowerService towerService) {
        super(metaController, ViewLoader.load(SaveMenuView::new));
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
        this.metaController.onMainMenu();
    }

    @Override
    public void onContinue() {
        this.teamService.loadTeamsAndActiveTeam();
        this.towerService.loadTowerProgress();
        this.metaController.onMainMenu();
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }
}
