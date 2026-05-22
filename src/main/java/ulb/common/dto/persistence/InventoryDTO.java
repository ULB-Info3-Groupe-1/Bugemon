package ulb.common.dto.persistence;

import java.util.Map;

import ulb.models.item.Item;

public record InventoryDTO(String playerName, Map<Item, Integer> items) {
}
