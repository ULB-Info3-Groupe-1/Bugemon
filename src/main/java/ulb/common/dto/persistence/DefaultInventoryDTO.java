package ulb.common.dto.persistence;

import java.util.Map;

import ulb.models.item.Item;

public record DefaultInventoryDTO(Map<Item, Integer> items) {
}
