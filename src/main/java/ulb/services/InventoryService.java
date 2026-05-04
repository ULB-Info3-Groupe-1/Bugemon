package ulb.services;

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

    public Inventory getInventory() {
        return this.inventory;
    }

    public void loadInventory() {
        this.inventory = this.playerRepository.getPlayerInventory(this.playername);
    }

    public void saveInventory() {
        this.inventory.getMap().forEach(
                (item, quantity) -> this.playerRepository.updateItemAmount(this.playername, item.id(), quantity));
    }

    public void addItem(Item item, int quantity) {
        this.playerRepository.addItemToPlayer(this.playername, item.id(), quantity);
        this.inventory.addItem(item, quantity);
    }
}
