package ulb.views;

import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import ulb.Configuration;
import ulb.controllers.ManageTeamController.TeamFormMode;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.views.components.AllBugemonsView;
import ulb.views.components.BugemonTeamView;

/**
 * View for the team creation screen. Holds a reference to the {@link BugemonTeam} model and reads from it directly in
 * {@link #refresh()}. Dispatches player interactions through a {@link Listener}; holds no reference to any concrete
 * controller class.
 */
public class ManageTeamView extends View {

    private static final String NO_TEAM_SELECTED = "Pas d'équipe sélectionnée";
    private static final String NO_ACTIVE_TEAM = "Aucune équipe active";
    private static final String INVALID_NAME = "Nom d'équipe invalide";
    private static final String TEAM_NAME_ALREADY_USED = "Nom d'équipe déjà utilisé";
    private static final String TEAM_NAME_NOT_FOUND = "Nom d'équipe introuvable";
    private static final String TEAM_EMPTY = "Équipe vide";
    private static final String TEAM_NOT_SAVED_MESSAGE = "Nouvelle équipe ou équipe existante modifiée non sauvegardée."
            + " Donnez lui un nom et sauvegardez la pour l'enregistrer.";
    private static final String GO_MAIN_MENU_WITHOUT_SAVING = "Aller au menu principal sans sauvegarder";
    private static final String BACK = "Retour";
    private static final String TEAM_NOT_SAVED = "Équipe non sauvegardée";

    @FXML
    private AllBugemonsView allBugemonsGridView;
    @FXML
    private BugemonTeamView bugemonsTeamView;
    @FXML
    private TextField saveTeamNameInput;
    @FXML
    private ListView<String> teamListView;
    @FXML
    private Text selectedTeamName;
    @FXML
    private Button modifyTeamButton;
    @FXML
    private Button deleteTeamButton;
    @FXML
    private Button renameTeamButton;
    @FXML
    private Button startAutomaticCombatButton;
    @FXML
    private Button startManualCombatButton;
    @FXML
    private Button startTowerCombatButton;

    private final TeamFormMode mode;

    private Listener listener;

    public ManageTeamView(TeamFormMode mode) {
        this.mode = mode;
    }

    // --- Initialization ---

    @FXML
    private void initialize() {
        this.setMode(this.mode);
    }

    /**
     * Sets the mode of the view and displays the appropriate buttons.
     *
     * @param mode
     *            The mode of the view
     */
    private void setMode(TeamFormMode mode) {
        boolean isCreate = (mode == TeamFormMode.CREATE);
        List<Button> editButtons = List.of(this.modifyTeamButton, this.renameTeamButton, this.deleteTeamButton,
                this.startAutomaticCombatButton, this.startManualCombatButton, this.startTowerCombatButton);

        editButtons.forEach(btn -> {
            btn.setVisible(!isCreate);
            btn.setManaged(!isCreate);
        });
    }

