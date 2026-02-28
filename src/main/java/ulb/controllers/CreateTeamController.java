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
    private final Map<String, Bugemon> allBugemonsById; // TODO: This will have to move somewhere else

    public CreateTeamController(MetaController metaController) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);

        this.bugemonTeam = new BugemonTeam();
        this.allBugemonsById = new HashMap<>();

        this.updateAllBugemonsView();
        this.updateBugemonsTeamView();
    }

    public void onBugemonClicked(String id) {
        if (this.bugemonTeam.contains(id)) {
            this.bugemonTeam.removeBugemon(id);
        } else {
            Bugemon bugemon = allBugemonsById.get(id);
            this.bugemonTeam.addBugemon(bugemon);
        }

        this.updateBugemonsTeamView();
        this.updateAllBugemonsView();
    }

    private void updateAllBugemonsView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.allBugemonsById.values());
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

}
