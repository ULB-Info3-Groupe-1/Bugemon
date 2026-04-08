package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javafx.stage.Stage;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.controllers.combat.ManualCombatController;
import ulb.controllers.combat.NOTowerController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.ManageTeamView;

/**
 * Instantiated once at startup; owns every concrete {@link Controller} and is the single authority for screen
 * navigation via {@link #switchTo(Window)}.
 */
public class MetaController {
    /** All navigable screens — pass to {@link #switchTo(Window)} to trigger a transition. */
    public enum Window {
        MAIN_MENU,
        CREATE_TEAM,
        EDIT_TEAM,
        MANUAL_COMBAT,
        AUTOMATIC_COMBAT,
        NOTOWER,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
        LEVEL_UP,
    }

    private final Stage stage;
    private final Map<Window, Runnable> transitions = new EnumMap<>(Window.class);
    private final MainMenuController mainMenuController;
    private final ManageTeamController createTeamController;
    private final ManageTeamController editTeamController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final NOTowerController noTowerController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final LevelUpController levelUpController;
    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;
    private boolean noTowerFlowActive;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage
     *            main JavaFX stage of the application
     * @throws IOException
     *             if a controller or view fails to initialize
     */
    public MetaController(Stage primaryStage, BugemonService bugemonService, PlayerService playerService)
            throws IOException {
        this.stage = primaryStage;

        this.mainMenuController = new MainMenuController(this, playerService);
        this.createTeamController = new ManageTeamController(ManageTeamView.TeamFormMode.CREATE, this, playerService,
                bugemonService);
        this.editTeamController = new ManageTeamController(ManageTeamView.TeamFormMode.EDIT, this, playerService,
                bugemonService);
        this.manualCombatController = new ManualCombatController(this, playerService, bugemonService);
        this.automaticCombatController = new AutomaticCombatController(this, playerService, bugemonService);
        this.noTowerController = new NOTowerController(this, playerService, bugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.levelUpController = new LevelUpController(this, playerService);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        this.initializeMusicResources();
        this.manualCombatController.setOnVictory(this.levelUpController::setLevelUp);
        this.automaticCombatController.setOnVictory(this.levelUpController::setLevelUp);

        this.initTransitions();
    }

    private void initializeMusicResources() throws IOException {
        this.musicLoader.loadAllResources(this.musicPlayer);
    }

    private void initTransitions() {
        this.transitions.put(Window.MAIN_MENU, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.mainMenuController.show(this.stage);
        });
        this.transitions.put(Window.CREATE_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.CREATE_TEAM, false);
            this.createTeamController.show(this.stage);
        });
        this.transitions.put(Window.EDIT_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.CREATE_TEAM, false);
            this.editTeamController.show(this.stage);
        });
        this.transitions.put(Window.MANUAL_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.manualCombatController.startCombat(true);
            this.manualCombatController.show(this.stage);
        });
        this.transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.automaticCombatController.startCombat(true);
            this.automaticCombatController.show(this.stage);
        });
        this.transitions.put(Window.NOTOWER, () -> {
            this.noTowerFlowActive = true;
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.noTowerController.runNOTower(this.stage);
        });
        this.transitions.put(Window.COMBAT_VICTORY, () -> {
            this.combatVictoryController.show(this.stage);
            this.musicPlayer.playAmbiance(Ambiance.VICTORY, true);
        });
        this.transitions.put(Window.COMBAT_DEFEAT, () -> {
            this.combatDefeatController.show(this.stage);
            this.musicPlayer.playAmbiance(Ambiance.DEFEAT, true);
        });
        this.transitions.put(Window.LEVEL_UP, () -> this.levelUpController.show(this.stage));
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window
     *            target screen to display
     * @throws IllegalArgumentException
     *             if the window is invalid
     */
    public final void switchTo(Window window) {
        Runnable transition = this.transitions.get(window);
        if (transition == null) {
            throw new IllegalArgumentException("Unknown window: " + window);
        }
        this.musicPlayer.stopMusic();
        transition.run();
    }

    public boolean isNOTowerFlowActive() {
        return this.noTowerFlowActive;
    }

    public void endNOTowerFlow() {
        this.noTowerFlowActive = false;
    }
}
