package ulb.repositories.postgres;

/**
 * Centralised string constants for every PostgreSQL column name used by the repository layer.
 *
 * <p>
 * Using these constants instead of inline string literals prevents typos and makes column renames a
 * single-point-of-change operation. The class is non-instantiable.
 */
public class DatabaseColumns {
    private DatabaseColumns() {
        // Private constructor to prevent instantiation
    }

    public static final String COL_PLAYERNAME = "playername";
    public static final String COL_BUGEMON_NAME = "bugemon_name";
    public static final String COL_CURRENT_TEAM = "current_team";
    public static final String COL_CURRENT_DEFENSE = "current_defense";
    public static final String COL_CURRENT_ATTACK = "current_attack";
    public static final String COL_CURRENT_INITIATIVE = "current_initiative";
    public static final String COL_CURRENT_MAX_HP = "current_max_hp";
    public static final String COL_CURRENT_XP = "current_xp";
    public static final String COL_CURRENT_LEVEL = "current_level";
    public static final String COL_CURRENT_TOWER_FLOOR = "current_tower_floor";
    public static final String COL_TEAM_NAME = "team_name";
    public static final String COL_SLOT_POSITION = "slot_position";
    public static final String COL_NAME = "name";
    public static final String COL_TARGET = "target";
    public static final String COL_STAT = "stat";
    public static final String COL_DURATION = "duration";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_TYPE = "type";
    public static final String COL_ID = "id";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_POWER = "power";
    public static final String COL_SPRITE = "sprite";
    public static final String COL_MODIFIER = "modifier";
    public static final String COL_BASE_DEFENSE = "base_defense";
    public static final String COL_BASE_ATTACK = "base_attack";
    public static final String COL_BASE_INITIATIVE = "base_initiative";
    public static final String COL_BASE_MAX_HP = "base_max_hp";
    public static final String COL_IS_STARTER = "is_starter";
    public static final String COL_ATTACK_ID_1 = "attack_1_id";
    public static final String COL_ATTACK_ID_2 = "attack_2_id";
    public static final String COL_ATTACK_ID_3 = "attack_3_id";
    public static final String COL_EFFECT_TYPE = "effect_type";
    public static final String COL_EFFECT_TARGET = "effect_target";
    public static final String COL_EFFECT_VALUE = "effect_value";
    public static final String COL_EFFECT_STAT = "effect_stat";
    public static final String COL_EFFECT_MODIFIER = "effect_modifier";
    public static final String COL_EFFECT_DURATION = "effect_duration";
    public static final String COL_ITEM_ID = "item_id";
    public static final String COL_CATEGORY = "category";
    public static final String COL_VALUE = "value";
    public static final String COL_SKILL_ID = "skill_id";
    public static final String COL_SKILL_POINTS = "skill_points";
    public static final String COL_INT_VALUE = "int_value";
    public static final String COL_DOUBLE_VALUE = "double_value";
    public static final String COL_SEED = "seed";
    public static final String COL_FLOOR = "current_floor";
    public static final String COL_ROW = "row";
    public static final String COL_COL = "col";
    public static final String COL_CURRENT_HP = "current_hp";
}
