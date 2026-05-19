package ulb.repositories.dto;

import java.util.Map;

import ulb.models.item.Item;

public record InventoryDTO(Map<Item, Integer> items) {
}
