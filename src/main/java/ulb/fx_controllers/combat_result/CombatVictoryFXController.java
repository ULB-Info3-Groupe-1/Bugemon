package ulb.fx_controllers.combat_result;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.CombatVictoryController;
import ulb.fx_controllers.FXController;

public class CombatVictoryFXController extends FXController {

    private final CombatVictoryController controller;

    @FXML private Button continueButton;

    public CombatVictoryFXController(CombatVictoryController controller) {
        this.controller = controller;
    }

    @FXML
    private void onContinue(ActionEvent event) {
        this.controller.onContinue();
    }
}
