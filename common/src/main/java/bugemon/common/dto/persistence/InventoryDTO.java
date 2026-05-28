package bugemon.common.dto.persistence;

import java.util.Map;

import bugemon.common.models.item.Item;

/**
 * Serialisable snapshot of a player's full inventory as stored in the database.
 *
 * @param playerName
 *            the owner of the inventory
 * @param items
 *            mapping of each owned {@link Item} to its current quantity
 */
public record InventoryDTO(String playerName, Map<Item, Integer> items) {
}
