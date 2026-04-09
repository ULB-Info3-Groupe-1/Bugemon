package ulb.controllers.combat;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.CombatVictoryView;
import ulb.views.ViewLoader;

/** Controller for the victory screen; navigates to NO Tower or main menu on continue. */
public class CombatVictoryController extends Controller<CombatVictoryView> implements CombatVictoryView.Listener {
    public CombatVictoryController(MetaController metaController) {
        super(metaController, ViewLoader.load(CombatVictoryView::new));
        this.view.setListener(this);
    }

    @Override
    public void onContinue() {
        // TODO: wtf ?
        if (this.metaController.isNOTowerFlowActive()) {
            this.metaController.switchTo(Window.NOTOWER);
            return;
        }

        this.metaController.onCombatVictoryFinished();
    }
}
