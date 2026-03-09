package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ulb.common.BugemonDTO;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.views.CreateTeamView;

public class CreateTeamController extends Controller<CreateTeamView> {

    private BugemonTeam bugemonTeam;

    /**
     * Constructor for the CreateTeamController class.
     * @param metaController the meta controller of the application
     * @param bugemonTeam the bugemon team of the trainer, which is a list of bugemon
     * @throws IOException if the view fails to initialize
     */
    public CreateTeamController(MetaController metaController, BugemonTeam bugemonTeam) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);

        this.bugemonTeam = bugemonTeam;

        this.updateAllBugemonsView();
        this.updateBugemonsTeamView();
    }

    /**
     * Callback invoked when the user clicks on a bugemon in the list of all bugemon or in the team.
     * @param id the id of the clicked bugemon
     */
    public void onBugemonClicked(String id) {
        if (this.bugemonTeam.contains(id)) {
            this.bugemonTeam.removeBugemon(id);
        } else if (this.bugemonTeam.isFull()) {
            metaController.showAlert("Team Full", "Your team is full! Please remove a Bugemon before adding another one.");
        } else {
            metaController.getAllBugemonsAvailable().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .ifPresent(bugemon -> this.bugemonTeam.addBugemon(bugemon));
        }

        this.updateBugemonsTeamView();
        this.updateAllBugemonsView();
    }

    /**
     * Updates the view of all bugemon and the view of the team of the trainer.
     */
    private void updateAllBugemonsView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(metaController.getAllBugemonsAvailable());
        this.view.showAll(bugemonList);
    }

    /**
     * Updates the view of the team of the trainer.
     */
    private void updateBugemonsTeamView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.bugemonTeam);
        this.view.showTeam(bugemonList);
    }

    /**
     * Warns the metaController to launch the AutoCombat
     */
    public void startAutoCombat() {
        this.metaController.launchAutoCombat();
    }

    /**
     * Warns the metaController to launch the ManuelCombat
     */
    public void startManuelCombat() {
        this.metaController.launchManuelCombat();
    }

    /**
     * Checks if the bugemon with the given id is already in the team of the trainer.
     * @param bugemonId the id of the bugemon to check
     * @return true if the bugemon is in the team, false otherwise
     */
    public boolean checkBugemonInTeam(String bugemonId) {
        return (this.bugemonTeam.contains(bugemonId));
    }
}
