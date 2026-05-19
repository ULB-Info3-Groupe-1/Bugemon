package ulb.repositories;

import ulb.repositories.dto.InventoryDTO;

public interface InventoryRepository {

    InventoryDTO findInventory(String playername);

    void save(String playername, InventoryDTO inventory);

    void delete(String playername);

}
