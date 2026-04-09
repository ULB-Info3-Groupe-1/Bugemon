package ulb.views;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import ulb.Configuration;
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

    public enum TeamFormMode {
        EDIT,
        CREATE
    }

    private static final String NO_TEAM_SELECTED = "Pas d'équipe sélectionnée";
    private static final String INVALID_NAME = "Nom d'équipe invalide";
    private static final String TEAM_NAME_ALREADY_USED = "Nom d'équipe déjà utilisé";
    private static final String TEAM_NAME_NOT_FOUND = "Nom d'équipe introuvable";
    private static final String TEAM_EMPTY = "Équipe vide";

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

    private Listener listener;
    private Optional<BugemonTeam> bugemonTeam;
    private boolean isBugemonTeamSaved;
    private List<Bugemon> availableBugemons;

    @FXML
    private void initialize() {
        this.selectedTeamName.setText(NO_TEAM_SELECTED);
    }

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
        this.listener.onModifyTeam();
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

    public void setTeam(Optional<BugemonTeam> team) {
        this.bugemonTeam = team;
    }

    /**
     * Sets the full list of available Bugemons and displays them in the selection grid.
     */
    public void setAvailableBugemons(List<Bugemon> allBugemons) {
        this.availableBugemons = allBugemons;
    }

    public void setTeamList(List<String> teamNames) {
        this.teamListView.setItems(FXCollections.observableArrayList(teamNames));
    }

    @Override
    public void refresh() {
        this.allBugemonsGridView.showAll(this.availableBugemons,
                this.bugemonTeam.map(team -> team.stream().collect(Collectors.toSet())).orElse(new HashSet<>()));

        this.bugemonTeam.ifPresentOrElse(team -> {
            this.bugemonsTeamView.showTeam(team);
            if (team.isEmpty()) {
                this.selectedTeamName.setText(NO_TEAM_SELECTED);

            } else if (this.isBugemonTeamSaved) {
                this.selectedTeamName.setText(team.getName());
                this.teamListView.getSelectionModel().select(team.getName());
            } else {
                this.selectedTeamName.setText("Cette équipe (nouvelle ou modifiée) n'a pas encore été enregistrée. "
                        + "Veuillez lui donner un nom et la sauvegarder pour conserver vos modifications.");
            }
        }, () -> {
            this.bugemonsTeamView.clearBugemons();
            this.selectedTeamName.setText(NO_TEAM_SELECTED);
            this.teamListView.getSelectionModel().clearSelection();
        });
    }

    public void setSaveTeamName(String name) {
        this.saveTeamNameInput.setText(name);
    }

    private String getTeamNameToSave() {
        return this.saveTeamNameInput.getText();
    }

    private String getTeamNameToLoad() {
        return this.selectedTeamName.getText();
    }

    public void showEmptyTeamNameAlert() {
        this.showAlert(INVALID_NAME, "Le nom d'équipe ne peut pas être vide.");
    }

    public void showEmptyTeamAlert() {
        this.showAlert(TEAM_EMPTY, "L'équipe ne peut pas être vide.");
    }

    public void showTeamNameAlreadyExistsAlert(String teamName) {
        this.showAlert(TEAM_NAME_ALREADY_USED, "Une équipe est déjà sauvée avec le nom " + teamName + ".");
    }

    public void showTeamNotFoundAlert(String teamName) {
        this.showAlert(TEAM_NAME_NOT_FOUND, "Aucune équipe sauvegardée avec le nom " + teamName + ".");
    }

    public void showRenameTeamNoActiveTeamAlert() {
        this.showAlert("Aucune équipe active", "Sélectionnez l'équipe que vous souhaitez renommer.");
    }

    /**
     * Clears the save team name input.
     */
    public void clearTeamNameToSave() {
        this.saveTeamNameInput.setText("");
    }

    public void setIsActiveTeamSaved(boolean isActiveTeamSaved) {
        this.isBugemonTeamSaved = isActiveTeamSaved;
    }

    public interface Listener {
        void onReturnToMainMenu();

        void onSave(String teamName);

        void onLoad(String teamName);

        void onDelete(String teamName);

        void onRename(String oldName, String newName);

        void onAddNewTeam();

        void onBugemonSelected(Bugemon bugemon);

        void onModifyTeam();

        void onStartAutomaticCombat();

        void onStartManualCombat();

        void onStartNOTowerCombat();
    }

    public void setMode(TeamFormMode mode) {
        if (mode == TeamFormMode.CREATE) {
            this.modifyTeamButton.setVisible(false);
            this.modifyTeamButton.setManaged(false);

            this.renameTeamButton.setVisible(false);
            this.renameTeamButton.setManaged(false);

            this.deleteTeamButton.setVisible(false);
            this.deleteTeamButton.setManaged(false);

            this.startAutomaticCombatButton.setVisible(false);
            this.startAutomaticCombatButton.setManaged(false);

            this.startManualCombatButton.setVisible(false);
            this.startManualCombatButton.setManaged(false);

            this.startTowerCombatButton.setVisible(false);
            this.startTowerCombatButton.setManaged(false);
        }
    }

    public void showAlertChooseTeamToModify() {
        this.showNoActiveTeamAlert("Veuillez choisir une equipe à modifier.");
    }

    /**
     * Displays a warning dialog to warn the user that there are unsaved changes. He can choose to continue or go back.
     *
     * @return (boolean) true if the user wants to continue, false if he wants to go back
     */
    public boolean showAlertTeamChangesNotSave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Equipe non sauvegardée");
        alert.setHeaderText(null);
        alert.setContentText(
                "Nouvelle équipe ou équipe existante modifiée non sauvegardée. Donnez lui un nom et sauvegardez la.");

        if (this.root != null && this.root.getScene() != null) {
            alert.initOwner(this.root.getScene().getWindow());
        }

        ButtonType continueBtn = new ButtonType("Continuer"); // L'utilisateur accepte de perdre les modifs
        ButtonType backBtn = new ButtonType("Retour", ButtonBar.ButtonData.CANCEL_CLOSE); // L'utilisateur reste ici

        alert.getButtonTypes().setAll(continueBtn, backBtn);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == continueBtn;
    }
}
