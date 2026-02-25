package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.CreateTeamView;

import ulb.models.bugemon.Bugemon;
import ulb.bugemon_team.models.BugemonTeam;
import ulb.common.BugemonDTO;

public class CreateTeamController extends Controller<CreateTeamView> {

    private BugemonTeam bugemonTeam;

    public CreateTeamController(MetaController metaController) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);

        this.bugemonTeam = new BugemonTeam();
    }

    public void startCombat() {
        this.metaController.switchTo(Window.COMBAT);
    }

    public void addToTeam(BugemonDTO bugemon) {
        System.out.println("removing" + bugemon.getId());
        bugemonTeam.addBugemon((Bugemon) bugemon);
    }

    public void removeFromTeam(BugemonDTO bugemon) {
        System.out.println("removing" + bugemon.getId());
        bugemonTeam.removeBugemon((Bugemon) bugemon);
    }

}
