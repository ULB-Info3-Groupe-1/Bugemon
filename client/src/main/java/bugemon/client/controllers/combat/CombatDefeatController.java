package bugemon.client.controllers.combat;

import bugemon.client.controllers.Controller;
import bugemon.client.controllers.MetaController;
import bugemon.client.views.CombatDefeatView;
import bugemon.client.views.ViewLoader;

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
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
