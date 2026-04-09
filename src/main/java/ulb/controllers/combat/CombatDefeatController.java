package ulb.controllers.combat;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.views.CombatDefeatView;
import ulb.views.ViewLoader;

/** Controller for the defeat screen; offers retry (→ CREATE_TEAM) or back to main menu. */
public class CombatDefeatController extends Controller<CombatDefeatView> implements CombatDefeatView.Listener {
    public CombatDefeatController(MetaController metaController) {
        super(metaController, ViewLoader.load(CombatDefeatView::new));
        this.view.setListener(this);
    }

    @Override
    public void onRetry() {
        this.metaController.onCombatDefeatRetry();
    }

    @Override
    public void onBackToMainMenu() {
        this.metaController.onCombatDefeatBackToMainMenu();
    }
}
