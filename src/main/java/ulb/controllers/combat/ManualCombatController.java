package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.combat.ManualCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.views.combat.ManualCombatView;

public class ManualCombatController extends CombatController<ManualCombatView> {

    /**
     * Constructor of the ManualCombatController which initializes the view and sets the controller for the view
     * @param metaController The MetaController of the application
     * @throws IOException if the view cannot be initialized
     */
    public ManualCombatController(MetaController metaController) throws IOException {
        super(metaController, new ManualCombatView());
        this.view.setController(this);
    }

    /**
     * Run a manuel combat
     * @param playerTeam the team of the player
     */
    public void runManuelCombat(final ManualTrainer player) {
        AutoTrainer opponent = new AutoTrainer(TeamFactory.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        ManualCombat combat = new ManualCombat(player, opponent);

        updateCombatView(player, opponent);

        Trainer winner = null;
        while (winner == null) {
            winner = combat.turn(null); // TODO: get the action from the GUI
        }
        handleCombatResult(winner, player);
    }
}
