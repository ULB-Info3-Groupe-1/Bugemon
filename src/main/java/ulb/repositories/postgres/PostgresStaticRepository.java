package ulb.repositories.postgres;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import ulb.common.dto.persistence.CreateBugemonDTO;
import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.effect.Effect;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.item.Item;
import ulb.models.item.ItemType;
import ulb.models.skills.SkillEffect;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.StaticRepository;
import ulb.utils.SpriteUtils;

public class PostgresStaticRepository extends AbstractRepository implements StaticRepository {

    private final Map<String, Attack> attackCache;
    private final DefaultInventoryDTO defaultInventoryCache;
    private final SkillTree skillTreeCache;
    private Map<String, Bugemon> bugemonCache;
    private List<Item> itemsCache;

    public PostgresStaticRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            DefaultInventoryDTO defaultInventory) {
        super(dbConnection, queries);
        this.attackCache = Collections.unmodifiableMap(this.loadAllAttacks());
        this.bugemonCache = Collections.unmodifiableMap(this.loadAllBugemons());
        this.itemsCache = Collections.unmodifiableList(this.loadItems());
        this.skillTreeCache = this.loadSkillTree();
        this.defaultInventoryCache = defaultInventory;
    }

    @Override
    public List<Bugemon> bugemons() {
        return Collections.unmodifiableList(this.bugemonCache.values().stream().toList());
    }

    public Optional<Bugemon> findBugemonByName(String name) {
        return Optional.ofNullable(this.bugemonCache.get(name));
    }

    @Override
    public List<Attack> attacks() {
        return Collections.unmodifiableList(this.attackCache.values().stream().toList());
    }

    @Override
    public void saveBugemon(CreateBugemonDTO bugemon) {
        String fileName = bugemon.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";
        try {
            SpriteUtils.saveSpriteFile(bugemon.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executeUpdate("SaveBugemon", bugemon.name(), bugemon.type().name(), fileName, bugemon.defense(),
                bugemon.attack(), bugemon.initiative(), bugemon.maxHp(), bugemon.isStarter(),
                bugemon.attacks().get(0).id(), bugemon.attacks().get(1).id(), bugemon.attacks().get(2).id());
        this.bugemonCache = Collections.unmodifiableMap(this.loadAllBugemons());
    }

    @Override
    public DefaultInventoryDTO defaultInventory() {
        return this.defaultInventoryCache;
    }

    @Override
    public SkillTree skillTree() {
        return this.skillTreeCache;
    }

    @Override
    public List<Item> items() {
        return this.itemsCache;
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

        // NOTE: there is no isBoss flag in db (because the same goes for the given json). Therefore the value of the
        // isBoss flag is recomputed from the configured boss name.
        String name = rs.getString(DatabaseColumns.COL_NAME);
        boolean isBoss = Configuration.Game.BOSS_NAME.equals(name);

        return new Bugemon(name, rs.getInt(DatabaseColumns.COL_BASE_MAX_HP), rs.getInt(DatabaseColumns.COL_BASE_ATTACK),
                rs.getInt(DatabaseColumns.COL_BASE_DEFENSE), rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE), type,
                List.of(this.attackCache.get(attackId1), this.attackCache.get(attackId2),
                        this.attackCache.get(attackId3)),
                rs.getString(DatabaseColumns.COL_SPRITE), rs.getBoolean(DatabaseColumns.COL_IS_STARTER), isBoss);
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

    private List<Item> loadItems() {
        List<Item> items = this.executeQuery("GetAllItems", rs -> {
            try {
                String effectType = rs.getString(DatabaseColumns.COL_EFFECT_TYPE);
                Effect effect = null;
                if (effectType != null) {
                    effect = this.buildItemEffect(rs, effectType);
                }
                return new Item(rs.getString(DatabaseColumns.COL_ITEM_ID), rs.getString(DatabaseColumns.COL_NAME),
                        rs.getString(DatabaseColumns.COL_DESCRIPTION),
                        ItemType.valueOf(rs.getString(DatabaseColumns.COL_CATEGORY)), effect);
            } catch (SQLException e) {
                throw new IllegalStateException("Error loading item", e);
            }
        });
        return items;
    }

    private Effect buildItemEffect(ResultSet rs, String effectType) throws SQLException {
        EffectTarget target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_EFFECT_TARGET, EffectTarget.class);
        return switch (effectType) {
            case "EffectHeal" -> new HealEffect(target, rs.getInt(DatabaseColumns.COL_EFFECT_VALUE));
            case "EffectStatModifier" -> {
                StatType stat = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_EFFECT_STAT, StatType.class);
                yield new StatModifierEffect(target, stat, rs.getInt(DatabaseColumns.COL_EFFECT_MODIFIER),
                        rs.getInt(DatabaseColumns.COL_EFFECT_DURATION) == 0 ? EffectDuration.PERMANENT
                                : EffectDuration.ONE_TURN);
            }
            case "EffectResetMalus" -> new ResetMalusEffect(target);
            default -> throw new IllegalStateException("Unknown item effect type: " + effectType);
        };
    }

    private SkillTree loadSkillTree() {
        Map<String, SkillNodeData> nodeData = new LinkedHashMap<>();
        this.executeQuery("GetAllSkillNodes", rs -> {
            String id = rs.getString("id");
            nodeData.computeIfAbsent(id, k -> {
                try {
                    SkillEffect effect = null;
                    String effectType = rs.getString("effect_type");
                    if (effectType != null) {
                        effect = this.buildSkillEffect(rs, effectType);
                    }
                    return new SkillNodeData(rs.getString("name"), rs.getString("description"), rs.getInt("x"),
                            rs.getInt("y"), rs.getInt("max_level"), rs.getInt("cost"), effect);
                } catch (SQLException e) {
                    throw new IllegalStateException("Error loading skill node " + id, e);
                }
            });
            return null;
        });

        Map<String, List<String>> prereqs = new HashMap<>();
        this.executeQuery("GetAllSkillPrerequisites", rs -> {
            prereqs.computeIfAbsent(rs.getString("skill_id"), k -> new ArrayList<>())
                    .add(rs.getString("prerequisite_id"));
            return null;
        });

        List<SkillNode> nodes = new ArrayList<>();
        nodeData.forEach((id, data) -> nodes.add(new SkillNode(id, data.name, data.description, data.x, data.y,
                data.maxLevel, data.cost, data.effect, prereqs.getOrDefault(id, List.of()))));
        return new SkillTree(nodes);
    }

    private SkillEffect buildSkillEffect(ResultSet rs, String type) throws SQLException {
        return switch (type) {
            case "stat_bonus" -> new SkillEffect.StatBonusEffect(StatType.valueOf(rs.getString("stat")),
                    rs.getInt(DatabaseColumns.COL_INT_VALUE));
            case "type_multiplicateur" -> new SkillEffect.TypeMultiplierEffect(
                    ElementType.valueOf(rs.getString("element_type")), rs.getDouble(DatabaseColumns.COL_DOUBLE_VALUE));
            case "critique_bonus" -> new SkillEffect.CritBonusEffect(rs.getDouble(DatabaseColumns.COL_DOUBLE_VALUE));
            case "regen_post_combat" ->
                new SkillEffect.RegenPostCombatEffect(rs.getDouble(DatabaseColumns.COL_DOUBLE_VALUE));
            case "xp_multiplicateur" ->
                new SkillEffect.XpMultiplierEffect(rs.getDouble(DatabaseColumns.COL_DOUBLE_VALUE));
            case "objets_bonus" ->
                new SkillEffect.StarterItemsEffect(rs.getInt(DatabaseColumns.COL_INT_VALUE), rs.getString("category"));
            case "recompense_choix" -> new SkillEffect.RewardChoiceEffect(rs.getInt(DatabaseColumns.COL_INT_VALUE));
            default -> throw new IllegalStateException("Unknown skill effect type: " + type);
        };
    }

    private static class SkillNodeData {
        final String name;
        final String description;
        final int x;
        final int y;
        final int maxLevel;
        final int cost;
        final SkillEffect effect;

        SkillNodeData(String name, String description, int x, int y, int maxLevel, int cost, SkillEffect effect) {
            this.name = name;
            this.description = description;
            this.x = x;
            this.y = y;
            this.maxLevel = maxLevel;
            this.cost = cost;
            this.effect = effect;
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
