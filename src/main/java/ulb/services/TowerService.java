package ulb.services;

import ulb.Configuration;
import ulb.controllers.combat.TowerController;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.repositories.PlayerRepository;

public class TowerService {

    private final String playername;
    private final PlayerRepository playerRepository;

    public TowerService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.playerRepository = playerRepository;
    }

    /**
     * Get the current floor by getting it from the database and setting it in the service.
     */
    public int getCurrentFloor() {
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
     * @param tower the tower
     * @param node the node to move the player to
     * @param controller the controller
     */
    public void movePlayer(Tower tower, FloorNode node, TowerController controller) {
        System.out.println("Moving player to " + node);
        tower.currentFloorMoveTo(node);
        tower.visitCurrentRoomIfNotVisited(controller);
    }

    /**
     * Save the current floor in the database
     */
    public void saveFloor(int currentFloor) {
        this.playerRepository.setPlayerCurrentFloor(this.playername, currentFloor);
    }
}
