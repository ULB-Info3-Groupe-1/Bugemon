package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.CombatDefeatView;

/** Controller for the defeat screen; offers retry (→ CREATE_TEAM) or back to main menu. */
public class CombatDefeatController extends Controller<CombatDefeatView> {
    public CombatDefeatController(MetaController metaController) throws IOException {
        super(metaController, new CombatDefeatView());
        this.view.setOnRetry(this::retry);
        this.view.setOnBackToMainMenu(this::backToMainMenu);
    }

    public void retry() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    public void backToMainMenu() {
        this.metaController.switchTo(Window.MAIN_MENU);
    }
}
