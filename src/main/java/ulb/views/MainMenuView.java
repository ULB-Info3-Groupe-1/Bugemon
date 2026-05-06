package ulb.views;

import javafx.fxml.FXML;

import ulb.Configuration;

/**
 * View for the combat menu screen. Dispatches player interactions to the controller exclusively through callbacks
 * registered via setters. The view holds no reference to any concrete controller class.
 */
public class MainMenuView extends View {

    private Listener listener;

    /**
     * Registers the listener.
     *
     * @param listener
     *            the listener
     */
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

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.MAIN_MENU_VIEW;
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }

    public interface Listener {

        /**
         * Calls the controller when the create team button is clicked.
         */
        void onCreateTeam();

        /**
         * Calls the controller when the create bugemon button is clicked.
         */
        void onCreateBugemon();

        /**
         * Calls the controller when the edit team button is clicked.
         */
        void onEditTeam();

        /**
         * Calls the controller when the tower button is clicked.
         */
        void onTower();

        /**
         * Calls the controller when the start manual combat button is clicked.
         */
        void onStartManualCombat();

        /**
         * Calls the controller when the start automatic combat button is clicked.
         */
        void onStartAutomaticCombat();

        /**
         * Calls the controller when the save menu return button is clicked.
         */
        void onSaveMenuReturnButton();
    }
}
