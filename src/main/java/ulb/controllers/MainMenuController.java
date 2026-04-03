package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;
import ulb.views.MainMenuView;

/** Controller for the main menu screen. */
public class MainMenuController extends Controller<MainMenuView> implements MainMenuView.Listener {
    private final PlayerService playerService;

    public MainMenuController(MetaController metaController, PlayerService playerService) throws IOException {
        super(metaController, new MainMenuView());
        this.playerService = playerService;

        this.view.setOnStartAutoCombat(this::startAutoCombat);
        this.view.setOnStartManualCombat(this::startManualCombat);

        this.view.setListener(this);
    }

    @Override
    public void onCreateTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    @Override
    public void onNoTower() {
        this.metaController.switchTo(Window.NOTOWER);
    }

    @Override
    public void onQuit() {
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
