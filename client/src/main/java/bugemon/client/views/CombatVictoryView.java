package bugemon.client.views;

import javafx.fxml.FXML;

import bugemon.common.Configuration;

/**
 * View for the combat victory screen, dispatching actions through the {@link Listener} interface.
 */
public class CombatVictoryView extends View {

    private Listener listener;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_VICTORY_VIEW;
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

    /** Callback interface for the victory screen actions. */
    public interface Listener {

        /** Called when the player chooses to continue after winning. */
        void onContinue();

    }
}
