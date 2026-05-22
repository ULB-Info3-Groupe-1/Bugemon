package ulb.repositories;

import ulb.common.dto.persistence.InventoryDTO;

public interface InventoryRepository {

    InventoryDTO findInventory(String playerName);

    void save(InventoryDTO inventory);

    void delete(String playerName);

}
