package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.views.MainMenuView;

/**
 * MainMenuController
 *
 * Controller responsible for the main menu screen.
 */
public class MainMenuController extends Controller<MainMenuView> {

    /**
     * @param metaController
     * @throws IOException
     */
    public MainMenuController(MetaController metaController) throws IOException {
        super(metaController, new MainMenuView());
        this.view.setController(this);
    }

    /**
     * Callback invoked when the player wants to create a team.
     */
    public void createTeam() {
        this.metaController.switchTo(Window.CREATE_TEAM);
    }

    /**
     * Callback invoked when the player wants to quit the application.
     */
    public void quit() {
        javafx.application.Platform.exit();
    }

}
