package ulb.models.bugemon;

import java.util.List;

// TODO: surely a better way to do this, but it works for now
public class ItemWrapper {
    private List<Item> items;
    private Inventory inventory;

    public ItemWrapper(List<Item> items, Inventory inventory) {
        this.items = items;
        this.inventory = inventory;
    }

    public List<Item> getItems() {
        return items;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}