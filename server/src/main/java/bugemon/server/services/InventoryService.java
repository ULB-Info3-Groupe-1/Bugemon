package bugemon.server.services;

import java.util.List;
import java.util.Random;

import bugemon.common.dto.persistence.DefaultInventoryDTO;
import bugemon.common.dto.persistence.InventoryDTO;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.item.Item;
import bugemon.common.models.item.ItemType;
import bugemon.common.models.skills.SkillContext;
import bugemon.server.repositories.InventoryRepository;
import bugemon.server.repositories.StaticRepository;

/**
 * Service that manages a player's {@link bugemon.common.models.item.Inventory}.
 *
 * <p>
 * Provides inventory loading, persistence, reset, and the application of skill-based starter item bonuses.
 */
public class InventoryService {

    private final String playerName;

    private final InventoryRepository inventoryRepository;
    private final StaticRepository staticRepository;

    private final Random random;

    /**
     * Constructs an {@code InventoryService} bound to the given player.
     *
     * @param inventoryRepository
     *            repository for reading and writing player inventory data
     * @param staticRepository
     *            repository providing static item definitions and default inventory
     * @param random
     *            random-number source used when selecting starter items
     * @param playerName
     *            the name of the player whose inventory this service manages
     */
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

    /**
     * Adds skill-unlocked starter items to {@code inventory} based on the active
     * {@link bugemon.common.models.skills.SkillContext}. For each {@link bugemon.common.models.item.ItemType} that the
     * skill grants bonus quantities of, a random eligible item of that type is chosen and added for each bonus unit.
     *
     * @param inventory
     *            the inventory to receive the bonus items
     * @param skillContext
     *            the context derived from the player's current skill-tree state
     */
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
