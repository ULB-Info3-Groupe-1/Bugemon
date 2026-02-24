package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.MainMenuController;

public class MainMenuView extends View {

    private MainMenuController controller;
    @FXML
    private Button createTeamButton;

    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
        this.controller = null;

        this.createTeamButton.setOnAction((e) -> this.controller.createTeam());
    }

    public void setController(MainMenuController controller) {
        this.controller = controller;
    }

    @Override
    protected String getTitle() {
        return "Main Menu";
    }

}
