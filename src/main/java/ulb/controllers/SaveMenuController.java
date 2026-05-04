package ulb.controllers;

import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.views.SaveMenuView;
import ulb.views.ViewLoader;

/** Controller for the main menu screen. */
public class SaveMenuController extends Controller<SaveMenuView> implements SaveMenuView.Listener {

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
        this.bugemonService.clearAllPlayerBugemons();
        this.teamService.clearTeamsAndActiveTeam();
        this.towerService.clearTowerProgress();
        this.inventoryService.loadInventory(); // TODO: check if needed or recreate a new inventory ?
        this.metaController.onMainMenu();
    }

    @Override
    public void onContinue() {
        this.teamService.loadTeamsAndActiveTeam();
        this.towerService.loadTowerProgress();
        this.inventoryService.loadInventory();
        this.metaController.onMainMenu();
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }
}
