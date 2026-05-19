package ulb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.views.SaveMenuView;
import ulb.views.ViewLoader;

/**
 * Controller for the save menu screen.
 */
public class SaveMenuController extends Controller<SaveMenuView> implements SaveMenuView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(SaveMenuController.class);

    private final BugemonService bugemonService;
    private final TeamService teamService;
    private final TowerService towerService;
    private final InventoryService inventoryService;

    public SaveMenuController(MetaController metaController, BugemonService bugemonService, TeamService teamService,
            TowerService towerService, InventoryService inventoryService) {
        super(metaController, ViewLoader.load(SaveMenuView::new));
        this.bugemonService = bugemonService;
        this.teamService = teamService;
        this.towerService = towerService;
        this.inventoryService = inventoryService;
        this.view.setListener(this);
    }

    @Override
    public void onNewGame() {
        LOG.info("Starting new game - clearing player data");
        this.bugemonService.clearAllPlayerBugemons();
        this.teamService.resetActiveTeam();
        this.teamService.deleteTeams();
        this.towerService.clearTowerProgress();
        this.inventoryService.resetInventory();
        this.metaController.onMainMenu();
    }

    @Override
    public void onContinue() {
        LOG.info("Continuing game - loading player data");
        this.inventoryService.loadInventory();
        this.metaController.onMainMenu();
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }
}
