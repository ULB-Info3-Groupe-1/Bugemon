package ulb.repositories;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

public interface PlayerRepository {

    void createPlayer(String playerName, DefaultInventoryDTO defaultInventory) throws PlayernameAlreadyExistsException;

    boolean playerExists(String playerName);

    int getPlayerCurrentFloor(String playerName);

    void setPlayerCurrentFloor(String playerName, int floorNumber);

    void resetPlayerCurrentFloor(String playerName);
}
