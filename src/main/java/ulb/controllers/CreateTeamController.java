package ulb.controllers;

import java.io.IOException;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;
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
    private final BugemonTeam bugemonTeam;

    public CreateTeamController(MetaController metaController, BugemonTeam bugemonTeam)
            throws IOException {
        super(metaController, new CreateTeamView());
        this.bugemonTeam = bugemonTeam;

        this.view.setModel(bugemonTeam);
        this.view.setOnBugemonClicked(this::onBugemonClicked);
        this.view.setOnStartAutoCombat(this::startAutoCombat);
        this.view.setOnStartManualCombat(this::startManualCombat);
        this.view.refresh();
    }

    /** Toggles the Bugemon identified by {@code id} in the player's team. */
    public void onBugemonClicked(String id) {
        if (this.bugemonTeam.contains(id)) {
            this.bugemonTeam.removeBugemon(id);
        } else {
            Parser.getInstance()
                    .getBugemons()
                    .stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst()
                    .ifPresent(bugemon -> this.bugemonTeam.addBugemon(bugemon));
        }
        this.view.refresh();
    }

    /** Launches an automatic combat session. */
    public void startAutoCombat() {
        this.metaController.launchAutoCombat();
    }

    /** Launches a manual combat session. */
    public void startManualCombat() {
        this.metaController.launchManualCombat();
    }
}
