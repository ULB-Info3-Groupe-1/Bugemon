package ulb.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import ulb.controllers.music.MusicPlayer;
import ulb.services.LevelUpService;
import ulb.services.PlayerService;

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
        MAIN_MENU("/fxml/MainMenu.fxml"),
        CREATE_TEAM("/fxml/CreateTeam.fxml"),
        MANUAL_COMBAT("/fxml/ManualCombat.fxml"),
        AUTOMATIC_COMBAT("/fxml/AutomaticCombat.fxml"),
        COMBAT_VICTORY("/fxml/CombatVictory.fxml"),
        COMBAT_DEFEAT("/fxml/CombatDefeat.fxml"),
        LEVEL_UP("/fxml/LevelUp.fxml");

        private final String fxmlPath;

        Window(String fxmlPath) {
            this.fxmlPath = fxmlPath;
        }

        public String getFxmlPath() {
            return fxmlPath;
        }
    }

    private final Stage stage;
    private final MusicPlayer musicPlayer;
    private final LevelUpService levelUpService;
    private final PlayerService playerService;
    private final ControllerFactory controllerFactory;
    private final Map<Window, Controller> controllers;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage main JavaFX stage of the application
     * @throws IOException if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.musicPlayer = new MusicPlayer();
        this.controllers = new HashMap<>();
        this.levelUpService = new LevelUpService();
        this.playerService = new PlayerService();
        this.controllerFactory = new ControllerFactory(this, playerService, levelUpService);
        initializeControllers();
    }

    /**
     * Initializes all controllers for the application screens.
     *
     * @throws IOException if a controller or view fails to initialize
     */
    private void initializeControllers() throws IOException {
        for (Window window : Window.values()) {
            loadScreen(window);
        }
    }

    /**
     * Loads a screen and its controller.
     *
     * @param window the window to load
     * @return the controller for the loaded screen
     * @throws IOException if the screen fails to load
     */
    private Controller loadScreen(Window window) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(window.getFxmlPath()));
        Pane root = loader.load();
        Controller controller = loader.getController();
        controller.setMetaController(this);
        controller.initScene(root);
        controllers.put(window, controller);
        return controller;
    }

    /**
     * Switches to the specified window.
     *
     * @param window the window to switch to
     */
    public void switchTo(Window window) {
        Controller controller = controllers.get(window);
        if (controller != null) {
            controller.show(stage);
        } else {
            System.err.println("Controller for window " + window + " not found.");
        }
    }

    /**
     * Gets the controller for the specified window.
     *
     * @param window the window whose controller is to be retrieved
     * @return the controller for the specified window
     */
    public Controller getController(Window window) {
        return controllers.get(window);
    }

    /**
     * Gets the music player.
     *
     * @return the music player
     */
    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}
