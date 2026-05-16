package ulb.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;
import ulb.models.effect.Effect;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.item.ItemType;

public class InventoryRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryRepository.class);

    private final Inventory defaultInventory;

    public InventoryRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            Inventory defaultInventory) {
        super(dbConnection, queries);
        this.defaultInventory = defaultInventory;
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

    private Effect buildItemEffect(ResultSet rs, String effectType) throws SQLException {
        EffectTarget target = EffectTarget.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_TARGET));
        return switch (effectType) {
            case "EffectHeal" -> new HealEffect(target, rs.getInt(DatabaseColumns.COL_EFFECT_VALUE));
            case "EffectStatModifier" ->
                new StatModifierEffect(target, StatType.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_STAT)),
                        rs.getInt(DatabaseColumns.COL_EFFECT_MODIFIER),
                        rs.getInt(DatabaseColumns.COL_EFFECT_DURATION) == 0 ? EffectDuration.PERMANENT
                                : EffectDuration.ONE_TURN);
            case "EffectResetMalus" -> new ResetMalusEffect(target);
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
        this.clearInventory(playername);
        this.addIventoryItems(playername, this.defaultInventory);
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
