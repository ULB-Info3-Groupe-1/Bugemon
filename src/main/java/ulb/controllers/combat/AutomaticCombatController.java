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
        this.view.initCombatMode();

        AutoTrainer opponent = new AutoTrainer(BugemonTeam.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        Trainer winner = null;
        while (winner == null) {
            combat.turn();
            winner = (AutoTrainer) combat.getWinner();
            combat.incrementTurn();
            updateCombatView(player, opponent);
        }
        handleCombatResult(winner, player);
    }

    private void updateCombatView(AutoTrainer player, AutoTrainer opponent) {
        for (int i = 0; i < player.getTeamSize(); i++) {
            view.updateTrainerBugemon(player.getTeam().get(i));
        }

        for (int i = 0; i < opponent.getTeamSize(); i++) {
            view.updateOpponentBugemon(opponent.getTeam().get(i));
        }

        try {
            Thread.sleep(1000); // wait for 1 second before the next turn to allow the player to see the changes
        } catch (InterruptedException e) {
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
