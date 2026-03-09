package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.combat.AutomaticCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.views.combat.AutomaticCombatView;

public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    
    /**
     * Constructor for the AutomaticCombatController class.
     * @param metaController the MetaController instance to manage the overall application state
     * @throws IOException if an I/O error occurs during view initialization
     */
    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
        this.view.setController(this);
    }
    
    /**
     * Run an auto combat
     * @param playerTeam the team of the player
     */
    public void runAutoCombat(final AutoTrainer player) {
        this.view.initCombatMode();

        AutoTrainer opponent = new AutoTrainer(TeamFactory.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        AutoTrainer winner = null;
        while (winner == null) {
            combat.turn();
            winner = (AutoTrainer) combat.getWinner();
            combat.incrementTurn();
        }
        handleCombatResult(winner, player);
    }
}
