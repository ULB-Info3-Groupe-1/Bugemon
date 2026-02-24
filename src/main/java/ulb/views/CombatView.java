package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.CombatController;

public class CombatView extends View {

    private CombatController controller;
    @FXML
    private Button victoryButton;
    @FXML
    private Button defeatButton;

    public CombatView() throws IOException {
        super("/fxml/Combat.fxml");
        this.controller = null;

        this.victoryButton.setOnAction((e) -> this.controller.handleVictory());
        this.defeatButton.setOnAction((e) -> this.controller.handleDefeat());
    }

    public void setController(CombatController controller) {
        this.controller = controller;
    }

    @Override
    protected String getTitle() {
        return "Combat";
    }

}
