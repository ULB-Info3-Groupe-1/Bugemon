package bugemon.common.dto.persistence;

import java.util.Map;

import bugemon.common.models.item.Item;

/**
 * Starter inventory granted to a new player on their first login.
 *
 * <p>
 * The map associates each {@link Item} with the quantity the player begins with.
 *
 * @param items
 *            mapping of item to starting quantity
 */
public record DefaultInventoryDTO(Map<Item, Integer> items) {
}
