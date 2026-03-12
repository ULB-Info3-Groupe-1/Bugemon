package ulb.views.victory_view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.views.View;

/**
 * CombatVictoryView
 *
 * View for the combat victory screen.
 */
public class CombatVictoryView extends View {

    @FXML private Button continueButton;

    /**
     * Loads the combat victory FXML layout and initializes UI bindings.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CombatVictoryView() throws IOException {
        super("/fxml/CombatVictory.fxml");
    }

    /**
     * Sets the action to be performed when the continue button is pressed.
     * @param action the action to execute on button press
     */
    public void setContinueButtonAction(Runnable action) {
        continueButton.setOnAction(e -> action.run());
    }
    
}
