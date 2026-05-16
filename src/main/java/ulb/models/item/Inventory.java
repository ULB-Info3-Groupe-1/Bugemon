package ulb.models.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Inventory {
    private final Map<Item, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public boolean hasItem(Item item) {
        return this.items.containsKey(item) && this.items.get(item) > 0;
    }

    /**
     * Returns the item used.
     */
    public Optional<Item> useItem(Item item) {
        if (!this.hasItem(item)) {
            return Optional.empty();
        }

        this.decrementItemQuantity(item);

        return Optional.of(item);
    }

    public void addItem(Item item, int quantity) {
        this.items.put(item, this.items.getOrDefault(item, 0) + quantity);
    }

    public Item getItem(String id) {
        return this.items.keySet().stream().filter(item -> item.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No item has been found with this id : " + id));
    }

    public Map<Item, Integer> getMap() {
        return new HashMap<>(this.items);
    }

    private void decrementItemQuantity(Item item) {
        Integer quantity = this.items.get(item) - 1;
        if (quantity <= 0) {
            this.items.remove(item);
        } else {
            this.items.replace(item, quantity);
        }
    }
}
