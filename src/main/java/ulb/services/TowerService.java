package ulb.services;

import ulb.Configuration;
import ulb.repositories.PlayerRepository;

public class TowerService {

    // TODO: add and connect method to save the current floor in the database when the player finishes a floor and moves
    // to the next one !

    private final String playername;
    private final PlayerRepository playerRepository;

    private int currentFloor;

    /**
     * Constructor for the TowerService class.
     *
     * @param playerRepository
     *            the player repository used to access the database
     * @param playername
     *            the player's name used to access the database
     */
    public TowerService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.playerRepository = playerRepository;
    }

    /**
     * Load the tower progress by getting the current floor from the database and setting it in the service.
     */
    public void loadTowerProgress() {
        this.currentFloor = this.playerRepository.getPlayerCurrentFloor(this.playername);
    }

    /**
     * Reset the tower progress by setting the current floor to the minimum floor and saving it in the database.
     */
    public void clearTowerProgress() {
        this.currentFloor = Configuration.Game.FLOOR_MIN;
        this.playerRepository.setPlayerCurrentFloor(this.playername, this.currentFloor);
    }

    /**
     * Get the current floor of the tower.
     *
     * @return the current floor
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }
}
