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

    /**
     * Constructor for the InventoryService class.
     *
     * @param playerRepository
     *            the player repository used to interact with the database
     * @param playername
     *            the name of the player whose inventory is being managed by this service and used to interact with the
     *            database
     */
    public InventoryService(PlayerRepository playerRepository, String playername) {
        this.playerRepository = playerRepository;
        this.playername = playername;
    }

    /**
     * Loads the player's inventory from the database and stores it in the service.
     */
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

    /**
     * Saves the player's inventory to the database.
     */
    public void saveInventory() {
        this.playerRepository.saveInventory(this.playername, this.inventory);
    }

    /**
     * Uses the specified item from the player's inventory.
     *
     * @param item
     *            the item to use
     */
    public void useItem(Item item) {
        this.inventory.useItem(item);
    }

    /**
     * Returns a read-only map of the player's inventory.
     *
     * @return a read-only map of the player's inventory
     */
    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventory.getMap());
    }

    /**
     * Checks if the player has the specified item in their inventory.
     *
     * @param item
     *            the item to check
     * @return true if the player has the item, false otherwise
     */
    public boolean hasItem(Item item) {
        return this.inventory.hasItem(item);
    }
}
