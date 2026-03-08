package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.CombatVictoryView;

public class CombatVictoryController extends Controller<CombatVictoryView> {

    /**
     * Constructor of the CombatVictoryController which initializes the view and sets the controller for the view
     * @param metaController The MetaController of the application
     * @throws IOException if the view cannot be initialized
     */
    public CombatVictoryController(MetaController metaController) throws IOException {
        super(metaController, new CombatVictoryView());
        this.view.setController(this);
    }

    /**
     * Callback invoked when the user presses the continue button.
     *
     * Switches to the level up screen if a bugemon leveled up;
     * otherwise returns to the main menu. (WARN: not yet implemented)
     *
     * NOTE: cont stands for continue
     */
    public void cont() {
        // TODO: impl

        // if level up -> switch to level up screen
        // otherwise -> back to main menu
        this.metaController.switchTo(Window.MAIN_MENU);
        this.metaController.resetTeam();
    }

}
