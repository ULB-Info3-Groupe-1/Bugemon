package ulb.controllers.combat;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.views.ViewLoader;
import ulb.views.combat.CombatVictoryView;

/** Controller for the victory screen; navigates to main menu or level-up on continue. */
public class CombatVictoryController extends Controller<CombatVictoryView> implements CombatVictoryView.Listener {
    public CombatVictoryController(MetaController metaController) {
        super(metaController, ViewLoader.load(CombatVictoryView::new));
        this.view.setListener(this);
    }

    @Override
    public void onContinue() {
        this.metaController.onCombatVictoryFinished();
    }
}
