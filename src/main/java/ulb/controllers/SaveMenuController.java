package ulb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.player.PlayerState;
import ulb.services.SaveService;
import ulb.views.SaveMenuView;
import ulb.views.ViewLoader;

/**
 * Controller for the save menu screen.
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
