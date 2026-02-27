package ulb.controllers;

import java.io.IOException;

import ulb.views.combat.AutomaticCombatView;

public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    
    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
        this.view.setController(this);
    }
    
    public void runCombat() {
        // TODO: implement the logic to run the combat automatically, 
        // including turn order, actions, and updating the view accordingly.
    }
}
