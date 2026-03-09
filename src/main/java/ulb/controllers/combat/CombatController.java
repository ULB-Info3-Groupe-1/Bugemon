package ulb.controllers.combat;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.combat.CombatView;
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
}
