package ulb;

import ulb.models.combat.damage.DamageCalculator.AttackFactorFormula;
import ulb.models.combat.damage.DamageCalculator.ReductionFactorFormula;

public abstract class Configuration {

    private Configuration() {
        // Private constructor to prevent instantiation
    }

    public static final class Paths {

        private Paths() {
            // Private constructor to prevent instantiation
        }

        public static final String SPRITES = "assets/sprites/";
        public static final String DEFAULT_SPRITE = "/png/unknown.png";
        public static final String ROOM_BASE_PATH = "/png/rooms/";
        public static final String SQL_BASE_PATH = "/sql/";

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

            public static final String SKILL_TREE_VIEW = "/fxml/SkillTree.fxml";
        }
    }

    public static final class Ui {

        private Ui() {
            // Private constructor to prevent instantiation
        }

        public static final String STAGE_TITLE = "Bugemon";

        public static final String FLOOR_PREFIX = "NO";
    }

    public static final class Music {

        private Music() {
            // Private constructor to prevent instantiation
        }

        public static final String MUSIC_PATH_COMBAT = "/musics/combat";
        public static final String MUSIC_PATH_MENU = "/musics/menu";
        public static final String SOUND_EFFECTS_PATH_VICTORY = "/sound_effects/victory";
        public static final String SOUND_EFFECTS_PATH_DEFEAT = "/sound_effects/defeat";

    }

    public static final class Game {

        private Game() {
            // Private constructor to prevent instantiation
        }

        public static final int ATTACKS_COUNT = 3; // view can't handle more than 3 attacks for now
        public static final int MAX_TEAM_SIZE = 6;
        // TODO: define (and enforce) this directly in Bugemon
        public static final int NUM_ATTACKS_PER_BUGEMON = 3;
        public static final String DEFAULT_TEAM_NAME = "Unnamed Team";
        public static final String BOSS_NAME = "FinalBoss";
        public static final int FLOOR_MAX = 9; // The highest floor is 9
        public static final int FLOOR_MIN = 2; // The lowest floor is 2
        public static final double BASE_CRIT_CHANCE = 0.10;
        public static final double CRIT_DAMAGE_FACTOR = 1.5;

        public static final AttackFactorFormula ATTACK_FACTOR_FORMULA = //
                effectiveAttack -> (100.0 + effectiveAttack) / 100.0;
        public static final ReductionFactorFormula REDUCTION_FACTOR_FORMULA = //
                effectiveDefense -> 100.0 / (100 + effectiveDefense);
    }

    public static final class Skill {
        private Skill() {
            // Private constructor to prevent instantiation
        }

        public static final String DEFAULT_ID = "start";
        public static final String DEFAULT_NAME = "départ";
        public static final String DEFAULT_DESCRIPTION = "Point de départ de l'arbre";

    }

    public static final class Json {

        private Json() {
            // Private constructor to prevent instantiation
        }

        public static final String ATTACK_PATH = "/json/attaques.json";
        public static final String BUGEMON_PATH = "/json/bugemons.json";
        public static final String ITEMS_PATH = "/json/objets.json";
        public static final String SKILL_TREE_PATH = "/json/skill_tree.json";

    }
}
