package ulb.repositories;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

public interface PlayerRepository {

    void createPlayer(String playername, DefaultInventoryDTO defaultInventory) throws PlayernameAlreadyExistsException;

    boolean playerExists(String playername);

    int getPlayerCurrentFloor(String playername);

    void setPlayerCurrentFloor(String playername, int floorNumber);

    void resetPlayerCurrentFloor(String playername);
}
