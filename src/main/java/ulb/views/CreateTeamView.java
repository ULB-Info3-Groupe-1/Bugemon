package ulb.views;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * View for the team creation screen.
 *
 * <p>
 * Holds a reference to the {@link BugemonTeam} model and reads from it directly
 * in {@link #refresh()}. Dispatches user interactions through callbacks; holds
 * no reference to any concrete controller class.
 * </p>
 */
public class CreateTeamView extends View {
    private static final String FXML_PATH = "/fxml/CreateTeam.fxml";
    private static final String NO_TEAM_SELECTED = "Pas d'équipe sélectionnée";

    @FXML private AllBugemonsGridView allBugemonsGridView;
    @FXML private BugemonTeamView bugemonsTeamView;
    @FXML private Button returnMainMenuBtn;
    @FXML private Button saveTeamBtn;
    @FXML private Button deleteTeamBtn;
    @FXML private Button renameTeamBtn;
    @FXML private Button addNewTeamBtn;
    @FXML private TextField saveTeamNameInput;
    @FXML private ListView<String> teamListView;
    @FXML private Text selectedTeamName;

    private BugemonTeam bugemonTeam;
    private List<Bugemon> allBugemonsAvailable;
    private Consumer<Bugemon> onGridBugemonClicked;
    private Runnable returnToMainMenu;
    private Runnable load;
    private Runnable save;
    private Runnable addNewTeam;
    private Consumer<String> delete;
    private BiConsumer<String, String> rename;

    /** @throws IOException if the FXML resource cannot be loaded. */
    public CreateTeamView() throws IOException {
        super(FXML_PATH);
        this.selectedTeamName.setText(NO_TEAM_SELECTED);
        initHandlers();
    }

    private void initHandlers() {
        this.allBugemonsGridView.setOnClickCallback(b -> {
            if (onGridBugemonClicked != null)
                onGridBugemonClicked.accept(b);
        });

        this.returnMainMenuBtn.setOnAction(e -> returnToMainMenu.run());
        this.saveTeamBtn.setOnAction(e -> save.run());
        this.saveTeamNameInput.setOnAction(e -> save.run());
        this.addNewTeamBtn.setOnAction(e -> addNewTeam.run());

        this.deleteTeamBtn.setOnAction(e -> {
            delete.accept(getTeamNameToLoad());
            selectedTeamName.setText(NO_TEAM_SELECTED);
        });

        this.renameTeamBtn.setOnAction(
                e -> rename.accept(getTeamNameToLoad(), getTeamNameToSave()));
        this.teamListView.setOnMouseClicked(e -> {
            String selected = this.teamListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                this.selectedTeamName.setText(selected);
                load.run();
            } else {
                this.selectedTeamName.setText(NO_TEAM_SELECTED);
            }
        });
    }

    /** Gives the view a reference to the team model it should read from. */
    public void setModel(BugemonTeam bugemonTeam) {
        this.bugemonTeam = bugemonTeam;
        this.allBugemonsGridView.setSelectionChecker(
                b -> this.bugemonTeam.stream().anyMatch(dto -> dto.getId().equals(b.getId())));
    }

    /**
     * Gives the view a reference to the list of all available Bugemons, so it can
     * display them in the selection grid and mark the ones already in the team as
     * selected.
     * @param allBugemons
     */
    public void setAllBugemonsAvailable(List<Bugemon> allBugemons) {
        this.allBugemonsAvailable = allBugemons;
        this.allBugemonsGridView.showAll(this.allBugemonsAvailable);
    }

    /** Registers the callback invoked when the player clicks a Bugemon in the selection grid. */
    public void setOnGridBugemonClicked(Consumer<Bugemon> callback) {
        this.onGridBugemonClicked = callback;
    }

    public void refreshTeam(BugemonTeam team) {
        this.bugemonTeam = team;
        refresh();
    }

    @Override
    public void refresh() {
        this.allBugemonsGridView.showAll(this.allBugemonsAvailable);
        this.bugemonsTeamView.showTeam(this.bugemonTeam);
    }

    public void setValidate(Runnable returnToMainMenu) {
        this.returnToMainMenu = returnToMainMenu;
    }

    public void setLoad(Runnable load) {
        this.load = load;
    }

    public void setSave(Runnable save) {
        this.save = save;
    }

    public void setDelete(Consumer<String> delete) {
        this.delete = delete;
    }

    public void setRename(BiConsumer<String, String> rename) {
        this.rename = rename;
    }

    public void setAddNewTeam(Runnable addNewTeam) {
        this.addNewTeam = addNewTeam;
    }

    public void updateTeamList(List<String> teamNames) {
        this.teamListView.setItems(FXCollections.observableArrayList(teamNames));
    }

    public void setSaveTeamName(String name) {
        this.saveTeamNameInput.setText(name);
    }

    /**
     * @return the team name currently entered in the text field for saving teams.
     */
    public String getTeamNameToSave() {
        return this.saveTeamNameInput.getText();
    }

    /**
     * @return the team name currently entered in the text field of the load team input.
     */
    public String getTeamNameToLoad() {
        return this.selectedTeamName.getText();
    }
}
