package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/** View for the combat defeat screen, dispatching actions through the {@link Listener} interface. */
public class CombatDefeatView extends View {
    private Listener listener;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_DEFEAT_VIEW;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onRetryClicked() {
        this.listener.onRetry();
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        this.listener.onReturnToMainMenu();
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the defeat screen.
    }

    /** Callback interface for the defeat screen actions. */
    public interface Listener {

        /** Called when the player chooses to retry the combat. */
        void onRetry();

        /** Called when the player chooses to return to the main menu. */
        void onReturnToMainMenu();

    }
}
