package bugemon.client.controllers.combat;

import bugemon.client.controllers.Controller;
import bugemon.client.controllers.MetaController;
import bugemon.client.views.CombatVictoryView;
import bugemon.client.views.ViewLoader;

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
