package ulb.repositories;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.Item.ItemType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;

public class InventoryRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryRepository.class);
    private final StaticDataRepository staticDataRepository;

    public InventoryRepository(DatabaseConnection dbConnection, StaticDataRepository staticDataRepository,
            Map<String, String> queries) {
        super(dbConnection, queries);
        this.staticDataRepository = staticDataRepository;
    }

    // --- Items/Inventory ---

    public Inventory getPlayerInventory(String playername) {
        LOG.debug("Getting inventory for playername: {}", playername);
        Inventory inventory = new Inventory();
        executeQuery("GetPlayerInventory", rs -> {
            String effectType = rs.getString(DatabaseColumns.COL_EFFECT_TYPE);
            Effect effect = effectType != null ? this.buildItemEffect(rs, effectType) : null;
            Item item = new Item(rs.getString(DatabaseColumns.COL_ITEM_ID), rs.getString(DatabaseColumns.COL_NAME),
                    rs.getString(DatabaseColumns.COL_DESCRIPTION),
                    ItemType.valueOf(rs.getString(DatabaseColumns.COL_CATEGORY)), effect);
            inventory.addItem(item, rs.getInt(DatabaseColumns.COL_AMOUNT));
            return null;
        }, playername);
        return inventory;
    }

    private Effect buildItemEffect(java.sql.ResultSet rs, String effectType) throws java.sql.SQLException {
        EffectTarget target = EffectTarget.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_TARGET));
        return switch (effectType) {
            case "EffectHeal" -> new EffectHeal(target, rs.getInt(DatabaseColumns.COL_EFFECT_VALUE));
            case "EffectStatModifier" ->
                new EffectStatModifier(target, EffectStat.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_STAT)),
                        rs.getInt(DatabaseColumns.COL_EFFECT_MODIFIER),
                        rs.getInt(DatabaseColumns.COL_EFFECT_DURATION) == 0 ? EffectDuration.PERMANENT
                                : EffectDuration.ONE_TURN);
            case "EffectResetMalus" -> new EffectResetMalus(target);
            default -> throw new IllegalStateException("Unknown item effect type: " + effectType);
        };
    }

    /**
     * Update the player's inventory by first removing all existing items and then adding the items from the provided
     * inventory.
     *
     *
     * the player's name whose inventory is to be updated
     *
     * @param inventory
     *            the inventory containing the items to be saved for the player
     */
    public void saveInventory(String playername, Inventory inventory) {
        LOG.debug("Saving inventory for playerId: {}", playername);
        this.clearInventory(playername);
        this.addIventoryItems(playername, inventory);
    }

    /**
     * Reset the player's inventory to a default state by first clearing the existing inventory and then adding the
     * default items.
     *
     * @param playername
     *            the player's name whose inventory is to be reset
     */
    public void addDefaultInventory(String playername) {
        Inventory defaultInventory = this.staticDataRepository.getDefaultInventory();
        this.clearInventory(playername);
        this.addIventoryItems(playername, defaultInventory);
    }

    private void clearInventory(String playername) {
        LOG.debug("Clearing inventory for playername: {}", playername);
        executeUpdate("RemoveItemsOfPlayer", playername);
    }

    private void addIventoryItems(String playername, Inventory inventory) {
        inventory.getMap()
                .forEach((item, quantity) -> executeUpdate("SaveItemForPlayer", playername, item.id(), quantity));
    }
}
