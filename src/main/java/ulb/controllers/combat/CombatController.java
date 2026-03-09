package ulb.controllers.combat;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.combat.CombatView;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;

public abstract class CombatController<View extends CombatView> extends Controller<View> {
    
    public CombatController(MetaController metaController, View view) {
        super(metaController, view);
    }

    /**
     * Verify if the winner is the player or not and handle victory or defeat of the winner
     * @param winner The Trainer winner of the combat
     * @param player The Trainer player
     */
    protected void handleCombatResult(Trainer winner, Trainer player) {
        if (winner == player) {this.metaController.switchTo(Window.COMBAT_VICTORY);}
        else {this.metaController.switchTo(Window.COMBAT_DEFEAT);}
    }

    /**
     * Update the combat view with the current state of the player's and opponent's bugemon
     * @param player the player trainer whose bugemon is being updated in the view
     * @param opponent the opponent trainer whose bugemon is being updated in the view
     */
    public void updateCombatView(Trainer player, AutoTrainer opponent) {
        this.view.updateTrainerBugemon(player.getCurrentBugemon());
        this.view.updateOpponentBugemon(opponent.getCurrentBugemon());
    }
}
