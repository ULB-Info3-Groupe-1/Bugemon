package ulb.controllers;

import java.io.IOException;

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
        COMBAT_RESULT,
    }

    private final String JSON_FILES_PATH = "resources/Assets/json";

    private final Stage stage;
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final CombatController combatController;
    private final CombatResultController combatResultController;
    private final Parser.ParseResult parseResult;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.parseResult = Parser.parse(JSON_FILES_PATH);

        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this);
        this.combatController = new CombatController(this, parseResult.getBugemonsList());
        this.combatResultController = new CombatResultController(this);
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
                this.combatController.show(this.stage);
                this.combatController.runCombat(createTeamController.getFinalTeam());
            }
            case COMBAT_RESULT -> {
                this.combatResultController.show(this.stage);
            }
            default ->
                throw new IllegalArgumentException("Invalid window");
        }
    }

}
