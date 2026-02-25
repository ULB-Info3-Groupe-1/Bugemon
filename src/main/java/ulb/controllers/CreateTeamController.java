package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.CreateTeamView;

public class CreateTeamController extends Controller<CreateTeamView> {

    public CreateTeamController(MetaController metaController) throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);
    }

    public void startCombat() {
        this.metaController.switchTo(Window.COMBAT);
    }

    public void addToTeam(String bugemonId) {
        System.out.println("adding: " + bugemonId);
        // TODO: impl
    }

    public void removeFromTeam(String bugemonId) {
        System.out.println("removing" + bugemonId);
        // TODO: impl
    }

}
