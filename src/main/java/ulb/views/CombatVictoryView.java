package ulb.views;

import javafx.fxml.FXML;

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

    private Listener listener;

    /** Creates the victory screen view. */
    public CombatVictoryView() {
    }

    @Override
    public String getPath() {
        return this.FXML_PATH;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onContinueClicked() {
        this.listener.onContinue();
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the victory screen.
    }

    public interface Listener {

        void onContinue();

    }
}
