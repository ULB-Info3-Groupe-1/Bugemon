package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/**
 * View for the main menu screen. Dispatches player interactions to the controller exclusively through callbacks
 * registered via setters. The view holds no reference to any concrete controller class.
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

    public interface Listener {
        void onNewGame();

        void onContinue();

        void onQuit();
    }
}
