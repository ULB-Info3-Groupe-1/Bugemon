package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * View for the main menu screen. Dispatches user interactions to the controller exclusively through callbacks
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

    private final String fxmlPath = "/fxml/MainMenu.fxml";

    public MainMenuView() {
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onCreateTeamClicked() {
        this.listener.onCreateTeam();
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
        return this.fxmlPath;
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
    }
}
