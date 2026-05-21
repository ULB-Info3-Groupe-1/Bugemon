package ulb.repositories;

import ulb.repositories.dto.InventoryDTO;

public interface InventoryRepository {

    InventoryDTO findInventory(String playername);

    void save(InventoryDTO inventory);

    void delete(String playername);

}
