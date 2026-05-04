package ulb.services;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.repositories.PlayerRepository;

public class InventoryService {

    private final PlayerRepository playerRepository;
    private final int playerId;
    private final Inventory inventory;

    public InventoryService(PlayerRepository playerRepository, int playerId) {
        this.playerRepository = playerRepository;
        this.playerId = playerId;
        this.inventory = playerRepository.getPlayerInventory(playerId);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void saveInventory() {
        this.inventory.getMap().forEach(
                (item, quantity) -> this.playerRepository.updateItemAmount(this.playerId, item.id(), quantity));
    }

    public void addItem(Item item, int quantity) {
        this.playerRepository.addItemToPlayer(this.playerId, item.id(), quantity);
        this.inventory.addItem(item, quantity);
    }
}
