package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javafx.stage.Stage;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.controllers.combat.ManualCombatController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.services.PlayerService;

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
    private final Map<Window, Runnable> transitions = new EnumMap<>(Window.class);
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final LevelUpController levelUpController;
    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @param playerService service for managing player data and persistence
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage, PlayerService playerService) throws IOException {
        this.stage = primaryStage;

        this.mainMenuController = new MainMenuController(this, playerService);
        this.createTeamController = new CreateTeamController(this, playerService);
        this.manualCombatController = new ManualCombatController(this, playerService);
        this.automaticCombatController = new AutomaticCombatController(this, playerService);
        this.combatVictoryController = new CombatVictoryController(this, playerService);
        this.combatDefeatController = new CombatDefeatController(this, playerService);
        this.levelUpController = new LevelUpController(this);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        musicLoader.loadFromDirectory("/musics/combat", Ambiance.COMBAT)
                .forEach(this.musicPlayer::addMusic);
        musicLoader.loadFromDirectory("/musics/menu", Ambiance.MENU)
                .forEach(this.musicPlayer::addMusic);

        this.manualCombatController.setOnVictory(levelUpController::setLevelUp);
        this.automaticCombatController.setOnVictory(levelUpController::setLevelUp);

        initTransitions();
    }

    private void initTransitions() {
        transitions.put(Window.MAIN_MENU, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU);
            mainMenuController.show(stage);
        });
        transitions.put(Window.CREATE_TEAM, () -> createTeamController.show(stage));
        transitions.put(Window.MANUAL_COMBAT, () -> {
            musicPlayer.playAmbiance(Ambiance.COMBAT);
            manualCombatController.startCombat();
            manualCombatController.show(stage);
        });
        transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            musicPlayer.playAmbiance(Ambiance.COMBAT);
            automaticCombatController.startCombat();
            automaticCombatController.show(stage);
        });
        transitions.put(Window.COMBAT_VICTORY, () -> combatVictoryController.show(stage));
        transitions.put(Window.COMBAT_DEFEAT, () -> combatDefeatController.show(stage));
        transitions.put(Window.LEVEL_UP, () -> levelUpController.show(stage));
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window target screen to display
     * @throws IllegalArgumentException if the window is invalid
     */
    public final void switchTo(Window window) {
        Runnable transition = transitions.get(window);
        if (transition == null)
            throw new IllegalArgumentException("Unknown window: " + window);
        musicPlayer.stopMusic();
        transition.run();
    }
}
