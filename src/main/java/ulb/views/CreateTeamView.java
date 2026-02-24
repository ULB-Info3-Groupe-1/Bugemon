package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.CreateTeamController;

public class CreateTeamView extends View {

    private CreateTeamController controller;
    @FXML
    private Button startCombatButton;

    public CreateTeamView() throws IOException {
        super("/fxml/CreateTeam.fxml");
        this.controller = null;

        this.startCombatButton.setOnAction((e) -> this.controller.startCombat());
    }

    public void setController(CreateTeamController controller) {
        this.controller = controller;
    }

    @Override
    protected String getTitle() {
        return "Create Team";
    }

}
