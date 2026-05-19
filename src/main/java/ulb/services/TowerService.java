package ulb.services;

import ulb.Configuration;
import ulb.repositories.postgres.PlayerRepository;

public class TowerService {

    private final String playername;
    private final PlayerRepository playerRepository;

    public TowerService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.playerRepository = playerRepository;
    }

    /**
     * Get the current floor by getting it from the database and setting it in the
     * service.
     */
    int getCurrentFloor() {
        return this.playerRepository.getPlayerCurrentFloor(this.playername);
    }

    /**
     * Reset the tower progress by setting the current floor to the minimum floor
     * and saving it in the database.
     */
    public void clearTowerProgress() {
        this.playerRepository.setPlayerCurrentFloor(this.playername, Configuration.Game.FLOOR_MIN);
    }
}
