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
    }

    public static final class UI {

        private UI() {
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
        public static final String MUSIC_PATH_CREATE_TEAM = "/musics/create_team";
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
