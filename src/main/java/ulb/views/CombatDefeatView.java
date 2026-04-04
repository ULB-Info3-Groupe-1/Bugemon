package ulb.views;

import javafx.fxml.FXML;

/**
 * View for the combat defeat screen.
 *
 * <p>
 * Dispatches user interactions through the callbacks registered via {@link #setOnRetry(Runnable)} and
 * {@link #setOnBackToMainMenu(Runnable)}. Holds no reference to any concrete controller class.
 * </p>
 */
public class CombatDefeatView extends View {
    private final String FXML_PATH = "/fxml/CombatDefeat.fxml";
    private Listener listener;

    /** Creates the defeat screen view. */
    public CombatDefeatView() {
    }

    @Override
    public String getPath() {
        return this.FXML_PATH;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onRetryClicked() {
        this.listener.onRetry();
    }

    @FXML
    private void onBackToMainMenuClicked() {
        this.listener.onBackToMainMenu();
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the defeat screen.
    }

    public interface Listener {

        void onRetry();

        void onBackToMainMenu();

    }
}
