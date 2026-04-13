package ulb.controllers;

import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;
import ulb.views.MainMenuView;
import ulb.views.ViewLoader;

/** Controller for the main menu screen. */
public class MainMenuController extends Controller<MainMenuView> implements MainMenuView.Listener {
    private final PlayerService playerService;

    public MainMenuController(MetaController metaController, PlayerService playerService) {
        super(metaController, ViewLoader.load(MainMenuView::new));
        this.playerService = playerService;
        this.view.setListener(this);
    }

    @Override
    public void onCreateTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    @Override
    public void onCreateBugemon() {
        this.metaController.switchTo(Window.CREATE_BUGEMON);
    }

    @Override
    public void onNoTower() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.NOTOWER);
        }
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }

    @Override
    public void onStartAutomaticCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.AUTOMATIC_COMBAT);
        }
    }

    @Override
    public void onStartManualCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.MANUAL_COMBAT);
        }
    }

    @Override
    public void onEditTeam() {
        this.metaController.switchTo(Window.EDIT_TEAM);
    }

    private boolean isActiveTeamEmpty() {
        if (this.playerService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
            return true;
        }
        return false;
    }
}
