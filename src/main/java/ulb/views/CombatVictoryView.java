package ulb.views;

import javafx.fxml.FXML;

/** View for the combat victory screen, dispatching actions through the {@link Listener} interface. */
public class CombatVictoryView extends View {
    private final String fxmlPath = "/fxml/CombatVictory.fxml";

    private Listener listener;

    public CombatVictoryView() {
    }

    @Override
    public String getPath() {
        return this.fxmlPath;
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
