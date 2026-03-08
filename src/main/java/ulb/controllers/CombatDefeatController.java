package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.CombatDefeatView;

public class CombatDefeatController extends Controller<CombatDefeatView> {

    /**
     * Constructor of the CombatDefeatController which initializes the view and sets the controller for the view
     * @param metaController The MetaController of the application
     * @throws IOException if the view cannot be initialized
     */
    public CombatDefeatController(MetaController metaController) throws IOException {
        super(metaController, new CombatDefeatView());
        this.view.setController(this);
    }

    /**
     * Callback invoked when the user presses the retry button.
     */
    public void retry() {
        // TODO: impl
        this.metaController.switchTo(Window.CREATE_TEAM);
        this.metaController.resetTeam();
    }

    /**
     * Callback invoked when the user switches back to the main menu.
     */
    public void backToMainMenu() {
        this.metaController.switchTo(Window.MAIN_MENU);
        this.metaController.resetTeam();
    }

}
