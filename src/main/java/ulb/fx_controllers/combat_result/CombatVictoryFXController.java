package ulb.fx_controllers.combat_result;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.CombatVictoryController;

public class CombatVictoryFXController {

    private final CombatVictoryController controller;

    @FXML private Button continueButton;

    public CombatVictoryFXController(CombatVictoryController controller) throws IOException {
        this.controller = controller;
    }

    @FXML
    private void onContinue(ActionEvent event) {
        this.controller.onContinue();
    }
}
