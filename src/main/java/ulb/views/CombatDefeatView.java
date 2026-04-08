package ulb.views;

import javafx.fxml.FXML;

/** View for the combat defeat screen, dispatching actions through the {@link Listener} interface. */
public class CombatDefeatView extends View {
    private static final String FXML_PATH = "/fxml/CombatDefeat.fxml";
    private Listener listener;

    @Override
    public String getPath() {
        return FXML_PATH;
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
