package ulb.models.bugemon;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<Item, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public boolean hasItem(Item item) {
        return this.items.containsKey(item) && items.get(item) > 0;
    }

    public boolean hasItem(String itemId) {
        return items.keySet().stream().anyMatch(
                item -> item.id().equals(itemId) && items.get(item) > 0);
    }

    public void useItem(Item item) {
        if (!hasItem(item)) {
            throw new IllegalStateException("Objet not in inventory or item quantity is 0");
        }
        Integer quantity = items.get(item) - 1;
        if (quantity <= 0) {
            items.remove(item);
        } else {
            items.replace(item, quantity);
        }
    }



    public void addItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public Item getItem(String id) {
        return this.items.keySet().stream()
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No objects has been found with this id : " + id));
    }
    public Map<Item, Integer> getItems() {
        return new HashMap<>(items);
    }
}
