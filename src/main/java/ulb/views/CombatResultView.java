package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.CombatResultController;

/**
 * CombatResultView
 *
 * View for the combat result screen.
 */
public class CombatResultView extends View {

    private CombatResultController controller;
    @FXML
    private Button backToMainMenuButton;

    /**
     * Loads the combat result FXML layout and initializes UI bindings.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatResultView() throws IOException {
        super("/fxml/CombatResult.fxml");
        this.controller = null;

        this.backToMainMenuButton.setOnAction((e) -> this.controller.backToMainMenu());
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling combat result screen
     */
    public void setController(CombatResultController controller) {
        this.controller = controller;
    }

}
