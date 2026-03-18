package ulb.controllers;

import java.io.IOException;
import java.util.List;
import javafx.stage.Stage;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.ManualCombatController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.models.bugemon.Inventory;
import ulb.models.level_up.LevelUp;
import ulb.models.player.Player;

/**
 * Central controller responsible for managing all screen controllers and
 * orchestrating application-level navigation.
 *
 * <p>
 * {@code MetaController} is instantiated once at startup by {@link ulb.Main}
 * and owns every concrete {@link Controller} in the application. It is the
 * single authority for:
 * <ul>
 * <li>Loading game resources from JSON files via {@link ulb.utils.Parser}.</li>
 * <li>Navigating between screens via
 * {@link #switchTo(Window)}.</li>
 * <li>Launching combat sessions ({@link #launchAutoCombat()},
 * {@link #launchManualCombat()}).</li>
 * <li>Resetting the player's team between sessions
 * ({@link #resetTeam()}).</li>
 * <li>Displaying application-wide alert dialogs
 * ({@link #showAlert(String, String)}).</li>
 * </ul>
 *
 * <p>
 * All lower-level controllers hold a reference to this class and call its
 * methods to trigger navigation or access shared state (e.g. the list of all
 * available Bugemons).
 * </p>
 *
 * @see Controller
 * @see Window
 * @see ulb.utils.Parser
 */
public class MetaController {
    /**
     * Enumerates all navigable screens in the application.
     *
     * <p>
     * Each constant corresponds to a concrete {@link Controller} managed by
     * the {@link MetaController}. Pass one of these values to
     * {@link MetaController#switchTo(Window)} to trigger a screen transition.
     * </p>
     */
    public enum Window {
        MAIN_MENU,
        CREATE_TEAM,
        MANUAL_COMBAT,
        AUTOMATIC_COMBAT,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
        LEVEL_UP,
    }

    private final Stage stage;
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final LevelUpController levelUpController;
    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;
    private final Player player = new Player(new Inventory());

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;

        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this, this.player);
        this.manualCombatController = new ManualCombatController(this, this.player);
        this.automaticCombatController = new AutomaticCombatController(this, this.player);
        this.combatVictoryController = new CombatVictoryController(this, this.player);
        this.combatDefeatController = new CombatDefeatController(this, this.player);
        this.levelUpController = new LevelUpController(this);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        musicLoader.loadFromDirectory("/musics/combat", Ambiance.COMBAT)
                .forEach(this.musicPlayer::addMusic);
        musicLoader.loadFromDirectory("/musics/menu", Ambiance.MENU)
                .forEach(this.musicPlayer::addMusic);
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window target screen to display
     * @throws IllegalArgumentException if the window is invalid
     */
    public final void switchTo(Window window) {
        switch (window) {
            case MAIN_MENU:
                this.musicPlayer.stopMusic();
                this.musicPlayer.playAmbiance(Ambiance.MENU);
                this.mainMenuController.show(this.stage);
                break;
            case CREATE_TEAM:
                this.musicPlayer.stopMusic();
                this.createTeamController.show(this.stage);
                break;
            case AUTOMATIC_COMBAT:
                this.musicPlayer.stopMusic();
                this.musicPlayer.playAmbiance(Ambiance.COMBAT);
                this.automaticCombatController.show(stage);
                break;
            case MANUAL_COMBAT:
                this.musicPlayer.stopMusic();
                this.musicPlayer.playAmbiance(Ambiance.COMBAT);
                this.manualCombatController.show(this.stage);
                break;
            case COMBAT_VICTORY:
                this.musicPlayer.stopMusic();
                this.combatVictoryController.show(this.stage);
                break;
            case COMBAT_DEFEAT:
                this.musicPlayer.stopMusic();
                this.combatDefeatController.show(this.stage);
                break;
            case LEVEL_UP:
                this.musicPlayer.stopMusic();
                this.levelUpController.show(this.stage);
                break;
            default:
                throw new IllegalArgumentException("Invalid window");
        }
    }

    public void startAutoCombat() {
        this.automaticCombatController.startCombat();
        this.switchTo(Window.AUTOMATIC_COMBAT);
    }

    public void startManualCombat() {
        this.manualCombatController.startCombat();
        this.switchTo(Window.MANUAL_COMBAT);
    }

    public void setLevelUp(List<LevelUp> levelUps) {
        this.levelUpController.setLevelUp(levelUps);
    }
}
