package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.controllers.combat.CombatController;

/**
 * CombatView
 *
 * View for the combat screen.
 */
public class CombatView extends View {

    private CombatController controller;

    @FXML
    private Button victoryButton;

    @FXML
    private Button defeatButton;

    /**
     * Loads the combat FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatView() throws IOException {
        super("/fxml/Combat.fxml");
        this.controller = null;
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling combat
     */
    public void setController(CombatController controller) {
        this.controller = controller;
    }
}
