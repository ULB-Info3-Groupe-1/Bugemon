package ulb.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.ManualCombatController;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
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
        MANUAL_COMBAT,
        AUTOMATIC_COMBAT,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
        LEVEL_UP,
    }

    private static final String JSON_ATTACK_PATH = "/json/attaques.json";
    private static final String JSON_BUGEMON_PATH = "/json/bugemons.json";
    private static final String JSON_OBJECTS_PATH = "/json/objets.json";

    private final Stage stage;
    private final Parser.ParseResult parseResult;
    private final MainMenuController mainMenuController;
    private final CreateTeamController createTeamController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final BugemonTeam playerTeam;
    private final LevelUpController levelUpController;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.parseResult = loadResources();

        this.playerTeam = new BugemonTeam();
        this.mainMenuController = new MainMenuController(this);
        this.createTeamController = new CreateTeamController(this, playerTeam);
        this.manualCombatController = new ManualCombatController(this);
        this.automaticCombatController = new AutomaticCombatController(this);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.levelUpController = new LevelUpController(this);
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window target screen to display
     * @throws IllegalArgumentException if the window is invalid
     */
    public final void switchTo(Window window) {
        switch (window) {
            case MAIN_MENU -> this.mainMenuController.show(this.stage);
            case CREATE_TEAM -> this.createTeamController.show(this.stage);
            case AUTOMATIC_COMBAT -> this.automaticCombatController.show(stage);
            case MANUAL_COMBAT -> this.manualCombatController.show(this.stage);
            case COMBAT_VICTORY -> this.combatVictoryController.show(this.stage);
            case COMBAT_DEFEAT -> this.combatDefeatController.show(this.stage);
            case LEVEL_UP -> this.levelUpController.show(this.stage);
            default -> throw new IllegalArgumentException("Invalid window");
        }
    }

    /**
     * Loads and parses the game data from JSON resource files.
     * @return a Parser.ParseResult containing the maps of attacks and the list of Bugemons
     * @throws IOException if the JSON directory is missing or if an error occurs during path
     *         conversion or file reading
     */
    private Parser.ParseResult loadResources() throws IOException {
        try (InputStream attacksStream = getClass().getResourceAsStream(JSON_ATTACK_PATH);
             InputStream bugemonsStream = getClass().getResourceAsStream(JSON_BUGEMON_PATH);
             InputStream objectsStream = getClass().getResourceAsStream(JSON_OBJECTS_PATH)) {
            if (attacksStream == null || bugemonsStream == null || objectsStream == null) {
                throw new IOException("JSON files not found in resources: ");
            }
            return Parser.parse(attacksStream, bugemonsStream, objectsStream);
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
        if (this.playerTeam.isEmpty()) {
            showAlert("Équipe incomplète",
                      "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
        } else {
            switchTo(Window.AUTOMATIC_COMBAT);
            this.automaticCombatController.runAutoCombat(new AutoTrainer(this.playerTeam));
        }
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
        if (this.playerTeam.isEmpty()) {
            showAlert("Équipe incomplète",
                      "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
        } else {
            switchTo(Window.MANUAL_COMBAT);
            this.manualCombatController.runManualCombat(new ManualTrainer(this.playerTeam));
        }
    }

    /**
     * Retrieves the complete list of all available Bugemons in the game
     *
     * @return a List containing all Bugemon objects loaded from the game resources
     */
    public final List<Bugemon> getAllBugemonsAvailable() {
        return this.parseResult.getBugemonsList();
    }

    /**
     * Resets the bugemon team of the trainer.
     */
    public void resetTeam() {
        this.playerTeam.reset();
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * @param title the title of the alert dialog
     * @param message the content message of the alert dialog
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setLevelUp(List<LevelUp> levelUps) {
        this.levelUpController.setLevelUp(levelUps);
    }

    public Inventory getInventory() { return this.parseResult.getInventory(); };
}

