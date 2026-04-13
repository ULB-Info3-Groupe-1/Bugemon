package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/** View for the main menu screen. */
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
        return Configuration.Paths.Fxml.MAIN_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }

    public interface Listener {
        void onCreateTeam();

        void onCreateBugemon();

        void onNoTower();

        void onQuit();

        void onStartManualCombat();

        void onStartAutomaticCombat();

        void onEditTeam();
    }
}
