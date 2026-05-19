package ulb.repositories.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
import ulb.models.item.Inventory;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.StaticRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.InventoryDTO;

public class PostgresStaticRepository extends AbstractRepository implements StaticRepository {

    private final Map<String, Attack> attackCache;
    private final Map<String, Bugemon> bugemonCache;
    private final InventoryDTO defaultInventoryCache;

    public PostgresStaticRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            InventoryDTO defaultInventory) {
        super(dbConnection, queries);
        this.attackCache = Collections.unmodifiableMap(this.loadAllAttacks());
        this.bugemonCache = Collections.unmodifiableMap(this.loadAllBugemons());
        this.defaultInventoryCache = defaultInventory;
    }

    @Override
    public List<Bugemon> findBugemons() {
        return Collections.unmodifiableList(this.bugemonCache.values().stream().toList());
    }

    public Optional<Bugemon> findBugemonByName(String name) {
        return Optional.ofNullable(this.bugemonCache.get(name));
    }

    @Override
    public List<Attack> findAttacks() {
        return Collections.unmodifiableList(this.attackCache.values().stream().toList());
    }

    @Override
    public void saveBugemon(CreateBugemonDTO bugemon) {
        String fileName = bugemon.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";
        try {
            this.saveSpriteFile(bugemon.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executeUpdate("SaveBugemon", bugemon.name(), bugemon.type().name(), fileName,
                bugemon.defense(), bugemon.attack(), bugemon.initiative(), bugemon.maxHp(),
                bugemon.isStarter(), bugemon.attack1().id(), bugemon.attack2().id(), bugemon.attack3().id());
        this.bugemonCache.put(bugemon.name(), new Bugemon(bugemon.name(), bugemon.maxHp(), bugemon.attack(),
                bugemon.defense(), bugemon.initiative(), bugemon.type(),
                List.of(bugemon.attack1(), bugemon.attack2(), bugemon.attack3()),
                fileName, bugemon.isStarter()));
    }

    @Override
    public InventoryDTO defaultInventory() {
        return this.defaultInventoryCache;
    }

    // --- Private loading ---

    private Map<String, Attack> loadAllAttacks() {
        Map<String, AttackInfo> infos = new LinkedHashMap<>();
        Map<String, List<Effect>> effects = new HashMap<>();

        this.executeQuery("GetAllAttacksWithEffects", rs -> {
            String id = rs.getString("attack_id");
            infos.computeIfAbsent(id, k -> {
                effects.put(k, new ArrayList<>());
                try {
                    return new AttackInfo(
                            rs.getString("attack_name"),
                            DatabaseHelper.getEnumOrNull(rs, "attack_type", ElementType.class),
                            rs.getString("attack_description"),
                            rs.getInt("attack_power"));
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

        Map<String, Attack> result = new HashMap<>();
        infos.forEach((id, info) -> result.put(id,
                new Attack(id, info.name, info.description, info.power, info.type, effects.get(id))));
        return result;
    }

    private Map<String, Bugemon> loadAllBugemons() {
        List<Bugemon> bugemons = this.executeQuery("GetAllDefaultBugemons", this::mapBugemon);
        Map<String, Bugemon> bugemonsMap = new HashMap<>();
        bugemons.forEach(b -> bugemonsMap.put(b.name(), b));
        return bugemonsMap;
    }

    private Bugemon mapBugemon(ResultSet rs) throws SQLException {
        ElementType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE, ElementType.class);
        String attackId1 = rs.getString(DatabaseColumns.COL_ATTACK_ID_1);
        String attackId2 = rs.getString(DatabaseColumns.COL_ATTACK_ID_2);
        String attackId3 = rs.getString(DatabaseColumns.COL_ATTACK_ID_3);
        return new Bugemon(
                rs.getString(DatabaseColumns.COL_NAME),
                rs.getInt(DatabaseColumns.COL_BASE_MAX_HP),
                rs.getInt(DatabaseColumns.COL_BASE_ATTACK),
                rs.getInt(DatabaseColumns.COL_BASE_DEFENSE),
                rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE),
                type,
                List.of(this.attackCache.get(attackId1), this.attackCache.get(attackId2),
                        this.attackCache.get(attackId3)),
                rs.getString(DatabaseColumns.COL_SPRITE),
                rs.getBoolean(DatabaseColumns.COL_IS_STARTER));
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

    private void saveSpriteFile(URL spriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(Configuration.Paths.SPRITES);
        if (!Files.exists(dirDestination)) {
            Files.createDirectories(dirDestination);
        }
        Path fileTarget = dirDestination.resolve(spriteFileName);
        try (InputStream in = spriteUrl.openStream()) {
            Files.copy(in, fileTarget, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible to save sprite file: " + fileTarget, e);
        }
    }

    private static class AttackInfo {
        final String name;
        final ElementType type;
        final String description;
        final int power;

        AttackInfo(String nameParam, ElementType typeParam, String descriptionParam, int powerParam) {
            this.name = nameParam;
            this.type = typeParam;
            this.description = descriptionParam;
            this.power = powerParam;
        }
    }
}
