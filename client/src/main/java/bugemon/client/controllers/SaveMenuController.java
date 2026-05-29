package bugemon.client.controllers;

import javafx.application.Platform;

import bugemon.client.views.SaveMenuView;
import bugemon.client.views.ViewLoader;

/**
 * Controller for the save/load screen shown on application startup.
 *
 * <p>
 * Offers three actions: start a new game (asks the server to wipe all player data, then rebuilds the session), continue
 * the existing game, or quit. All persistence is delegated to the server through {@link MetaController}; this controller
 * holds no service or database reference.
 */
public class SaveMenuController extends Controller<SaveMenuView> implements SaveMenuView.Listener {

    public SaveMenuController(MetaController metaController) {
        super(metaController, ViewLoader.load(SaveMenuView::new));
        this.view.setListener(this);
    }

    @Override
    public void onNewGame() {
        this.metaController.onNewGame();
    }

    @Override
    public void onContinue() {
        this.metaController.onMainMenu();
    }

    @Override
    public void onQuit() {
        Platform.exit();
    }
}
