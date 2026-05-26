package ulb;

import ulb.models.combat.damage.DamageCalculator.AttackFactorFormula;
import ulb.models.combat.damage.DamageCalculator.ReductionFactorFormula;

/**
 * Central repository of compile-time constants for the Bugemon application.
 *
 * <p>
 * All values are grouped into static nested classes by concern so that call sites read as
 * {@code Configuration.Game.MAX_TEAM_SIZE} instead of bare magic numbers. This class is non-instantiable; all nested
 * classes are likewise non-instantiable.
 */
public abstract class Configuration {

    private Configuration() {
        // Private constructor to prevent instantiation
    }

    /** Filesystem and classpath paths to assets used at runtime. */
    public static final class Paths {

        private Paths() {
            // Private constructor to prevent instantiation
        }

        /** Relative path prefix for Bugemon sprite assets. */
        public static final String SPRITES = "assets/sprites/";
        /** Classpath path to the fallback sprite shown when a Bugemon has no sprite. */
        public static final String DEFAULT_SPRITE = "/png/unknown.png";
        /** Classpath prefix for room background images. */
        public static final String ROOM_BASE_PATH = "/png/rooms/";
        /** Classpath path to the player trainer icon displayed on the floor map. */
        public static final String PLAYER_ICON = "/png/Trainer.png";
        /** Classpath prefix for SQL script files loaded by {@link ulb.repositories.QueryLoader}. */
        public static final String SQL_BASE_PATH = "/sql/";

        /** Classpath paths for the application's CSS stylesheets. */
        public static final class Css {

            private Css() {
                // Private constructor to prevent instantiation
            }

            /** Design-token variables (colors, spacing, …). Must be loaded first. */
            public static final String TOKENS = "/css/tokens.css";
            /** Base layout and typography styles. */
            public static final String BASE = "/css/base.css";
            /** Button styles. */
            public static final String BUTTONS = "/css/buttons.css";
            /** Bugemon card and stat panel styles. */
            public static final String BUGEMON = "/css/bugemon.css";
            /** Combat screen styles. */
            public static final String COMBAT = "/css/combat.css";
            /** Menu screen styles. */
            public static final String MENUS = "/css/menus.css";
            /** Reward screen styles. */
            public static final String REWARD = "/css/reward.css";

            /**
             * Ordered list of stylesheets to apply to the {@link javafx.scene.Scene}. Tokens must appear before any
             * stylesheet that references them.
             */
            public static final java.util.List<String> LOAD_ORDER = java.util.List.of(TOKENS, BASE, BUTTONS, BUGEMON,
                    COMBAT, MENUS, REWARD);
        }

        /** Classpath paths for FXML view descriptors. */
        public static final class Fxml {

            private Fxml() {
                // Private constructor to prevent instantiation
            }

            public static final String COMPONENT_ACTION_MENU = "/fxml/components/ActionMenu.fxml";
            public static final String COMPONENT_ATTACK_MENU = "/fxml/components/AttackMenu.fxml";
            public static final String COMPONENT_BUGEMON_INFO = "/fxml/components/BugemonInfo.fxml";
            public static final String COMPONENT_ITEM_MENU = "/fxml/components/ItemMenu.fxml";
            public static final String COMPONENT_SWITCH_MENU = "/fxml/components/SwitchMenu.fxml";
            public static final String COMPONENT_ALL_BUGEMONS = "/fxml/components/AllBugemons.fxml";
            public static final String COMPONENT_BUGEMON_CARD = "/fxml/components/BugemonCard.fxml";
            public static final String COMPONENT_BUGEMON_DETAIL_POPUP = "/fxml/components/BugemonDetailPopup.fxml";
            public static final String COMPONENT_BUGEMON_TEAM = "/fxml/components/BugemonTeam.fxml";
            public static final String COMPONENT_DIALOG_ZONE = "/fxml/components/DialogZone.fxml";
            public static final String COMPONENT_HOVER_INFO = "/fxml/components/HoverInfo.fxml";
            public static final String COMPONENT_ROOM_NODE = "/fxml/components/RoomNode.fxml";
            public static final String COMPONENT_ROOM = "/fxml/components/Room.fxml";

