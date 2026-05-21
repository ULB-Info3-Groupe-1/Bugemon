package ulb.services;

import ulb.models.item.Inventory;
import ulb.repositories.InventoryRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.dto.InventoryDTO;

public class InventoryService {

    private final String playername;

    private final InventoryRepository inventoryRepository;
    private final StaticRepository staticRepository;

    // TODO: UI should permit the player to select the +x items granted by the skill

    public InventoryService(String playername, InventoryRepository inventoryRepository,
            StaticRepository staticRepository) {
        this.inventoryRepository = inventoryRepository;
        this.staticRepository = staticRepository;
        this.playername = playername;
    }

    public Inventory getInventory() {
        InventoryDTO inventoryDTO = this.inventoryRepository.findInventory(this.playername);
        return new Inventory(inventoryDTO.items());
    }

    public void resetInventory() {
        this.inventoryRepository.delete(this.playername);
    }

    public Inventory getDefaultInventory() {
        return new Inventory(this.staticRepository.defaultInventory().items());
    }

    public void save(Inventory inventory) {
        this.inventoryRepository.save(this.toDTO(inventory));
    }

    private InventoryDTO toDTO(Inventory inventory) {
        return new InventoryDTO(this.playername, inventory.getMap());
    }

}
