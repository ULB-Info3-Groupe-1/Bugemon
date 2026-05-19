package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
import ulb.models.item.Item;
import ulb.models.item.ItemType;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.dto.InventoryDTO;

public class PostgresInventoryRepository extends AbstractRepository implements InventoryRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresInventoryRepository.class);

    private static final int DB_DURATION_PERMANENT = 0;

    public PostgresInventoryRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public InventoryDTO findInventory(String playername) {
        LOG.debug("Finding inventory for playername: {}", playername);
        Map<Item, Integer> inventoryMap = new HashMap<>();
        this.executeQuery("GetPlayerInventory", rs -> {
            String effectType = rs.getString(DatabaseColumns.COL_EFFECT_TYPE);
            Effect effect = effectType != null ? this.buildItemEffect(rs, effectType) : null;
            Item item = new Item(rs.getString(DatabaseColumns.COL_ITEM_ID), rs.getString(DatabaseColumns.COL_NAME),
                    rs.getString(DatabaseColumns.COL_DESCRIPTION),
                    ItemType.valueOf(rs.getString(DatabaseColumns.COL_CATEGORY)), effect);
            inventoryMap.put(item, rs.getInt(DatabaseColumns.COL_AMOUNT));
            return null;
        }, playername);
        return new InventoryDTO(inventoryMap);
    }

    @Override
    public void save(String playername, InventoryDTO inventory) {
        LOG.debug("Saving inventory for playername: {}", playername);
        this.clearInventory(playername);
        inventory.items()
                .forEach((item, quantity) -> this.executeUpdate("SaveItemForPlayer", playername, item.id(), quantity));
    }

    @Override
    public void delete(String playername) {
        LOG.debug("Deleting inventory for playername: {}", playername);
        this.clearInventory(playername);
    }

    private void clearInventory(String playername) {
        this.executeUpdate("RemoveItemsOfPlayer", playername);
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