    // --- View loading ---

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.MANAGE_TEAM_VIEW;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        this.allBugemonsGridView.setListener(this.listener::onBugemonSelected);
        this.bugemonsTeamView.setListener(this.listener::onBugemonSelected);
    }

    public interface Listener {
        void onReturnToMainMenu();

        void onSave(String teamName);

        void onDelete(String teamName);

        void onRename(String newName);

        void onAddNewTeam();

        void onBugemonSelected(PlayerBugemon bugemon);

        void onModifyTeam(String teamName);

        void onStartAutomaticCombat();

        void onStartManualCombat();

        void onStartTowerCombat();

        boolean isWorkingTeamSaved();

        void onTeamSelected(String teamName);
    }

    // --- View refresh ---

    @Override
    public void refresh() {
    }

    public void refreshAvailableBugemons(List<PlayerBugemon> availableBugemons, Set<PlayerBugemon> selectedBugemons) {
        this.allBugemonsGridView.showAll(availableBugemons, selectedBugemons);
    }

    public void refreshTeamNames(List<String> teamNames) {
        this.teamListView.setItems(FXCollections.observableArrayList(teamNames));
    }

    public void refreshTeam(Team team, boolean isTeamSaved) {
        String teamName = team.getName();
        if (teamName == null) {
            this.teamListView.getSelectionModel().clearSelection();
        } else {
            this.teamListView.getSelectionModel().select(teamName);
        }

        this.bugemonsTeamView.showTeam(team);
        if (team.isEmpty()) {
            this.selectedTeamName.setText(NO_TEAM_SELECTED);
        } else if (isTeamSaved) {
            this.selectedTeamName.setText(team.getName());
        } else {
            this.selectedTeamName.setText(TEAM_NOT_SAVED_MESSAGE);
        }
    }

    // --- Utils ---

    public void clearTeamNameToSave() {
        this.saveTeamNameInput.setText("");
    }

    // --- Actions to perform when clicked ---

    @FXML
    private void onSaveClicked() {
        this.listener.onSave(this.getTeamNameToSave());
    }

    @FXML
    private void onReturnMainMenuClicked() {
        this.listener.onReturnToMainMenu();
    }

    @FXML
    private void onDeleteClicked() {
        this.listener.onDelete(this.getSelectedTeamName());
    }

    @FXML
    private void onRenameClicked() {
        this.listener.onRename(this.getTeamNameToSave());
    }

    @FXML
    private void onAddNewTeamClicked() {
        this.listener.onAddNewTeam();
    }

    @FXML
    private void onModifyTeamClicked() {
        this.listener.onModifyTeam(this.getSelectedTeamName());
    }

    @FXML
    private void onTeamSelected(MouseEvent event) {
        this.listener.onTeamSelected(this.teamListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onStartAutomaticCombatClicked() {
        this.listener.onStartAutomaticCombat();
    }

    @FXML
    private void onStartManualCombatClicked() {
        this.listener.onStartManualCombat();
    }

    @FXML
    private void onStartTowerCombatClicked() {
        this.listener.onStartTowerCombat();
    }

    // --- Getters ---

    private String getTeamNameToSave() {
        return this.saveTeamNameInput.getText();
    }

    private String getSelectedTeamName() {
        return this.selectedTeamName.getText();
    }

    // --- Alerts ---

    public void showEmptyTeamNameAlert() {
        this.showWarningAlert(INVALID_NAME, "Le nom d'équipe ne peut pas être vide.");
    }

    public void showEmptyTeamAlert() {
        this.showWarningAlert(TEAM_EMPTY, "L'équipe ne peut pas être vide.");
    }

    public void showTeamNameAlreadyExistsAlert(String teamName) {
        this.showWarningAlert(TEAM_NAME_ALREADY_USED, "Une équipe est déjà sauvée avec le nom " + teamName + ".");
    }

    public void showTeamNotFoundAlert(String teamName) {
        this.showWarningAlert(TEAM_NAME_NOT_FOUND, "Aucune équipe sauvegardée avec le nom " + teamName + ".");
    }

    public void showSelectTeamToRenameAlert() {
        this.showWarningAlert(TEAM_NAME_NOT_FOUND, "Veuillez sélectionner une équipe à renommer.");
    }

    public void showDeleteTeamNoActiveTeamAlert() {
        this.showWarningAlert(NO_ACTIVE_TEAM, "Sélectionnez l'équipe que vous souhaitez supprimer.");
    }

    public void showRenameTeamNoActiveTeamAlert() {
        this.showWarningAlert(NO_ACTIVE_TEAM, "Sélectionnez l'équipe que vous souhaitez renommer.");
    }

    public void showAlertChooseTeamToModify() {
        this.showNoActiveTeamAlert("Veuillez choisir une équipe à modifier.");
    }

    /**
     * Displays a warning dialog to warn the user that there are unsaved changes. He can choose to continue or go back.
     *
     * @return (boolean) true if the user wants to continue, false if he wants to go back
     */
    public boolean showAlertTeamChangesNotSave() {
        return this.showAlertWithTwoButtons(TEAM_NOT_SAVED, TEAM_NOT_SAVED_MESSAGE, GO_MAIN_MENU_WITHOUT_SAVING, BACK)
                .equals(GO_MAIN_MENU_WITHOUT_SAVING);
    }
}
