package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.AutomaticCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.views.combat.AutomaticCombatView;

public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    
    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
        this.view.setController(this);
    }
    
    /**
     * Run an auto combat
     * @param playerTeam the team of the player
     */
    public void runAutoCombat(final AutoTrainer player) {
        AutoTrainer opponent = new AutoTrainer(BugemonTeam.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        Trainer winner = null;
        while (winner == null) {
            combat.turn();
            winner = (AutoTrainer) combat.getWinner();
            combat.incrementTurn();
        }
        handleCombatResult(winner, player);
    }
}
