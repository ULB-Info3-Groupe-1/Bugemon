package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ulb.common.BugemonDTO;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.views.CreateTeamView;

public class CreateTeamController extends Controller<CreateTeamView> {

    private BugemonTeam bugemonTeam;
    private final List<Bugemon> bugemonList;

    public CreateTeamController(
        MetaController metaController,
        List<Bugemon> bugemonList
    ) throws IOException {
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
            this.bugemonList.stream()
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

    /**
     * Warns the metaController to launch the AutoCombat
     */
    public void startAutoCombat() {
        this.metaController.switchTo(Window.COMBAT);
        this.metaController.launchAutoCombat(new AutoTrainer(this.bugemonTeam));
    }

    /**
     * Warns the metaController to launch the ManuelCombat
     */
    public void startManuelCombat() {
        this.metaController.switchTo(Window.COMBAT);
        this.metaController.launchManuelCombat(new ManualTrainer(this.bugemonTeam));
    }

    public boolean checkBugemonInTeam(String bugemonId) {
        return (this.bugemonTeam.contains(bugemonId));
    }
}
