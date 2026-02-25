package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.MainMenuController;

/**
 * MainMenuView
 *
 * View for the main menu screen.
 */
public class MainMenuView extends View {

    private MainMenuController controller;
    @FXML
    private Button createTeamButton;

    /**
     * Loads the main menu FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
        this.controller = null;

        this.createTeamButton.setOnAction((e) -> this.controller.createTeam());
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling main menu events
     */
    public void setController(MainMenuController controller) {
        this.controller = controller;
    }

    @Override
    protected String getTitle() {
        return "Main Menu";
    }

}
