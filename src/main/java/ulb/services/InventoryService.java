package ulb.services;

import java.util.Collections;
import java.util.Map;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;

public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final String playername;
    private Inventory inventory;

    public InventoryService(PlayerRepository playerRepository, InventoryRepository inventoryRepository,
            String playername) {
        this.inventoryRepository = inventoryRepository;
        this.playername = playername;
    }

    public void loadInventory() {
        this.inventory = this.inventoryRepository.getPlayerInventory(this.playername);
    }

    /**
     * Resets the player's inventory to a default state with the default items.
     */
    public void resetInventory() {
        this.inventoryRepository.addDefaultInventory(this.playername);
        this.loadInventory();
    }

    public void saveInventory() {
        this.inventoryRepository.saveInventory(this.playername, this.inventory);
    }

    public void useItem(Item item) {
        this.inventory.useItem(item);
    }

    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventory.getMap());
    }

    public boolean hasItem(Item item) {
        return this.inventory.hasItem(item);
    }
}
