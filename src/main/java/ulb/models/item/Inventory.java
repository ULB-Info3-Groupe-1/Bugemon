package ulb.models.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tracks the player's collection of {@link Item}s and their quantities.
 *
 * <p>
 * Items are keyed by identity (via {@link Item#equals}/{@link Item#hashCode}), so duplicate definitions of the same
 * item consolidate into a single entry with a cumulative quantity.
 */
public class Inventory {
    private final Map<Item, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public Inventory(Map<Item, Integer> items) {
        this.items = new HashMap<>(items);
    }

    public boolean hasItem(Item item) {
        return this.items.containsKey(item) && this.items.get(item) > 0;
    }

    /**
     * Consumes one unit of {@code item} from the inventory and returns it.
     *
     * @param item
     *            the item to use
     * @return the used item, or {@link java.util.Optional#empty()} if the item is not present in the inventory
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

    /**
     * Returns the item with the given identifier.
     *
     * @param id
     *            the item identifier to look up
     * @return the matching {@link Item}
     * @throws IllegalArgumentException
     *             if no item with that id exists in the inventory
     */
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

    public void clear() {
        this.items.clear();
    }
}
