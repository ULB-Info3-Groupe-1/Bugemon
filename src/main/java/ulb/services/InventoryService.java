package ulb.services;

import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.item.ItemType;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.StarterItemsEffect;
import ulb.repositories.InventoryRepository;

// TODO: refact this class

public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final SkillService skillService;
    private final String playername;

    public InventoryService(String playername, InventoryRepository inventoryRepository, SkillService skillService) {
        this.inventoryRepository = inventoryRepository;
        this.playername = playername;
        this.skillService = skillService;
    }

    public Inventory loadInventory() {
        Inventory inventory = new Inventory();

        // Inventory inventory = this.inventoryRepository.getPlayerInventory(this.playername);
        // this.applyStarterItemsSkills(inventory);
        return inventory;
    }

    /**
     * Resets the player's inventory to a default state with the default items, then grants the starter items granted by
     * every unlocked {@link StarterItemsEffect} skill.
     */
    public void resetInventory() {
        // this.inventoryRepository.addDefaultInventory(this.playername);
    }

    public Inventory getDefaultInventory() {
        return this.loadInventory();
        // return this.inventoryRepository.getDefaultInventory();
    }

    // TODO: UI should permit the player to select the +x items granted by the skill
    /**
     * For every unlocked {@link StarterItemsEffect}, adds {@code quantity} units of every item in the inventory whose
     * category matches the effect's category.
     */
    private void applyStarterItemsSkills(Inventory inventory) {
        for (Skill skill : this.skillService.getSkills(StarterItemsEffect.class)) {
            StarterItemsEffect effect = (StarterItemsEffect) skill.getEffect();
            ItemType targetType = mapCategoryToItemType(effect.category());
            for (Item item : inventory.getMap().keySet()) {
                if (item.type() == targetType) {
                    inventory.addItem(item, effect.quantity());
                }
            }
        }
    }

    private static ItemType mapCategoryToItemType(String category) {
        return switch (category) {
            case "soin" -> ItemType.HEALING;
            case "boost" -> ItemType.BOOST;
            default -> throw new IllegalArgumentException("Unknown starter item category: " + category);
        };
    }

    public void saveInventory(Inventory inventory) {
        // this.inventoryRepository.saveInventory(this.playername, inventory);
    }

    public void save(Inventory inventory) {
        this.saveInventory(inventory);
    }

}
