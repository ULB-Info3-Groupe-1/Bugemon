package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.controllers.CombatDefeatController;

/**
 * CombatDefeatView
 *
 * View for the combat defeat screen.
 */
public class CombatDefeatView extends View {
    private CombatDefeatController controller;
    @FXML private Button retryButton;
    @FXML private Button backToMainMenuButton;

    /**
     * Loads the combat defeat FXML layout and initializes UI bindings.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatDefeatView() throws IOException {
        super("/fxml/CombatDefeat.fxml");
        this.controller = null;

        this.retryButton.setOnAction((e) -> this.controller.retry());
        this.backToMainMenuButton.setOnAction((e) -> this.controller.backToMainMenu());
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling combat defeat screen
     */
    public void setController(CombatDefeatController controller) {
        this.controller = controller;
    }
}
