package ulb.views;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.views.components.AllBugemonsView;
import ulb.views.components.BugemonTeamView;

/**
 * View for the team creation screen.
 *
 * <p>
 * Holds a reference to the {@link BugemonTeam} model and reads from it directly in {@link #refresh()}. Dispatches user
 * interactions through a {@link Listener}; holds no reference to any concrete controller class.
 * </p>
 */
public class CreateTeamView extends View {
    private static final String NO_TEAM_SELECTED = "Pas d'équipe sélectionnée";
    /** Number of team slots always visible in the list; matches {@code size-team-list-height / size-team-slot-height}. */
    private static final int VISIBLE_TEAM_SLOTS = 3;

    private final String fxmlPath = "/fxml/CreateTeam.fxml";

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

    private Listener listener;
    private BugemonTeam bugemonTeam;
    private List<Bugemon> allBugemonsAvailable;

    @FXML
    private void initialize() {
        this.selectedTeamName.setText(NO_TEAM_SELECTED);
        this.allBugemonsGridView.setOnClick(b -> {
            if (this.listener != null) {
                this.listener.onBugemonSelected(b);
            }
        });
        this.teamListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                this.initTeamListScrollBehavior();
            }
        });
    }

    private void initTeamListScrollBehavior() {
        ScrollBar sb = (ScrollBar) this.teamListView.lookup(".scroll-bar:vertical");
        if (sb == null) {
            return;
        }
        this.teamListView.addEventFilter(ScrollEvent.SCROLL, event -> {
            int items = this.teamListView.getItems().size();
            int scrollable = items - VISIBLE_TEAM_SLOTS;
            if (scrollable > 0) {
                double step = 1.0 / scrollable;
                double newVal = sb.getValue() + (event.getDeltaY() < 0 ? step : -step);
                sb.setValue(Math.max(0.0, Math.min(1.0, newVal)));
            }
            event.consume();
        });
    }

    @Override
    public String getPath() {
        return this.fxmlPath;
    }

    /** Registers the listener that receives all user interaction events from this view. */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onSaveClicked() {
        this.listener.onSave();
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
    private void onTeamSelected(MouseEvent event) {
        String selected = this.teamListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.selectedTeamName.setText(selected);
            this.listener.onLoad();
        } else {
            this.selectedTeamName.setText(NO_TEAM_SELECTED);
        }
    }

    /** Gives the view a reference to the team model it should read from. */
    public void setModel(BugemonTeam newBugemonTeam) {
        this.bugemonTeam = newBugemonTeam;
        this.allBugemonsGridView
                .setSelectionChecker(b -> this.bugemonTeam.stream().anyMatch(dto -> dto.getName().equals(b.getName())));
    }

    /**
     * Sets the full list of available Bugemons and displays them in the selection grid.
     */
    public void setAllBugemonsAvailable(List<Bugemon> allBugemons) {
        this.allBugemonsAvailable = allBugemons;
        this.allBugemonsGridView.showAll(this.allBugemonsAvailable);
    }

    /** Updates the model reference and refreshes the view. */
    public void refreshTeam(BugemonTeam team) {
        this.bugemonTeam = team;
        this.refresh();
    }

    @Override
    public void refresh() {
        this.allBugemonsGridView.showAll(this.allBugemonsAvailable);
        this.bugemonsTeamView.showTeam(this.bugemonTeam);
    }

    /** Replaces the team list displayed in the ListView. */
    public void updateTeamList(List<String> teamNames) {
        this.teamListView.setItems(FXCollections.observableArrayList(teamNames));
    }

    public void setSaveTeamName(String name) {
        this.saveTeamNameInput.setText(name);
    }

    public String getTeamNameToSave() {
        return this.saveTeamNameInput.getText();
    }

    public String getTeamNameToLoad() {
        return this.selectedTeamName.getText();
    }

    /** Callback interface for all user interactions on the team creation screen. */
    public interface Listener {
        void onReturnToMainMenu();

        void onSave();

        void onLoad();

        void onDelete(String teamName);

        void onRename(String oldName, String newName);

        void onAddNewTeam();

        void onBugemonSelected(Bugemon bugemon);
    }
}
