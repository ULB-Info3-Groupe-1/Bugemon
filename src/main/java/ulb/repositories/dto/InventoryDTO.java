package ulb.repositories.dto;

import java.util.Map;

import ulb.models.item.Item;

public record InventoryDTO(String playername, Map<Item, Integer> items) {
}
