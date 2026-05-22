package ulb.repositories;

import ulb.common.dto.persistence.InventoryDTO;

/**
 * Repository for persisting and querying a player's item inventory.
 */
public interface InventoryRepository {

    /**
     * Loads the full inventory for the given player.
     *
     * @param playerName
     *            the player's unique name
     * @return the player's {@link InventoryDTO}, with an empty item map if no items are stored
     */
    InventoryDTO findInventory(String playerName);

    /**
     * Replaces the persisted inventory with the contents of the supplied DTO.
     *
     * @param inventory
     *            the inventory state to persist
     */
    void save(InventoryDTO inventory);

    /**
     * Removes all inventory records for the given player.
     *
     * @param playerName
     *            the player's unique name
     */
    void delete(String playerName);

}
