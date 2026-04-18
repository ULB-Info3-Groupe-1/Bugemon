package ulb.controllers;

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
        this.metaController.onCreateTeam();
    }

    @Override
    public void onCreateBugemon() {
        this.metaController.onCreateBugemon();
    }

    @Override
    public void onNoTower() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.onTower();
        }
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
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
    public void onEditTeam() {
        this.metaController.onEditTeam();
    }

    private boolean isActiveTeamEmpty() {
        if (this.playerService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
            return true;
        }
        return false;
    }
}
