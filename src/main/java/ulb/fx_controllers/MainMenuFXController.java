package ulb.fx_controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.MetaController.Window;

/**
 * MainMenuView
 *
 * View for the main menu screen.
 */
public class MainMenuFXController extends FXController {

    @FXML private Button createTeamButton;
    @FXML private Button quitButton;

    @FXML
    private void onCreateTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    @FXML
    private void onQuit() {
        javafx.application.Platform.exit();
    }
}
