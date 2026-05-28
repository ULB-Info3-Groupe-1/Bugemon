package bugemon.client.views;

import javafx.fxml.FXML;

import bugemon.common.Configuration;

/**
 * View for the save/load screen. Lets the player start a new game, continue an existing save, or quit. Dispatches
 * player interactions to the controller exclusively through callbacks registered via setters; holds no reference to any
 * concrete controller class.
 */
public class SaveMenuView extends View {

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onNewGameClicked() {
        if (this.showAlertWithTwoButtons("Start New Game",
                "Êtes-vous sûr de vouloir commencer une nouvelle partie ? Cela effacera votre progression actuelle.",
                "Oui", "Non").equals("Oui")) {
            this.listener.onNewGame();
        }
    }

    @FXML
    private void onContinueClicked() {
        this.listener.onContinue();
    }

    @FXML
    private void onQuitClicked() {
        this.listener.onQuit();
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SAVE_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the save menu, so this method is empty.
    }

    /** Callback interface for save menu actions. */
    public interface Listener {
        /** Called when the player confirms starting a new game (replaces any existing save). */
        void onNewGame();

        /** Called when the player chooses to continue from the existing save. */
        void onContinue();

        /** Called when the player chooses to quit the application. */
        void onQuit();
    }
}
