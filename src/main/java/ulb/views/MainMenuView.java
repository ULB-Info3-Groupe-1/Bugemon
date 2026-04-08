package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.Configuration;

/**
 * View for the main menu screen. Dispatches player interactions to the controller exclusively through callbacks
 * registered via setters. The view holds no reference to any concrete controller class.
 */
public class MainMenuView extends View {
    @FXML
    private Button createTeamButton;
    @FXML
    private Button noTowerButton;
    @FXML
    private Button quitButton;
    @FXML
    private Button startAutomaticCombatButton;
    @FXML
    private Button startManualCombatButton;

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onCreateTeamClicked() {
        this.listener.onCreateTeam();
    }

    @FXML
    private void onEditTeamClicked() {
        this.listener.onEditTeam();
    }

    @FXML
    private void onNoTowerClicked() {
        this.listener.onNoTower();
    }

    @FXML
    private void onQuitClicked() {
        this.listener.onQuit();
    }

    @FXML
    private void onStartManualCombatClicked() {
        this.listener.onStartManualCombat();
    }

    @FXML
    private void onStartAutomaticCombatClicked() {
        this.listener.onStartAutomaticCombat();
    }

    @Override
    public String getPath() {
        return Configuration.Paths.FXML.MAIN_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }

    public interface Listener {
        void onCreateTeam();

        void onNoTower();

        void onQuit();

        void onStartManualCombat();

        void onStartAutomaticCombat();

        void onEditTeam();
    }
}
