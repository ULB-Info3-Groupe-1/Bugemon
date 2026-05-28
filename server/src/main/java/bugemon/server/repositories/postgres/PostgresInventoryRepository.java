package bugemon.server.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.EffectDuration;
import bugemon.common.EffectTarget;
import bugemon.common.StatType;
import bugemon.common.dto.persistence.InventoryDTO;
import bugemon.common.models.effect.Effect;
import bugemon.common.models.effect.HealEffect;
import bugemon.common.models.effect.ResetMalusEffect;
import bugemon.common.models.effect.StatModifierEffect;
import bugemon.common.models.item.Item;
import bugemon.common.models.item.ItemType;
import bugemon.server.repositories.DatabaseConnection;
import bugemon.server.repositories.InventoryRepository;

/**
 * PostgreSQL implementation of {@link InventoryRepository}.
 *
 * <p>
 * Reconstructs {@link bugemon.common.models.item.Item} objects — including their polymorphic
 * {@link bugemon.common.models.effect.Effect} — from the {@code inventory} and {@code item_effects} tables. Saving
 * replaces the entire inventory in a single delete-then-insert cycle.
 */
public class PostgresInventoryRepository extends AbstractRepository implements InventoryRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresInventoryRepository.class);

    private static final int DB_DURATION_PERMANENT = 0;

    public PostgresInventoryRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public InventoryDTO findInventory(String playerName) {
        LOG.debug("Finding inventory for playerName: {}", playerName);
        Map<Item, Integer> inventoryMap = new HashMap<>();
        this.executeQuery("GetPlayerInventory", rs -> {
            String effectType = rs.getString(DatabaseColumns.COL_EFFECT_TYPE);
            Effect effect = effectType != null ? this.buildItemEffect(rs, effectType) : null;
            Item item = new Item(rs.getString(DatabaseColumns.COL_ITEM_ID), rs.getString(DatabaseColumns.COL_NAME),
                    rs.getString(DatabaseColumns.COL_DESCRIPTION),
                    ItemType.valueOf(rs.getString(DatabaseColumns.COL_CATEGORY)), effect);
            inventoryMap.put(item, rs.getInt(DatabaseColumns.COL_AMOUNT));
            return null;
        }, playerName);
        return new InventoryDTO(playerName, inventoryMap);
    }

    @Override
    public void save(InventoryDTO inventory) {
        LOG.debug("Saving inventory for playerName: {}", inventory.playerName());
        this.clearInventory(inventory.playerName());
        inventory.items().forEach((item, quantity) -> this.executeUpdate("SaveItemForPlayer", inventory.playerName(),
                item.id(), quantity));
    }

    @Override
    public void delete(String playerName) {
        LOG.debug("Deleting inventory for playerName: {}", playerName);
        this.clearInventory(playerName);
    }

    private void clearInventory(String playerName) {
        this.executeUpdate("RemoveItemsOfPlayer", playerName);
    }

    private Effect buildItemEffect(ResultSet rs, String effectType) throws SQLException {
        EffectTarget target = EffectTarget.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_TARGET));
        return switch (effectType) {
            case "EffectHeal" -> new HealEffect(target, rs.getInt(DatabaseColumns.COL_EFFECT_VALUE));
            case "EffectStatModifier" ->
                new StatModifierEffect(target, StatType.valueOf(rs.getString(DatabaseColumns.COL_EFFECT_STAT)),
                        rs.getInt(DatabaseColumns.COL_EFFECT_MODIFIER),
                        rs.getInt(DatabaseColumns.COL_EFFECT_DURATION) == DB_DURATION_PERMANENT
                                ? EffectDuration.PERMANENT
                                : EffectDuration.ONE_TURN);
            case "EffectResetMalus" -> new ResetMalusEffect(target);
            default -> throw new IllegalStateException("Unknown item effect type: " + effectType);
        };
    }
}
