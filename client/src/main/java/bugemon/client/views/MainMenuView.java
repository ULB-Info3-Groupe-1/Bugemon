package bugemon.client.views;

import javafx.fxml.FXML;

import bugemon.common.Configuration;

/**
 * View for the main menu screen. Dispatches player interactions to the controller exclusively through callbacks
 * registered via setters. The view holds no reference to any concrete controller class.
 */
public class MainMenuView extends View {

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
    private void onCreateBugemonClicked() {
        this.listener.onCreateBugemon();
    }

    @FXML
    private void onStartManualCombatClicked() {
        this.listener.onStartManualCombat();
    }

    @FXML
    private void onSkillTreeClicked() {
        this.listener.onSkillTree();
    }

    @FXML
    private void onStartAutomaticCombatClicked() {
        this.listener.onStartAutomaticCombat();
    }

    @FXML
    private void onTowerClicked() {
        this.listener.onTower();
    }

    @FXML
    private void onSaveMenuReturnButtonClicked() {
        this.listener.onSaveMenuReturnButton();
    }

    @FXML
    private void onQuitClicked() {
        this.listener.onQuit();
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.MAIN_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }

    /** Callback interface for all main menu navigation actions. */
    public interface Listener {
        void onCreateTeam();

        void onCreateBugemon();

        void onEditTeam();

        void onTower();

        void onStartManualCombat();

        void onStartAutomaticCombat();

        void onSaveMenuReturnButton();

        void onSkillTree();

        void onQuit();
    }
}
