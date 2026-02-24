package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.CombatResultController;

public class CombatResultView extends View {

    private CombatResultController controller;
    @FXML
    private Button backToMainMenuButton;

    public CombatResultView() throws IOException {
        super("/fxml/CombatResult.fxml");
        this.controller = null;

        this.backToMainMenuButton.setOnAction((e) -> this.controller.backToMainMenu());
    }

    public void setController(CombatResultController controller) {
        this.controller = controller;
    }

    @Override
    protected String getTitle() {
        return "Combat Result";
    }

}
