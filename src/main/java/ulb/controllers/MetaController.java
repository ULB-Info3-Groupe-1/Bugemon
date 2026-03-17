package ulb.controllers;

import java.io.IOException;
import java.util.List;
import javafx.stage.Stage;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.ManualCombatController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;

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
    private final BugemonTeam playerTeam;
    private final LevelUpController levelUpController;
    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;

        this.playerTeam = new BugemonTeam();
        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this, playerTeam);
        this.manualCombatController = new ManualCombatController(this);
        this.automaticCombatController = new AutomaticCombatController(this);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
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

    /**
     * Instructs the {@link AutomaticCombatController} to start an automatic
     * combat using the player's current team.
     *
     * <p>
     * If the player's team is empty, an alert dialog is displayed and no combat
     * is started. Otherwise, the adversary team is built by randomly sampling
     * the pool of all available Bugemons (same size as the player's team), and
     * the application navigates to the {@link Window#COMBAT} screen.
     * </p>
     *
     * <p>
     * In an automatic combat both sides choose their actions randomly each turn;
     * see {@link AutomaticCombatController#runAutoCombat(AutoTrainer)} for
     * details.
     * </p>
     */
    public void launchAutoCombat() {
        switchTo(Window.AUTOMATIC_COMBAT);
        this.automaticCombatController.runAutoCombat(new AutoTrainer(this.playerTeam));
    }

    /**
     * Instructs the {@link ManualCombatController} to start a manual combat
     * using the player's current team.
     *
     * <p>
     * If the player's team is empty, an alert dialog is displayed and no combat
     * is started. Otherwise, the adversary team is built by randomly sampling
     * the pool of all available Bugemons (same size as the player's team), and
     * the application navigates to the {@link Window#COMBAT} screen.
     * </p>
     *
     * <p>
     * In a manual combat the player selects their action each turn via the UI;
     * see {@link ManualCombatController#runManualCombat(ManualTrainer)} for
     * details.
     * </p>
     */
    public void launchManualCombat() {
        switchTo(Window.MANUAL_COMBAT);
        this.manualCombatController.runManualCombat(new ManualTrainer(this.playerTeam));
    }

    /**
     * Resets every {@link ulb.models.bugemon.Bugemon} in the player's team to
     * its initial stats by delegating to
     * {@link ulb.models.bugemon_team.BugemonTeam#reset()}.
     *
     * <p>
     * This method is called by the outcome controllers
     * ({@link CombatVictoryController}, {@link CombatDefeatController}) after a
     * combat session ends so that the team is fully restored before the next
     * session.
     * </p>
     */
    public void resetTeam() {
        this.playerTeam.reset();
    }

    public void setLevelUp(List<LevelUp> levelUps) {
        this.levelUpController.setLevelUp(levelUps);
    }
}
