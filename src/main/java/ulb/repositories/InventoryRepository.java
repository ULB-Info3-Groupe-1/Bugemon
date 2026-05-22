package ulb.repositories;

import ulb.common.dto.persistence.InventoryDTO;

public interface InventoryRepository {

    InventoryDTO findInventory(String playername);

    void save(InventoryDTO inventory);

    void delete(String playername);

}
