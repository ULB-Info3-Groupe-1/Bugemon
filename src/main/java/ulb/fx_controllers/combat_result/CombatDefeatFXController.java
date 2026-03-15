package ulb.fx_controllers.combat_result;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.CombatResultController;

public class CombatDefeatFXController extends CombatResultFXController {

    private CombatResultController controller;

    @FXML private Button retryButton;
    @FXML private Button backToMainMenuButton;


    public CombatDefeatFXController(CombatResultController controller) throws IOException {
        super(controller);
    }
}
