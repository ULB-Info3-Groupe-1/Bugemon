package ulb.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;

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
    @FXML private Button launchAutomaticCombat;
    @FXML private Button launchManualCombat;

    private BugemonTeam bugemonTeam;
    private Consumer<Bugemon> onGridBugemonClicked;
    private Runnable onStartAutoCombat;
    private Runnable onStartManualCombat;

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

        this.launchAutomaticCombat.setOnAction(e -> launchCombat(onStartAutoCombat));
        this.launchManualCombat.setOnAction(e -> launchCombat(onStartManualCombat));
    }

    /** Gives the view a reference to the team model it should read from. */
    public void setModel(BugemonTeam bugemonTeam) {
        this.bugemonTeam = bugemonTeam;
        this.allBugemonsGridView.setSelectionChecker(
                b -> this.bugemonTeam.stream().anyMatch(dto -> dto.getId().equals(b.getId())));
    }

    /** Registers the callback invoked when the player clicks a Bugemon in the selection grid. */
    public void setOnGridBugemonClicked(Consumer<Bugemon> callback) {
        this.onGridBugemonClicked = callback;
    }

    /** Registers the callback invoked when the player launches an automatic combat. */
    public void setOnStartAutoCombat(Runnable callback) {
        this.onStartAutoCombat = callback;
    }

    /** Registers the callback invoked when the player launches a manual combat. */
    public void setOnStartManualCombat(Runnable callback) {
        this.onStartManualCombat = callback;
    }

    private void launchCombat(Runnable onStart) {
        if (bugemonTeam != null && bugemonTeam.isEmpty()) {
            showAlert("Équipe incomplète",
                      "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
        } else if (onStart != null) {
            onStart.run();
        }
    }

    public void refreshTeam(BugemonTeam team) {
        this.bugemonTeam = team;
        refresh();
    }

    @Override
    public void refresh() {
        List<Bugemon> allBugemons = new ArrayList<>(Parser.getInstance().getBugemons());
        this.allBugemonsGridView.showAll(allBugemons);
        this.bugemonsTeamView.showTeam(this.bugemonTeam);
    }
}
