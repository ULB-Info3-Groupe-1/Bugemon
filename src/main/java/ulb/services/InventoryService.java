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
     * Resets the player's inventory to a default state with the default items.
     */
    public void resetInventory() {
        this.playerRepository.addDefaultInventory(this.playername);
        this.loadInventory();
    }

    public void loadInventory() {
        this.inventory = this.playerRepository.getPlayerInventory(this.playername);
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
