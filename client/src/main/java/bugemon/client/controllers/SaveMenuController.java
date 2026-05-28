package bugemon.client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.views.SaveMenuView;
import bugemon.client.views.ViewLoader;
import bugemon.common.models.player.PlayerState;
import bugemon.server.services.SaveService;

/**
 * Controller for the save/load screen shown on application startup.
 *
 * <p>
 * Offers three actions: start a new game (clears all player data), continue an existing game, or quit. Delegates to
 * {@link SaveService} for persistence and to {@link MetaController} for navigation.
 */
public class SaveMenuController extends Controller<SaveMenuView> implements SaveMenuView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(SaveMenuController.class);

    private final SaveService saveService;
    private final PlayerState playerState;

    public SaveMenuController(MetaController metaController, SaveService saveService, PlayerState playerState) {
        super(metaController, ViewLoader.load(SaveMenuView::new));
        this.saveService = saveService;
        this.playerState = playerState;

        this.view.setListener(this);
    }

    @Override
    public void onNewGame() {
        LOG.info("Starting new game - clearing player data");
        this.saveService.clear(this.playerState);
        this.metaController.onMainMenu();
    }

    @Override
    public void onContinue() {
        LOG.info("Continuing game - loading player data");
        this.metaController.onMainMenu();
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }
}
