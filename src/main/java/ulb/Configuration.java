package ulb;

/**
 * Static configuration values
 */
public abstract class Configuration {

    private Configuration() {
        // Private constructor to prevent instantiation
    }

    /**
     * Static paths
     */
    public static final class Paths {

        private Paths() {
            // Private constructor to prevent instantiation
        }

        /** The path to the sprite folder */
        public static final String SPRITES = "assets/sprites/";
        /** The path to the default sprite */
        public static final String DEFAULT_SPRITE = "/png/unknown.png";
        /** The path to the room folder */
        public static final String ROOM_BASE_PATH = "/png/rooms/";

        /**
         * The path to the FXML files
         */
        public static final class Fxml {

            private Fxml() {
                // Private constructor to prevent instantiation
            }

            /** The path to the FXML files */
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

        }
    }

    /**
     * Static UI values
     */
    public static final class Ui {

        private Ui() {
            // Private constructor to prevent instantiation
        }

        /** The title of the application */
        public static final String STAGE_TITLE = "Bugemon";
    }

    /**
     * Static music and sound effect paths
     */
    public static final class Music {

        private Music() {
            // Private constructor to prevent instantiation
        }

        /** The path to the music and sound effects folder */
        public static final String MUSIC_PATH_COMBAT = "/musics/combat";
        public static final String MUSIC_PATH_MENU = "/musics/menu";
        public static final String SOUND_EFFECTS_PATH_VICTORY = "/sound_effects/victory";
        public static final String SOUND_EFFECTS_PATH_DEFEAT = "/sound_effects/defeat";

    }

    /**
     * Static game values
     */
    public static final class Game {

        private Game() {
            // Private constructor to prevent instantiation
        }

        /** The maximum size of a team */
        public static final int MAX_TEAM_SIZE = 6;
        /** The default name of a team */
        public static final String DEFAULT_TEAM_NAME = "Unnamed Team";
        /** The name of the boss */
        public static final String BOSS_NAME = "FinalBoss";

        /** The minimum and maximum number of floors */
        public static final int FLOOR_MAX = 8; // The highest floor is NO8
        public static final int FLOOR_MIN = 2; // The lowest floor is NO2

    }

    /**
     * Static JSON paths
     */
    public static final class Json {

        private Json() {
            // Private constructor to prevent instantiation
        }

        /** The path to the JSON files */
        public static final String ATTACK_PATH = "/json/attaques.json";
        public static final String BUGEMON_PATH = "/json/bugemons.json";
        public static final String ITEMS_PATH = "/json/objets.json";

    }
}
