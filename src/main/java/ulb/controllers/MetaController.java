package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.combat.AutomaticCombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.controllers.combat.ManualCombatController;
import ulb.controllers.combat.TowerController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.models.combat.Combat;
import ulb.models.level_up.LevelUp;
import ulb.services.BugemonService;
import ulb.services.PlayerService;

/**
 * Instantiated once at startup; owns every concrete {@link Controller} and is the single authority for screen
 * navigation via {@link #switchTo(Window)}.
 */
public class MetaController {
    private static final Logger LOG = LoggerFactory.getLogger(MetaController.class);

    /**
     * All navigable screens — pass to {@link #switchTo(Window)} to trigger a transition.
     */
    public enum Window {
        MAIN_MENU,
        CREATE_TEAM,
        EDIT_TEAM,
        CREATE_BUGEMON,
        MANUAL_COMBAT,
        AUTOMATIC_COMBAT,
        NOTOWER,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
        LEVEL_UP,
    }

    // TODO: prob not the best place to store this
    private List<LevelUp> pendingLevelUps = new ArrayList<>();

    private final Stage stage;
    private final Map<Window, Runnable> transitions = new EnumMap<>(Window.class);
    private final MainMenuController mainMenuController;
    private final ManageTeamController createTeamController;
    private final ManageTeamController editTeamController;
    private final CreateBugemonController createBugemonController;
    private final AutomaticCombatController automaticCombatController;
    private final ManualCombatController manualCombatController;
    private final TowerController towerController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final LevelUpController levelUpController;
    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;
    private boolean isTowerActive;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage
     *            main JavaFX stage of the application
     * @throws IOException
     *             if the music fails to be initialized
     */
    public MetaController(Stage primaryStage, BugemonService bugemonService, PlayerService playerService)
            throws IOException {
        this.stage = primaryStage;

        this.mainMenuController = new MainMenuController(this, playerService);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                playerService, bugemonService);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this, playerService,
                bugemonService);
        this.createBugemonController = new CreateBugemonController(this, bugemonService);
        this.manualCombatController = new ManualCombatController(this, playerService, bugemonService);
        this.automaticCombatController = new AutomaticCombatController(this, playerService, bugemonService);
        this.towerController = new TowerController(this, playerService, bugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.levelUpController = new LevelUpController(this, playerService);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        this.initializeMusicResources();
        this.initTransitions();
    }

    public void onCombatFinished(List<LevelUp> levelUps, boolean won) {
        LOG.info("onCombatFinished, won: {}, numLevelUps: {}", won, levelUps.size());

        if (this.isTowerActive()) {
            this.towerController.onTowerCombatFinished(won);
            return;
        }

        this.pendingLevelUps = levelUps;
        this.switchTo(won ? Window.COMBAT_VICTORY : Window.COMBAT_DEFEAT);
    }

    public void onCombatVictoryFinished() {
        if (!this.pendingLevelUps.isEmpty()) {
            this.levelUpController.setLevelUps(this.pendingLevelUps);
            this.switchTo(Window.LEVEL_UP);
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    public void onCombatDefeatRetry() {
        // TODO: correct impl
        this.switchTo(Window.MAIN_MENU);
    }

    public void onCombatDefeatReturnToMainMenu() {
        this.switchTo(Window.MAIN_MENU);
    }

    public void onLevelUpfinished() {
        this.switchTo(Window.MAIN_MENU);
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
        this.transitions.put(Window.CREATE_BUGEMON, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.createBugemonController.show(this.stage);
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
            this.automaticCombatController.startAutoRun();
        });
        this.transitions.put(Window.NOTOWER, () -> {
            this.isTowerActive = true;
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.towerController.runTower(this.stage);
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

    public boolean isTowerActive() {
        return this.isTowerActive;
    }

    public void endTowerFlow() {
        this.isTowerActive = false;
    }

    public void startTowerCombat(Combat combat) {
        this.musicPlayer.stopMusic();
        this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
        this.manualCombatController.startCombat(combat);
        this.manualCombatController.show(this.stage);
    }

    public ManualCombatController getCombatController() {
        return this.manualCombatController;
    }

}
