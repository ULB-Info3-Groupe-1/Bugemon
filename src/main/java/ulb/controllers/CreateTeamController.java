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
import ulb.views.CreateTeamView;

public class CreateTeamController extends Controller<CreateTeamView> {

    private BugemonTeam bugemonTeam;
    private Map<String, Bugemon> allBugemonsById;

    public CreateTeamController(MetaController metaController) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);

        this.bugemonTeam = new BugemonTeam();
        this.allBugemonsById = new HashMap<>();

        this.updateAllBugemonsView();
        this.updateBugemonsTeamView();
    }

    private void updateAllBugemonsView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.allBugemonsById.values());
        this.view.showAll(bugemonList);
    }

    private void updateBugemonsTeamView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();

        // TODO: UGLY, should change the return type of getTeam to make this cleaner
        // (cf. updateAllBugemonsView)
        for (Bugemon bugemon : this.bugemonTeam.getTeam()) {
            bugemonList.add(bugemon);
        }

        this.view.showTeam(bugemonList);
    }

    public void startCombat() {
        this.metaController.switchTo(Window.COMBAT);
    }

    public void addToTeam(String bugemonId) {
        System.out.println("adding" + bugemonId);

        // TODO: would probably be cleaner to have a get-by-id method in BugemonTeam
        // instead of usng this map here?
        Bugemon bugemon = allBugemonsById.get(bugemonId);

        bugemonTeam.addBugemon(bugemon);
    }

    public void removeFromTeam(String bugemonId) {
        System.out.println("removing" + bugemonId);

        Bugemon bugemon = this.allBugemonsById.get(bugemonId);
        bugemonTeam.removeBugemon(bugemon);
    }

}
