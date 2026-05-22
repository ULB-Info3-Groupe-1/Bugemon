package ulb.services;

import java.util.List;
import java.util.Random;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.common.dto.persistence.InventoryDTO;
import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.item.ItemType;
import ulb.models.skills.SkillContext;
import ulb.repositories.InventoryRepository;
import ulb.repositories.StaticRepository;

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

    public DefaultInventoryDTO getDefaultInventory() {
        return this.staticRepository.defaultInventory();
    }

    public List<Item> getItems() {
        return this.staticRepository.items();
    }

    public void applyStarterItemsBonus(Inventory inventory, SkillContext skillContext) {
        Random random = new Random();
        for (ItemType type : ItemType.values()) {
            int quantity = skillContext.getStarterItemQuantity(type);
            if (quantity <= 0) {
                continue;
            }
            List<Item> eligible = this.staticRepository.items().stream()
                    .filter(item -> item.type() == type)
                    .toList();
            if (eligible.isEmpty()) {
                continue;
            }
            for (int i = 0; i < quantity; i++) {
                inventory.addItem(eligible.get(random.nextInt(eligible.size())), 1);
            }
        }
    }

    public void save(Inventory inventory) {
        this.inventoryRepository.save(this.toDTO(inventory));
    }

    private InventoryDTO toDTO(Inventory inventory) {
        return new InventoryDTO(this.playername, inventory.getMap());
    }

}
