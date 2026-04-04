package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.CombatVictoryView;

/** Controller for the victory screen; navigates to NO Tower or main menu on continue. */
public class CombatVictoryController extends Controller<CombatVictoryView> implements CombatVictoryView.Listener {
    public CombatVictoryController(MetaController metaController) throws IOException {
        super(metaController, new CombatVictoryView());
        this.view.setListener(this);
    }

    @Override
    public void onContinue() {
        if (this.metaController.isNOTowerFlowActive()) {
            this.metaController.switchTo(Window.NOTOWER);
            return;
        }
        this.metaController.switchTo(Window.MAIN_MENU);
    }
}
