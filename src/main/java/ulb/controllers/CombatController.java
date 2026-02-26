package ulb.controllers;

import java.io.IOException;

import ulb.views.CombatView;
import ulb.controllers.MetaController.Window;

public class CombatController extends Controller<CombatView> {

    public CombatController(MetaController metaController) throws IOException {
        super(metaController, new CombatView());
        this.view.setController(this);
    }

    public void handleVictory() {
        this.metaController.switchTo(Window.COMBAT_RESULT);
    }

    public void handleDefeat() {
        this.metaController.switchTo(Window.COMBAT_RESULT);
    }

}