            public static final String COMBAT_VIEW = "/fxml/Combat.fxml";
            public static final String COMBAT_VICTORY_VIEW = "/fxml/CombatVictory.fxml";
            public static final String COMBAT_DEFEAT_VIEW = "/fxml/CombatDefeat.fxml";
            public static final String LEVEL_UP_VIEW = "/fxml/LevelUp.fxml";
            public static final String MAIN_MENU_VIEW = "/fxml/MainMenu.fxml";
            public static final String MANAGE_TEAM_VIEW = "/fxml/ManageTeam.fxml";
            public static final String CREATE_BUGEMON_VIEW = "/fxml/CreateBugemon.fxml";
            public static final String SAVE_MENU_VIEW = "/fxml/SaveMenu.fxml";
            public static final String LOGIN_VIEW = "/fxml/Login.fxml";

            public static final String FLOOR_VIEW = "/fxml/Floor.fxml";
            public static final String SKILL_TREE_VIEW = "/fxml/SkillTree.fxml";
            public static final String REWARD_VIEW = "/fxml/Reward.fxml";
        }
    }

    /** User-interface display constants. */
    public static final class Ui {

        private Ui() {
            // Private constructor to prevent instantiation
        }

        /** Title displayed in the application window's title bar. */
        public static final String STAGE_TITLE = "Bugemon";

        /** Prefix prepended to floor numbers on the floor map label (e.g. "NO 3"). */
        public static final String FLOOR_PREFIX = "NO";
        /** Duration in milliseconds of the player-movement animation on the floor map. */
        public static final int FLOOR_MOVE_ANIMATION_MS = 500;
        /** Base font size in points used when loading the pixel-art font. */
        public static final int FONT_SIZE = 16;
    }

    /** Classpath prefixes for background music and sound-effect tracks. */
    public static final class Music {

        private Music() {
            // Private constructor to prevent instantiation
        }

        /** Classpath directory prefix for combat music files. */
        public static final String MUSIC_PATH_COMBAT = "/musics/combat";
        /** Classpath directory prefix for menu music files. */
        public static final String MUSIC_PATH_MENU = "/musics/menu";
        /** Classpath directory prefix for the victory sound effect. */
        public static final String SOUND_EFFECTS_PATH_VICTORY = "/sound_effects/victory";
        /** Classpath directory prefix for the defeat sound effect. */
        public static final String SOUND_EFFECTS_PATH_DEFEAT = "/sound_effects/defeat";

    }

    /** Core game-rule constants governing combat, teams, XP, and stat growth. */
    public static final class Game {

        private Game() {
            // Private constructor to prevent instantiation
        }

        /** Maximum number of attacks shown in the combat UI (view cannot handle more). */
        public static final int ATTACKS_COUNT = 3;
        /** Minimum number of Bugemons required to start a run. */
        public static final int MIN_TEAM_SIZE = 1;
        /** Maximum number of Bugemons allowed in a team. */
        public static final int MAX_TEAM_SIZE = 6;
        /** Number of attacks assigned to each Bugemon. */
        public static final int NUM_ATTACKS_PER_BUGEMON = 3;
        /** Default name given to newly created teams. */
        public static final String DEFAULT_TEAM_NAME = "Unnamed Team";
        /** Identifier used to look up the final-boss encounter in the database. */
        public static final String BOSS_NAME = "FinalBoss";
        /** Highest tower floor number reachable by the player. */
        public static final int FLOOR_MAX = 9;
        /** Lowest tower floor number (floor of the first non-tutorial encounter). */
        public static final int FLOOR_MIN = 2;
        /** Base critical-hit probability (10%). */
        public static final double BASE_CRIT_CHANCE = 0.10;
        /** Damage multiplier applied when a critical hit occurs. */
        public static final double CRIT_DAMAGE_FACTOR = 1.5;

        /** Base XP awarded to each surviving Bugemon after a normal combat. */
        public static final int BASE_XP = 30;
        /** XP multiplier applied when the defeated opponent is the boss. */
        public static final int BOSS_MULTIPLIER = 2;
        /** XP multiplier applied for regular (non-boss) opponents. */
        public static final int NORMAL_MULTIPLIER = 1;

        /** Number of stat-bonus points distributed per level-up bonus choice. */
        public static final int NUM_POINTS_PER_BONUS = 10;
        /** Number of bonus choices offered to the player at each level-up. */
        public static final int NUM_BONUS_PER_LEVEL_UP = 3;
        /** HP gained for each stat-bonus point spent on HP. */
        public static final int HP_GAIN_PER_POINT = 2;
        /** Attack power gained for each stat-bonus point spent on attack. */
        public static final int ATTACK_GAIN_PER_POINT = 1;
        /** Defense gained for each stat-bonus point spent on defense. */
        public static final int DEFENSE_GAIN_PER_POINT = 1;
        /** Initiative gained for each stat-bonus point spent on initiative. */
        public static final int INITIATIVE_GAIN_PER_POINT = 2;

