package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.CombatVictoryController;

/**
 * CombatVictoryView
 *
 * View for the combat victory screen.
 */
public class CombatVictoryView extends View {
    private CombatVictoryController controller;
    @FXML private Button continueButton;

    /**
     * Loads the combat victory FXML layout and initializes UI bindings.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatVictoryView() throws IOException {
        super("/fxml/CombatVictory.fxml");
        this.controller = null;

        this.continueButton.setOnAction((e) -> this.controller.cont());
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling combat victory screen
     */
    public void setController(CombatVictoryController controller) {
        this.controller = controller;
    }
}
