package ulb.repository;

/**
 * This class defines constants for database column names used in the application. It provides a
 * centralized location for all column name definitions, ensuring consistency across the codebase.
 * By using constants, we can avoid hardcoding column names in multiple places, making the code
 * easier to maintain and less error-prone.
 */
public class DatabaseColumns {
    private DatabaseColumns() {
        // Private constructor to prevent instantiation
    }

    public static final String COL_USER_ID = "user_id";
    public static final String COL_BUGEMON_ID = "bugemon_id";
    public static final String COL_CURRENT_DEFENSE = "current_defense";
    public static final String COL_CURRENT_ATTACK_POWER = "current_attack_power";
    public static final String COL_CURRENT_INITIATIVE = "current_initiative";
    public static final String COL_CURRENT_MAX_HP = "current_max_hp";
    public static final String COL_CURRENT_XP = "current_xp";
    public static final String COL_CURRENT_LEVEL = "current_level";
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
    public static final String COL_BASE_ATTACK_POWER = "base_attack_power";
    public static final String COL_BASE_INITIATIVE = "base_initiative";
    public static final String COL_BASE_MAX_HP = "base_max_hp";
    public static final String COL_IS_STARTER = "is_starter";
    public static final String COL_ATTACK_ID_1 = "attack_1_id";
    public static final String COL_ATTACK_ID_2 = "attack_2_id";
    public static final String COL_ATTACK_ID_3 = "attack_3_id";
}
