package ulb.services;

import ulb.Configuration;
import ulb.controllers.TowerController;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.repositories.PlayerRepository;

public class TowerService {

    private final String playername;
    private final PlayerRepository playerRepository;
    private final BugemonService bugemonService;
    private final TeamService teamService;
    private final InventoryService inventoryService;

    public TowerService(PlayerRepository playerRepository, String playername, BugemonService bugemonService,
            TeamService teamService, InventoryService inventoryService) {
        this.playername = playername;
        this.playerRepository = playerRepository;
        this.bugemonService = bugemonService;
        this.teamService = teamService;
        this.inventoryService = inventoryService;
    }

    /**
     * Get the current floor by getting it from the database and setting it in the service.
     */
    int getCurrentFloor() {
        return this.playerRepository.getPlayerCurrentFloor(this.playername);
    }

    /**
     * Reset the tower progress by setting the current floor to the minimum floor and saving it in the database.
     */
    public void clearTowerProgress() {
        this.playerRepository.setPlayerCurrentFloor(this.playername, Configuration.Game.FLOOR_MIN);
    }

    /**
     * Move the player to the given node from the floor it is currently on
     *
     * @param tower
     *            the tower
     * @param node
     *            the node to move the player to
     * @param controller
     *            the controller
     */
    public void movePlayer(Tower tower, FloorNode node, TowerController controller) {
        tower.currentFloorMoveTo(node);
        tower.visitCurrentRoom(controller);
    }

    /**
     * Save the current floor in the database
     */
    private void saveFloor(int currentFloor) {
        this.playerRepository.setPlayerCurrentFloor(this.playername, currentFloor);
    }

    public void handleCombatEnd(Tower tower, boolean playerWon) {
        this.inventoryService.saveInventory();

        if (!playerWon) {
            this.clearTowerProgress();
            return;
        }

        if (tower.isCurrentFloorComplete()) {
            if (tower.getCurrentFloorNumber() == Configuration.Game.FLOOR_MAX) {
                tower.setTowerFinished();
                this.clearTowerProgress();
            } else {
                tower.goToNextFloor();
                this.saveFloor(tower.getCurrentFloorNumber());
            }
        }
    }

    /**
     * Create a new tower and return it
     *
     * @return the new tower
     */
    public Tower createTower() {
        return new Tower(this.teamService.getRequiredActiveTeam(), this.bugemonService, this.inventoryService,
                this.getCurrentFloor());
    }
}
