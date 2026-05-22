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

    private final String playerName;

    private final InventoryRepository inventoryRepository;
    private final StaticRepository staticRepository;

    private final Random random;

    public InventoryService(InventoryRepository inventoryRepository, StaticRepository staticRepository, Random random,
            String playerName) {
        this.inventoryRepository = inventoryRepository;
        this.staticRepository = staticRepository;
        this.random = random;
        this.playerName = playerName;
    }

    public Inventory getInventory() {
        InventoryDTO inventoryDTO = this.inventoryRepository.findInventory(this.playerName);
        return new Inventory(inventoryDTO.items());
    }

    public void resetInventory() {
        this.inventoryRepository.delete(this.playerName);
    }

    public DefaultInventoryDTO getDefaultInventory() {
        return this.staticRepository.defaultInventory();
    }

    public List<Item> getItems() {
        return this.staticRepository.items();
    }

    public void applyStarterItemsBonus(Inventory inventory, SkillContext skillContext) {
        for (ItemType type : ItemType.values()) {
            int quantity = skillContext.getStarterItemQuantity(type);
            if (quantity > 0) {
                List<Item> eligible = this.staticRepository.items().stream().filter(item -> item.type() == type)
                        .toList();

                if (!eligible.isEmpty()) {
                    for (int i = 0; i < quantity; i++) {
                        inventory.addItem(eligible.get(this.random.nextInt(eligible.size())), 1);
                    }
                }
            }
        }
    }

    public void save(Inventory inventory) {
        this.inventoryRepository.save(this.toDTO(inventory));
    }

    private InventoryDTO toDTO(Inventory inventory) {
        return new InventoryDTO(this.playerName, inventory.getMap());
    }

}
