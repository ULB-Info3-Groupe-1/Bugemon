package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
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
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.PlayerService;
import ulb.views.View;

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

    private final BugemonService bugemonService;

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
    public MetaController(Stage primaryStage, BugemonService bugemonService, PlayerService playerService,
            CombatService combatService) throws IOException {
        this.bugemonService = bugemonService;

        this.stage = primaryStage;

        this.mainMenuController = new MainMenuController(this, playerService);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                playerService, bugemonService);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this, playerService,
                bugemonService);
        this.createBugemonController = new CreateBugemonController(this, bugemonService);
        this.manualCombatController = new ManualCombatController(this, playerService, bugemonService, combatService);
        this.automaticCombatController = new AutomaticCombatController(this, playerService, bugemonService,
                combatService);
        this.levelUpController = new LevelUpController(this, bugemonService);
        this.towerController = new TowerController(this, playerService, bugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        this.initializeMusicResources();
        this.initTransitions();
    }

    public void onCombatFinished(boolean won) {
        LOG.info("onCombatFinished, won: {}", won);
        if (this.isTowerActive()) {
            this.towerController.onTowerCombatFinished(won);
            return;
        }

        this.switchTo(won ? Window.COMBAT_VICTORY : Window.COMBAT_DEFEAT);
    }

    public void onCombatVictoryFinished() {
        if (this.bugemonService.hasPendingLevelUps()) {
            this.switchTo(Window.LEVEL_UP);
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    public void onCombatDefeatRetry() {
        this.switchTo(Window.CREATE_TEAM);
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
            this.mainMenuController.show();
        });
        this.transitions.put(Window.CREATE_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.CREATE_TEAM, false);
            this.createTeamController.show();
        });
        this.transitions.put(Window.EDIT_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.CREATE_TEAM, false);
            this.editTeamController.show();
        });
        this.transitions.put(Window.CREATE_BUGEMON, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.createBugemonController.show();
        });
        this.transitions.put(Window.MANUAL_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.manualCombatController.startCombat(true);
            this.manualCombatController.show();
        });
        this.transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.automaticCombatController.startCombat(true);
            this.automaticCombatController.show();
            this.automaticCombatController.startAutoRun();
        });
        this.transitions.put(Window.NOTOWER, () -> {
            this.isTowerActive = true;
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.towerController.runTower();
        });
        this.transitions.put(Window.COMBAT_VICTORY, () -> {
            this.combatVictoryController.show();
            this.musicPlayer.playAmbiance(Ambiance.VICTORY, true);
        });
        this.transitions.put(Window.COMBAT_DEFEAT, () -> {
            this.combatDefeatController.show();
            this.musicPlayer.playAmbiance(Ambiance.DEFEAT, true);
        });
        this.transitions.put(Window.LEVEL_UP, this.levelUpController::show);
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

    void showView(View view) {
        view.show(this.stage);
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
        this.manualCombatController.show();
    }
}
