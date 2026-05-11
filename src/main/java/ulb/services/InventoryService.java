package ulb.services;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.Item.ItemType;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.StarterItemsEffect;
import ulb.repositories.InventoryRepository;

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
        Inventory inventory = this.inventoryRepository.getPlayerInventory(this.playername);
        this.applyStarterItemsSkills(inventory);
        return inventory;
    }

    /**
     * Resets the player's inventory to a default state with the default items, then grants the starter items granted by
     * every unlocked {@link StarterItemsEffect} skill.
     */
    public void resetInventory() {
        this.inventoryRepository.addDefaultInventory(this.playername);
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
        this.inventoryRepository.saveInventory(this.playername, inventory);
    }

}
