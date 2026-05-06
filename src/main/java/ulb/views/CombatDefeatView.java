package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/**
 * View for the combat defeat screen, dispatching actions through the {@link CombatDefeatView.Listener} interface.
 */
public class CombatDefeatView extends View {
    private Listener listener;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_DEFEAT_VIEW;
    }

    /**
     * Sets the listener to be notified when the buttons are clicked.
     *
     * @param listener
     *            the listener
     */
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

    public interface Listener {

        /**
         * Dispatches a retry action to the controller.
         */
        void onRetry();

        /**
         * Dispatches a return to main menu action to the controller.
         */
        void onReturnToMainMenu();
    }
}
