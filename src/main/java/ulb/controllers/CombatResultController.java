package ulb.controllers;

import java.io.IOException;

import ulb.views.CombatResultView;
import ulb.controllers.MetaController.Window;

public class CombatResultController extends Controller<CombatResultView> {

    public CombatResultController(MetaController metaController) throws IOException {
        super(metaController, new CombatResultView());
        this.view.setController(this);
    }

    public void backToMainMenu() {
        this.metaController.switchTo(Window.MAIN_MENU);
    }

}
