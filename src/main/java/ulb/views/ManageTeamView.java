package ulb.views;

import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import ulb.Configuration;
import ulb.controllers.ManageTeamController.TeamFormMode;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
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
    private BugemonTeam bugemonTeam;
    private boolean isBugemonTeamSaved;
    private List<Bugemon> availableBugemons;

    public ManageTeamView(TeamFormMode mode) {
        this.mode = mode;
    }

    // --- Initialization ---

    @FXML
    private void initialize() {
        if (this.mode != null) {
            this.setMode(this.mode);
        }
    }

    /**
     * Sets the mode of the view and displays the appropriate buttons.
     *
     * @param mode
     *            The mode of the view
     */
    public void setMode(TeamFormMode mode) {
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

        if (this.listener != null) {
            this.allBugemonsGridView.setListener(this.listener::onBugemonSelected);
            this.bugemonsTeamView.setListener(this.listener::onBugemonSelected);
        }
    }

    public interface Listener {
        void onReturnToMainMenu();

        void onSave(String teamName);

        void onLoad(String teamName);

        void onDelete(String teamName);

        void onRename(String oldName, String newName);

        void onAddNewTeam();

        void onBugemonSelected(Bugemon bugemon);

        void onModifyTeam(String teamName);

        void onStartAutomaticCombat();

        void onStartManualCombat();

        void onStartNOTowerCombat();
    }

    // --- View refresh ---

    @Override
    public void refresh() {
        this.allBugemonsGridView.showAll(this.availableBugemons, new HashSet<>(this.bugemonTeam.getAll()));
        this.updateTeamUI();
    }

    private void updateTeamUI() {
        this.bugemonsTeamView.showTeam(this.bugemonTeam);
        if (this.bugemonTeam.isEmpty()) {
            this.selectedTeamName.setText(NO_TEAM_SELECTED);
        } else if (this.isBugemonTeamSaved) {
            this.selectedTeamName.setText(this.bugemonTeam.getName());
        } else {
            this.selectedTeamName.setText(TEAM_NOT_SAVED_MESSAGE);
        }
    }

    // --- Utils ---

    public void clearTeamNameToSave() {
        this.saveTeamNameInput.setText("");
    }

    public void setIsTeamSaved(boolean isTeamSaved) {
        this.isBugemonTeamSaved = isTeamSaved;
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
        this.listener.onDelete(this.getTeamNameToLoad());
        this.selectedTeamName.setText(NO_TEAM_SELECTED);
    }

    @FXML
    private void onRenameClicked() {
        this.listener.onRename(this.getTeamNameToLoad(), this.getTeamNameToSave());
    }

    @FXML
    private void onAddNewTeamClicked() {
        this.listener.onAddNewTeam();
    }

    @FXML
    private void onModifyTeamClicked() {
        this.listener.onModifyTeam(this.getTeamNameToLoad());
    }

    @FXML
    private void onTeamSelected(MouseEvent event) {
        String selected = this.teamListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.selectedTeamName.setText(selected);
            this.listener.onLoad(this.getTeamNameToLoad());
        } else {
            this.selectedTeamName.setText(NO_TEAM_SELECTED);
        }
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
        this.listener.onStartNOTowerCombat();
    }

    // --- Setters ---

    public void setTeam(BugemonTeam team) {
        this.bugemonTeam = team;
    }

    public void setAvailableBugemons(List<Bugemon> allBugemons) {
        this.availableBugemons = allBugemons;
    }

    /**
     * Selects the team with the given name in the team list. If the team is not found, no team is selected.
     *
     * @param teamName
     *            the name of the team to select
     */
    public void setTeamListSelected(String teamName) {
        this.teamListView.getSelectionModel().select(teamName);
    }

    public void setTeamList(List<String> teamNames) {
        this.teamListView.setItems(FXCollections.observableArrayList(teamNames));
    }

    public void setSaveTeamName(String name) {
        this.saveTeamNameInput.setText(name);
    }

    // --- Getters ---

    private String getTeamNameToSave() {
        return this.saveTeamNameInput.getText();
    }

    private String getTeamNameToLoad() {
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
