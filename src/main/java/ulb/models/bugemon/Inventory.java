package ulb.models.bugemon;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<Item, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public boolean hasItem(Item item) {
        return this.items.containsKey(item) && this.items.get(item) > 0;
    }

    public void useItem(Item item) {
        if (!this.hasItem(item)) {
            throw new IllegalStateException("Objet not in inventory or item quantity is 0");
        }
        Integer quantity = this.items.get(item) - 1;
        if (quantity <= 0) {
            this.items.remove(item);
        } else {
            this.items.replace(item, quantity);
        }
    }

    public void addItem(Item item, int quantity) {
        this.items.put(item, this.items.getOrDefault(item, 0) + quantity);
    }

    public Item getItem(String id) {
        return this.items.keySet()
                .stream()
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElseThrow(()
                                     -> new IllegalArgumentException(
                                             "No objects has been found with this id : " + id));
    }

    public Map<Item, Integer> getMap() {
        return new HashMap<>(this.items);
    }
}
