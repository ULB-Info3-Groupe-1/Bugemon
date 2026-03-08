package ulb.controllers;

import java.io.IOException;
import java.io.InputStream;
import javafx.stage.Stage;
import ulb.utils.Parser;

/**
 * MetaController
 *
 * Central controller responsible for managing other controllers (corresponding
 * to other application screens).
 * Instantiates all controllers and handles window transitions.
 */
public class MetaController {

    /**
     * Window
     *
     * Available application screens.
     */
    public enum Window {
        MAIN_MENU,
        CREATE_TEAM,
        COMBAT,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
    }

    private static final String JSON_ATTACK_PATH = "/json/attaques.json";
    private static final String JSON_BUGEMON_PATH = "/json/bugemons.json";

    private final Stage stage;
    private final Parser.ParseResult parseResult;
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.parseResult = loadResources();
        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this, parseResult.getBugemonsList());
        this.manualCombatController = new ManualCombatController(this);
        this.automaticCombatController = new AutomaticCombatController(this);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window target screen to display
     * @throws IllegalArgumentException if the window is invalid
     */
    public final void switchTo(Window window) {
        switch (window) {
            case MAIN_MENU -> {
                this.mainMenuController.show(this.stage);
            }
            case CREATE_TEAM -> {
                this.createTeamController.show(this.stage);
            }
            case COMBAT -> {
                this.manualCombatController.show(this.stage);
            }
            case COMBAT_VICTORY ->
                this.combatVictoryController.show(this.stage);
            case COMBAT_DEFEAT ->
                this.combatDefeatController.show(this.stage);
            default ->
                throw new IllegalArgumentException("Invalid window");
        }
    }

    /**
     * Loads and parses the game data from JSON resource files.
     * @return a Parser.ParseResult containing the maps of attacks and the list of Bugemons
     * @throws IOException if the JSON directory is missing or if an error occurs during path conversion or file reading
     */
    private Parser.ParseResult loadResources() throws IOException {
        try (
            InputStream attacksStream = getClass().getResourceAsStream(
                JSON_ATTACK_PATH
            );
            InputStream bugemonsStream = getClass().getResourceAsStream(
                JSON_BUGEMON_PATH
            );
        ) {
            if (attacksStream == null || bugemonsStream == null) {
                throw new IOException("JSON files not found in resources: ");
            }
            return Parser.parse(attacksStream, bugemonsStream);
        }
    }
}
