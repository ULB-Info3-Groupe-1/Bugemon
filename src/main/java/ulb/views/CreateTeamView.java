package ulb.views;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    @FXML private AllBugemonsGridView allBugemonsGridView;
    @FXML private BugemonTeamView bugemonsTeamView;
    @FXML private Button returnMainMenuBtn;
    @FXML private Button loadTeamBtn;
    @FXML private Button saveTeamBtn;
    @FXML private TextField saveTeamNameInput;
    @FXML private TextField loadTeamNameInput;

    private BugemonTeam bugemonTeam;
    private List<Bugemon> allBugemonsAvailable;
    private Consumer<Bugemon> onGridBugemonClicked;
    private Runnable returnToMainMenu;
    private Runnable load;
    private Runnable save;

    /**
     * Loads the team-creation FXML layout and wires click handlers on the
     * Bugemon grid and the two launch buttons.
     *
     * @throws IOException if the FXML resource cannot be loaded.
     */
    public CreateTeamView() throws IOException {
        super(FXML_PATH);

        this.allBugemonsGridView.setOnClickCallback(b -> {
            if (onGridBugemonClicked != null)
                onGridBugemonClicked.accept(b);
        });

        this.returnMainMenuBtn.setOnAction(e -> returnToMainMenu(returnToMainMenu));
        this.loadTeamBtn.setOnAction(e -> loadTeam(load));
        this.saveTeamBtn.setOnAction(e -> saveTeam(save));
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

    public void returnToMainMenu(Runnable validate) {
        if (validate != null) {
            validate.run();
        }
    }

    public void setLoad(Runnable load) {
        this.load = load;
    }

    public void setSave(Runnable save) {
        this.save = save;
    }

    public void saveTeam(Runnable save) {
        if (save != null) {
            save.run();
        }
    }

    public void loadTeam(Runnable load) {
        if (load != null) {
            load.run();
        }
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
        return this.loadTeamNameInput.getText();
     }
}
