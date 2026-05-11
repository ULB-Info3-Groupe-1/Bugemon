package ulb.services;

import java.util.Collections;
import java.util.Map;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.Item.ItemType;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.StarterItemsEffect;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;

public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final String playername;
    private final SkillService skillService;
    private Inventory inventory;

    public InventoryService(PlayerRepository playerRepository, InventoryRepository inventoryRepository,
            String playername, SkillService skillService) {
        this.inventoryRepository = inventoryRepository;
        this.playername = playername;
        this.skillService = skillService;
    }

    public void loadInventory() {
        this.inventory = this.inventoryRepository.getPlayerInventory(this.playername);
    }

    /**
     * Resets the player's inventory to a default state with the default items, then grants the starter items granted by
     * every unlocked {@link StarterItemsEffect} skill.
     */
    public void resetInventory() {
        this.inventoryRepository.addDefaultInventory(this.playername);
        this.loadInventory();
        this.applyStarterItemsSkills();
    }

    // TODO: UI should permit the player to select the +x items granted by the skill
    /**
     * For every unlocked {@link StarterItemsEffect}, adds {@code quantity} units of every item in the inventory whose
     * category matches the effect's category.
     */
    private void applyStarterItemsSkills() {
        for (Skill skill : this.skillService.getSkills(StarterItemsEffect.class)) {
            StarterItemsEffect effect = (StarterItemsEffect) skill.getEffect();
            ItemType targetType = mapCategoryToItemType(effect.category());
            for (Item item : this.inventory.getMap().keySet()) {
                if (item.type() == targetType) {
                    this.inventory.addItem(item, effect.quantity());
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

    public void saveInventory() {
        this.inventoryRepository.saveInventory(this.playername, this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventory.getMap());
    }

    public boolean hasItem(Item item) {
        return this.inventory.hasItem(item);
    }
}
