package ulb.services;

import java.util.Collections;
import java.util.Map;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.repositories.PlayerRepository;

public class InventoryService {

    private final PlayerRepository playerRepository;
    private final String playername;
    private Inventory inventory;

    public InventoryService(PlayerRepository playerRepository, String playername) {
        this.playerRepository = playerRepository;
        this.playername = playername;
    }

    public void loadInventory() {
        this.inventory = this.playerRepository.getPlayerInventory(this.playername);
    }

    /**
     * Returns the currently loaded inventory.
     *
     * @throws IllegalStateException
     *             if {@link #loadInventory()} (or {@link #resetInventory()}) has not been called yet.
     */
    public Inventory getRequiredInventory() {
        if (this.inventory == null) {
            throw new IllegalStateException("Inventory not loaded. Call loadInventory() first.");
        }
        return this.inventory;
    }

    /**
     * Resets the player's inventory to a default state with the default items.
     */
    public void resetInventory() {
        this.playerRepository.addDefaultInventory(this.playername);
        this.loadInventory();
    }

    public void saveInventory() {
        this.playerRepository.saveInventory(this.playername, this.inventory);
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
