package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.Player;
import ulb.views.CreateTeamView;

/**
 * Controller responsible for the team creation screen.
 *
 * <p>
 * Mutates the {@link BugemonTeam} model in response to user actions, then calls
 * {@code view.refresh()} so the view can pull the updated state from the model
 * directly. The controller never pushes data into the view.
 * </p>
 */
public class CreateTeamController extends Controller<CreateTeamView> {
    private final Player player;
    private final List<Bugemon> selectedTeam;

    private static final int MAX_TEAM_SIZE = 6;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and
     * performs an initial {@link ulb.views.CreateTeamView#refresh()} to populate
     * the Bugemon grid.
     *
     * @param metaController the application-level controller used for navigation.
     * @param bugemonTeam    the player's team model to mutate in response to selections.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public CreateTeamController(MetaController metaController, Player player) throws IOException {
        super(metaController, new CreateTeamView());
        this.player = player;
        this.selectedTeam = new ArrayList<Bugemon>();

        this.view.setModel(player.getActiveTeam());
        this.view.setOnGridBugemonClicked(this::onBugemonClicked);
        this.view.setOnStartAutoCombat(this::startAutoCombat);
        this.view.setOnStartManualCombat(this::startManualCombat);
        this.view.refresh();
    }

    /** Toggles {@code bugemon} in the player's selected team. */
    public void onBugemonClicked(Bugemon bugemon) {
        if (this.selectedTeam.contains(bugemon)) {
            this.selectedTeam.remove(bugemon);
        } else if (this.selectedTeam.size() < MAX_TEAM_SIZE) {
            this.selectedTeam.add(bugemon);
            this.view.refreshTeam(this.selectedTeam);
        }
        this.view.refresh();
    }

    /** Launches an automatic combat session. */
    public void startAutoCombat() {
        this.player.setActiveTeam(this.selectedTeam);
        this.metaController.switchTo(MetaController.Window.AUTOMATIC_COMBAT);
    }

    /** Launches a manual combat session. */
    public void startManualCombat() {
        this.player.setActiveTeam(this.selectedTeam);
        this.metaController.switchTo(MetaController.Window.MANUAL_COMBAT);
    }
}
