package ulb.views;

import javafx.fxml.FXML;

/** View for the combat defeat screen, dispatching actions through the {@link Listener} interface. */
public class CombatDefeatView extends View {
    private final String fxmlPath = "/fxml/CombatDefeat.fxml";
    private Listener listener;

    public CombatDefeatView() {
    }

    @Override
    public String getPath() {
        return this.fxmlPath;
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
