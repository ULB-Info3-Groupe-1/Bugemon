package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;
import ulb.views.MainMenuView;

/** Controller for the main menu screen. */
public class MainMenuController extends Controller<MainMenuView> {
    private final PlayerService playerService;

    public MainMenuController(MetaController metaController, PlayerService playerService) throws IOException {
        super(metaController, new MainMenuView());
        this.playerService = playerService;

        this.view.setOnCreateTeam(this::createTeam);
        this.view.setOnNoTower(this::launchNoTower);
        this.view.setOnQuit(this::quit);
        this.view.setOnStartAutoCombat(this::startAutoCombat);
        this.view.setOnStartManualCombat(this::startManualCombat);
    }

    public void createTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    /** Callback invoked when the player requests to launch the NO Tower mode. */
    public void launchNoTower() {
        this.metaController.switchTo(Window.NOTOWER);
    }

    public void quit() {
        javafx.application.Platform.exit();
    }

    /** Launches an automatic combat session. */
    public void startAutoCombat() {
        if (this.playerService.isActiveTeamEmpty()) {
            this.showNoTeamAlert();
        } else {
            this.metaController.switchTo(Window.AUTOMATIC_COMBAT);
        }
    }

    /** Launches a manual combat session. */
    public void startManualCombat() {
        if (this.playerService.isActiveTeamEmpty()) {
            this.showNoTeamAlert();
        } else {
            this.metaController.switchTo(Window.MANUAL_COMBAT);
        }
    }

    private void showNoTeamAlert() {
        view.showAlert("Aucune équipe active", "Veuillez créer ou charger une équipe avant de lancer un combat.");
    }
}
