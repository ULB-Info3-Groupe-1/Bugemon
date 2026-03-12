package ulb.views.victory_view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.views.View;

/**
 * CombatDefeatView
 *
 * View for the combat defeat screen.
 */
public class CombatDefeatView extends View {

    @FXML private Button retryButton;
    @FXML private Button backToMainMenuButton;

    /**
     * Loads the combat defeat FXML layout and initializes UI bindings.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatDefeatView() throws IOException {
        super("/fxml/CombatDefeat.fxml");
    }

    /**
     * Sets the action to be performed when the retry button is pressed.
     * @param action the action to execute on button press
     */
    public void setRetryButtonAction(Runnable action) {
        retryButton.setOnAction(e -> action.run());
    }

    /**
     * Sets the action to be performed when the back to main menu button is pressed.
     * @param action the action to execute on button press
     */
    public void setBackToMainMenuButtonAction(Runnable action) {
        backToMainMenuButton.setOnAction(e -> action.run());
    }
}