        /** Quantity of an item granted as a single item-reward after combat. */
        public static final int ITEM_QUANTITY_FOR_ITEM_REWARD = 1;

        /**
         * Formula that converts effective attack into an attack factor. Evaluates to
         * {@code (100 + effectiveAttack) / 100}.
         */
        public static final AttackFactorFormula ATTACK_FACTOR_FORMULA = effectiveAttack -> (100.0 + effectiveAttack)
                / 100.0;

        /**
         * Formula that converts effective defense into a damage-reduction factor. Evaluates to
         * {@code 100 / (100 + effectiveDefense)}.
         */
        public static final ReductionFactorFormula REDUCTION_FACTOR_FORMULA = effectiveDefense -> 100.0
                / (100 + effectiveDefense);

        /**
         * Minimum value for the configurable maximum depth of a floor branch, used to prevent degenerate floor layouts
         * during generation.
         */
        public static final int MIN_MAX_MAXIMAL_DEPTH = 4;
    }

    /** Constants for the meta-progression skill tree. */
    public static final class Skill {
        private Skill() {
            // Private constructor to prevent instantiation
        }

        /** Identifier of the root skill node (the always-unlocked starting point). */
        public static final String DEFAULT_ID = "start";
        /** Display name of the root skill node. */
        public static final String DEFAULT_NAME = "départ";
        /** Description shown for the root skill node. */
        public static final String DEFAULT_DESCRIPTION = "Point de départ de l'arbre";

        /** Number of skill choices offered to the player after each level-up. */
        public static final int DEFAULT_LEVEL_UP_CHOICE_COUNT = 3;

    }

    /** Classpath locations of JSON data files parsed at startup. */
    public static final class Json {

        private Json() {
            // Private constructor to prevent instantiation
        }

        /** Classpath path to the attack definitions JSON file. */
        public static final String ATTACK_PATH = "/json/attaques.json";
        /** Classpath path to the Bugemon species definitions JSON file. */
        public static final String BUGEMON_PATH = "/json/bugemons.json";
        /** Classpath path to the item definitions JSON file. */
        public static final String ITEMS_PATH = "/json/objets.json";
        /** Classpath path to the skill-tree structure JSON file. */
        public static final String SKILL_TREE_PATH = "/json/skill_tree.json";

    }

    /** Constants that govern procedural floor-map generation. */
    public static final class FloorMap {
        private FloorMap() {
            // Private constructor to prevent instantiation
        }

        /** Side length of the square grid used to place room nodes. */
        public static final int GRID_SIZE = 5;
        /** Maximum number of nodes along any single branch from the root. */
        public static final int MAX_DEPTH = 6;
        /** Grid coordinate of the center cell ({@code GRID_SIZE / 2}). */
        public static final int CENTER = GRID_SIZE / 2;

        /** Minimum number of branches spawned from the root node. */
        public static final int MIN_BRANCHES = 3;
        /** Maximum number of branches spawned from the root node. */
        public static final int MAX_BRANCHES = 4;

        /** Minimum number of {@link ulb.common.RoomType#COMBAT} rooms per floor. */
        public static final int MIN_COMBATS = 4;
        /** Maximum number of {@link ulb.common.RoomType#COMBAT} rooms per floor. */
        public static final int MAX_COMBATS = 6;

        /** Minimum number of {@link ulb.common.RoomType#REWARD} rooms per floor. */
        public static final int MIN_REWARD = 2;
        /** Maximum number of {@link ulb.common.RoomType#REWARD} rooms per floor. */
        public static final int MAX_REWARD = 3;

        /** Maximum number of generation retries before throwing an error. */
        public static final int MAX_GENERATION_ATTEMPTS = 10;

        /**
         * Probability ({@code 0–1}) that the generator continues in the same cardinal direction when extending a
         * branch, creating straighter corridors.
         */
        public static final double BIAS_SAME_DIRECTION_PROB = 0.7;

        /**
         * Four cardinal direction vectors {@code {dx, dy}} used during branch expansion: up, down, right, left.
         */
        public static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    }
}
