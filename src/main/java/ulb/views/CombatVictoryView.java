package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * View for the combat victory screen.
 *
 * <p>
 * Dispatches user interactions through the callback registered via {@link #setOnContinue(Runnable)}. Holds no reference
 * to any concrete controller class.
 * </p>
 */
public class CombatVictoryView extends View {
    private final String FXML_PATH = "/fxml/CombatVictory.fxml";

    @FXML
    private Button continueButton;

    Listener listener;

    /**
     * Loads the victory-screen FXML layout and wires the continue button.
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public CombatVictoryView() {
    }

    @Override
    public String getPath() {
        return this.FXML_PATH;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the victory screen.
    }

    public interface Listener {

        void onContinue();

    }
}
