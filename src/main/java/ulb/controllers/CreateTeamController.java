package ulb.controllers;

import java.io.IOException;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.PlayerService;
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
    private final BugemonTeam selectedTeam;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and
     * performs an initial {@link ulb.views.CreateTeamView#refresh()} to populate
     * the Bugemon grid.
     *
     * @param metaController the application-level controller used for navigation.
     * @param playerService the service used to access and mutate player data.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public CreateTeamController(MetaController metaController)
            throws IOException {
        super(metaController, new CreateTeamView());
        this.selectedTeam = new BugemonTeam();

        this.view.setModel(this.selectedTeam);
        this.view.setValidate(this::returnToMainMenu);
        this.view.setLoad(this::loadTeam);
        this.view.setSave(this::saveTeam);
        this.view.setAllBugemonsAvailable(PlayerService.getInstance().getAllDefaultBugemons());
        this.view.setOnGridBugemonClicked(this::toggleBugemonSelection);
        this.view.refresh();
    }

    /** Toggles {@code bugemon} in the player's selected team. */
    public void toggleBugemonSelection(Bugemon bugemon) {
        // TODO: handle exceptions thrown by BugemonTeam
        // + change logic
        if (this.selectedTeam.contains(bugemon)) {
            this.selectedTeam.remove(bugemon);
        } else if (!this.selectedTeam.isFull()) {
            this.selectedTeam.add(bugemon.clone());
        }
        this.view.refreshTeam(this.selectedTeam);
    }

    public void returnToMainMenu() {
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }

    public void saveTeam() {
        PlayerService.getInstance().saveTeam("test_1", this.selectedTeam);
    }

    public void loadTeam() {
        List<Bugemon> team = PlayerService.getInstance().loadTeam("test_1");
        this.selectedTeam.clear();
        team.forEach(this.selectedTeam::add);
        this.view.refreshTeam(this.selectedTeam);
    }
}
