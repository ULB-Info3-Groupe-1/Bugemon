package ulb.services;

import ulb.repositories.PlayerRepository;

public class TowerService {

    private final String playername;
    private final PlayerRepository playerRepository;

    private int currentFloor;

    public TowerService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.playerRepository = playerRepository;
        this.currentFloor = this.playerRepository.getPlayerCurrentFloor(this.playername);
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }
}
