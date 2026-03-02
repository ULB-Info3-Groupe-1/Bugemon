package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulb.common.BugemonDTO;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.UtilsBugemons;
import ulb.views.CreateTeamView;

public class CreateTeamController extends Controller<CreateTeamView> {

    private BugemonTeam bugemonTeam;
    private final List<Bugemon> bugemonList;

    public CreateTeamController(MetaController metaController, List<Bugemon> bugemonList) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);
        
        this.bugemonList = bugemonList;

        this.bugemonTeam = new BugemonTeam();

        this.updateAllBugemonsView();
        this.updateBugemonsTeamView();
    }

    public void onBugemonClicked(String id) {
        if (this.bugemonTeam.contains(id)) {
            this.bugemonTeam.removeBugemon(id);
        } else {
            this.bugemonList
            .stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .ifPresent(bugemon -> this.bugemonTeam.addBugemon(bugemon));
        }

        this.updateBugemonsTeamView();
        this.updateAllBugemonsView();
    }

    private void updateAllBugemonsView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.bugemonList);
        this.view.showAll(bugemonList);
    }

    private void updateBugemonsTeamView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.bugemonTeam.getTeam());
        this.view.showTeam(bugemonList);
    }

    public void startCombat() {
        this.metaController.switchTo(Window.COMBAT);
    }

    public boolean checkBugemonInTeam(String bugemonId) {
        return (this.bugemonTeam.contains(bugemonId));
    }

    /**
     * Return the team chosen by the player to run a combat
     * @return BugemonTeam the team of bugemons chosen by the player
     */
    public final BugemonTeam getFinalTeam() {
        // TODO: change method to get the real player team
        BugemonTeam bgTeam = new BugemonTeam();
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("1"));
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("2"));
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("3"));
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("4"));
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("5"));
        bgTeam.addBugemon(UtilsBugemons.createDefaultBugemon("6"));
        return bgTeam;
    }

}
