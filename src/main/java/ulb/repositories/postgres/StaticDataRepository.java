package ulb.repositories.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ulb.Configuration;
import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.effect.Effect;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.item.Item;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.utils.DatabaseHelper;

public class StaticDataRepository extends AbstractRepository {
    private static final int CRITICAL_TABLES_COUNT = 10;
    private static final String SAVE_ITEM_EFFECT_QUERY = "SaveItemEffect";

    public StaticDataRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            List<CreateBugemonDTO> defaultBugemonDTO, Map<String, Attack> attacks, List<Item> items) {
        super(dbConnection, queries);
        this.prepareDatabase(defaultBugemonDTO, attacks, items);
    }

    // --- DATABASE INIT ---

    private void prepareDatabase(List<CreateBugemonDTO> defaultBugemonDTO, Map<String, Attack> attacks,
            List<Item> items) {
        Integer tableCount = executeQuery("areTablesPresent", rs -> rs.getInt("existing_critical_tables")).stream()
                .findFirst().orElse(0);

        if (tableCount < CRITICAL_TABLES_COUNT) {
            executeUpdate("CreateSchema");
            this.addDefaultGameData(defaultBugemonDTO, attacks, items);
            return;
        }

        Integer rowCount = executeQuery("IsDataEmpty", rs -> rs.getInt("total_rows")).stream().findFirst().orElse(0);
        if (rowCount == 0) {
            this.addDefaultGameData(defaultBugemonDTO, attacks, items);
            return;
        }

        Integer itemCount = executeQuery("IsItemsEmpty", rs -> rs.getInt("item_count")).stream().findFirst().orElse(0);
        if (itemCount == 0) {
            items.forEach(this::saveItem);
        }
    }

    private void addDefaultGameData(List<CreateBugemonDTO> defaultBugemonDTO, Map<String, Attack> attacks,
            List<Item> items) {
        attacks.values().forEach(this::saveAttackFull);
        defaultBugemonDTO.forEach(this::saveBugemon);
        items.forEach(this::saveItem);
    }

    private void saveAttackFull(Attack attack) {
        executeUpdate("SaveAttack", attack.id(), attack.name(), (attack.type() != null ? attack.type().name() : null),
                attack.description(), attack.power());

        if (attack.effects() != null && !attack.effects().isEmpty()) {
            try (PreparedStatement ps = dbConnection.prepareStatement(getSql("SaveEffect"))) {
                for (Effect effect : attack.effects()) {
                    ps.setString(1, attack.id());
                    this.setEffectParameters(ps, effect);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                throw new IllegalStateException("Error saving effects for " + attack.id(), e);
            }
        }
    }

    // --- DATABASE QUERIES ---

    private Bugemon mapBugemon(ResultSet rs, Map<String, Attack> attackMap) throws SQLException {
        ElementType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE, ElementType.class);
        return new Bugemon(rs.getString(DatabaseColumns.COL_NAME), rs.getInt(DatabaseColumns.COL_BASE_MAX_HP),
                rs.getInt(DatabaseColumns.COL_BASE_ATTACK), rs.getInt(DatabaseColumns.COL_BASE_DEFENSE),
                rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE), type,
                List.of(attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_1)),
                        attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_2)),
                        attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_3))),
                rs.getString(DatabaseColumns.COL_SPRITE), rs.getBoolean(DatabaseColumns.COL_IS_STARTER));
    }

    private void setEffectParameters(PreparedStatement psEffect, Effect effect) throws SQLException {
        switch (effect) {
            case StatModifierEffect modifier -> this.setStatModifierParameters(psEffect, modifier);
            case HealEffect heal -> this.setHealParameters(psEffect, heal);
            case ResetMalusEffect resetMalus -> this.setResetMalusParameters(psEffect, resetMalus);
            default -> throw new IllegalStateException("Unknown effect type: " + effect.getClass().getSimpleName());
        }
    }

    private void setStatModifierParameters(PreparedStatement psEffect, StatModifierEffect modifier)
            throws SQLException {
        psEffect.setString(2, modifier.getClass().getSimpleName());
        psEffect.setString(3, modifier.getTarget().name());
        psEffect.setObject(4, modifier.getStat() != null ? modifier.getStat().name() : null, Types.VARCHAR);
        psEffect.setInt(5, modifier.getModifier());
        psEffect.setString(6, modifier.getDuration().toString());
        psEffect.setNull(7, Types.INTEGER);
    }

    private void setHealParameters(PreparedStatement psEffect, HealEffect heal) throws SQLException {
        psEffect.setString(2, heal.getClass().getSimpleName());
        psEffect.setString(3, heal.getTarget().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setInt(7, heal.getAmount());
    }

    private void setResetMalusParameters(PreparedStatement psEffect, ResetMalusEffect resetMalus) throws SQLException {
        psEffect.setString(2, resetMalus.getClass().getSimpleName());
        psEffect.setString(3, resetMalus.getTarget().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setNull(7, Types.INTEGER);
    }

    private void saveItem(Item item) {
        executeUpdate("CreateItem", item.id(), item.name(), item.description(), item.type().name(), item.id() + ".png");
        if (item.effect() != null) {
            this.saveItemEffect(item.id(), item.effect());
        }
    }

    private void saveItemEffect(String itemId, Effect effect) {
        switch (effect) {
            case HealEffect heal -> executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectHeal", heal.getTarget().name(),
                    heal.getAmount(), null, null, null);
            case StatModifierEffect modifier -> executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectStatModifier",
                    modifier.getTarget().name(), null, modifier.getStat() != null ? modifier.getStat().name() : null,
                    modifier.getModifier(), modifier.getDuration() == EffectDuration.PERMANENT ? 0 : 1);
            case ResetMalusEffect resetMalus -> executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectResetMalus",
                    resetMalus.getTarget().name(), null, null, null, null);
            default -> throw new IllegalStateException("Unknown effect type: " + effect.getClass().getSimpleName());
        }
    }

    // --- UTILS FOR CLASS USING THIS REPO --

    public Optional<Bugemon> findByName(String name) {
        return executeQuery("GetBugemonByName", rs -> this.mapBugemon(rs, this.getAllAttacks())).stream()
                .filter(b -> b.name().equals(name)).findFirst();
    }

    /**
     * Retrieves all default Bugemons.
     *
     * @return (List<Bugemon>) List of default Bugemons of the game
     */
    public List<Bugemon> getAllDefaultBugemons() {
        Map<String, Attack> attackMap = this.getAllAttacks();
        return executeQuery("GetAllDefaultBugemons", rs -> this.mapBugemon(rs, attackMap));
    }

    private static class AttackInfo {
        String name;
        ElementType type;
        String description;
        int power;

        AttackInfo(String name, ElementType type, String description, int power) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.power = power;
        }
    }

    /**
     * Retrieves all attacks with their effects.
     *
     * @return map of attacks (keyed by attack id)
     */
    public Map<String, Attack> getAllAttacks() {
        Map<String, AttackInfo> infos = new LinkedHashMap<>();
        Map<String, List<Effect>> effects = new HashMap<>();

        executeQuery("GetAllAttacksWithEffects", rs -> {
            String id = rs.getString("attack_id");
            infos.computeIfAbsent(id, k -> {
                effects.put(k, new ArrayList<>());
                try {
                    return new AttackInfo(rs.getString("attack_name"),
                            DatabaseHelper.getEnumOrNull(rs, "attack_type", ElementType.class),
                            rs.getString("attack_description"), rs.getInt("attack_power"));
                } catch (SQLException e) {
                    throw new IllegalStateException("Error loading attack " + id, e);
                }
            });

            String effType = rs.getString(DatabaseColumns.COL_EFFECT_TYPE);
            if (effType != null) {
                effects.get(id).add(this.buildEffect(rs, effType));
            }
            return null;
        });

        Map<String, Attack> attackMap = new HashMap<>();
        infos.forEach((id, info) -> attackMap.put(id,
                new Attack(id, info.name, info.description, info.power, info.type, effects.get(id))));
        return attackMap;
    }

    private Effect buildEffect(ResultSet rs, String effectType) throws SQLException {
        EffectTarget target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_EFFECT_TARGET, EffectTarget.class);
        return switch (effectType) {
            case "StatModifierEffect" -> {
                StatType stat = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_EFFECT_STAT, StatType.class);
                String dbDuration = rs.getString(DatabaseColumns.COL_EFFECT_DURATION);
                String duration = (dbDuration != null) ? dbDuration : "0_tour";
                yield new StatModifierEffect(target, stat, rs.getInt(DatabaseColumns.COL_EFFECT_MODIFIER),
                        EffectDuration.fromLabel(duration));
            }
            case "HealEffect" -> new HealEffect(target, rs.getInt("effect_amount"));
            case "ResetMalusEffect" -> new ResetMalusEffect(target);
            default -> throw new IllegalStateException("Unknown effect type: " + effectType);
        };
    }

    /**
     * Saves a Bugemon to the database.
     *
     * @param b
     *          (CreateBugemonDTO) the bugemon to be saved
     */
    public void saveBugemon(CreateBugemonDTO b) {
        String fileName = b.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";
        try {
            this.saveSpriteFile(b.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        executeUpdate("SaveBugemon", b.name(), b.type().name(), fileName, b.defense(), b.attack(), b.initiative(),
                b.maxHp(), b.isStarter(), b.attack1().id(), b.attack2().id(), b.attack3().id());
    }

    private void saveSpriteFile(URL currentSpriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(Configuration.Paths.SPRITES);

        if (!Files.exists(dirDestination)) {
            Files.createDirectories(dirDestination);
        }

        Path fileTarget = dirDestination.resolve(spriteFileName);
        try (InputStream in = currentSpriteUrl.openStream()) {
            Files.copy(in, fileTarget, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible to save sprite file: " + fileTarget, e);
        }
    }
}
