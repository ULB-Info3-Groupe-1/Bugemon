package ulb;

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

            public static final String COMBAT_VIEW = "/fxml/Combat.fxml";
            public static final String COMBAT_VICTORY_VIEW = "/fxml/CombatVictory.fxml";
            public static final String COMBAT_DEFEAT_VIEW = "/fxml/CombatDefeat.fxml";
            public static final String LEVEL_UP_VIEW = "/fxml/LevelUp.fxml";
            public static final String MAIN_MENU_VIEW = "/fxml/MainMenu.fxml";
            public static final String MANAGE_TEAM_VIEW = "/fxml/ManageTeam.fxml";
            public static final String CREATE_BUGEMON_VIEW = "/fxml/CreateBugemon.fxml";
            public static final String COMBAT_MENU_VIEW = "/fxml/CombatMenu.fxml";

        }
    }

    public static final class Ui {

        private Ui() {
            // Private constructor to prevent instantiation
        }

        public static final String STAGE_TITLE = "Bugemon";
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

        public static final int MAX_TEAM_SIZE = 6;
        public static final String DEFAULT_TEAM_NAME = "Unnamed Team";
        public static final String BOSS_NAME = "FinalBoss";
        public static final int FLOOR_MAX = 8; // The highest floor is NO8
        public static final int FLOOR_MIN = 2; // The lowest floor is NO2

    }

    public static final class Json {

        private Json() {
            // Private constructor to prevent instantiation
        }

        public static final String ATTACK_PATH = "/json/attaques.json";
        public static final String BUGEMON_PATH = "/json/bugemons.json";
        public static final String ITEMS_PATH = "/json/objets.json";

    }
}
