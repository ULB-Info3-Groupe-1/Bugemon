package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/**
 * View for the combat menu screen. Dispatches player interactions to the controller exclusively through callbacks
 * registered via setters. The view holds no reference to any concrete controller class.
 */
public class CombatMenuView extends View {

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onStartManualCombatClicked() {
        this.listener.onStartManualCombat();
    }

    @FXML
    private void onStartAutomaticCombatClicked() {
        this.listener.onStartAutomaticCombat();
    }

    @FXML
    private void onNoTowerClicked() {
        this.listener.onNoTower();
    }

    @FXML
    private void onReturnMainMenuClicked() {
        this.listener.onReturnToMainMenu();
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }

    public interface Listener {
        void onNoTower();

        void onStartManualCombat();

        void onStartAutomaticCombat();

        void onReturnToMainMenu();
    }
}
