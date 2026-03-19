package ulb.controllers;

import java.io.IOException;

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
    private final PlayerService playerService;
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
    public CreateTeamController(MetaController metaController, PlayerService playerService)
            throws IOException {
        super(metaController, new CreateTeamView());
        this.playerService = playerService;
        this.selectedTeam = new BugemonTeam();

        this.view.setModel(this.selectedTeam);
        this.view.setValidate(this::returnToMainMenu);
        this.view.setLoad(this::loadTeam);
        this.view.setSave(this::saveTeam);
        this.view.setAllBugemonsAvailable(this.playerService.getAllDefaultBugemons());
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
            try {
                this.selectedTeam.add(bugemon.clone());
            } catch (CloneNotSupportedException e) {
                // TODO: Clone method should not implement Cloneable (reconstruct bugemon)
            }
        }
        this.view.refreshTeam(this.selectedTeam);
    }

    public void returnToMainMenu(){
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }

    public void saveTeam(){

    }

    public void loadTeam(){

    }


}
