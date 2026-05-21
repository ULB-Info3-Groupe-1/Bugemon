package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.bootstrap.ServiceRegistry;
import ulb.controllers.combat.CombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.level_up.LevelUp;
import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.SoundEffect;
import ulb.models.player.PlayerState;
import ulb.models.run.RunTeam;
import ulb.models.team.factory.TeamFactory;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.MusicService;
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
        SAVE_MENU,
        CREATE_TEAM,
        EDIT_TEAM,
        CREATE_BUGEMON,
        MANUAL_COMBAT,
        AUTOMATIC_COMBAT,
        TOWER,
        COMBAT_VICTORY,
        COMBAT_DEFEAT,
        LEVEL_UP,
        SKILL_TREE,
    }

    private final Stage stage;
    private final Map<Window, Runnable> transitions = new EnumMap<>(Window.class);
    private final SaveMenuController saveMenuController;
    private final MainMenuController mainMenuController;
    private final ManageTeamController createTeamController;
    private final ManageTeamController editTeamController;
    private final CreateBugemonController createBugemonController;
    private final CombatController combatController;
    private final CombatVictoryController combatVictoryController;
    private final CombatDefeatController combatDefeatController;
    private final LevelUpController levelUpController;
    private final SkillTreeController skillTreeController;

    private final CombatService combatService;
    private final MusicService musicService;
    private final BugemonService bugemonService;
    private final PlayerState playerState;

    private boolean isTowerActive;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage
     *            main JavaFX stage of the application
     * @throws IOException
     *             if the music fails to be initialized
     */
    public MetaController(Stage primaryStage, ServiceRegistry services, PlayerState playerState) throws IOException {
        this.stage = primaryStage;
        this.combatService = services.combat;
        this.bugemonService = services.bugemon;
        this.musicService = services.music;
        this.playerState = playerState;

        this.saveMenuController = new SaveMenuController(this, services.save, playerState);
        this.mainMenuController = new MainMenuController(this, playerState);
        this.combatController = new CombatController(this, this.combatService, services.skill, playerState);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                services.team, this.bugemonService, playerState);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this, services.team,
                this.bugemonService, playerState);
        this.createBugemonController = new CreateBugemonController(this, this.bugemonService);
        this.levelUpController = new LevelUpController(this, this.bugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.skillTreeController = new SkillTreeController(this, services.skill, playerState);
        this.initTransitions();
    }

    public void start() {
        this.switchTo(Window.SAVE_MENU);
    }

    public void onCombatFinished(boolean won) {
        LOG.info("onCombatFinished, won: {}", won);
        if (this.isTowerActive()) {
            return;
        }

        this.switchTo(won ? Window.COMBAT_VICTORY : Window.COMBAT_DEFEAT);
    }

    public void onCombatVictoryFinished() {
        if (this.levelUpController.hasWorkToDo()) {
            this.switchTo(Window.LEVEL_UP);
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    public void onCombatDefeatRetry() {
        this.onEditTeam();
    }

    public void onCreateTeam() {
        this.switchTo(Window.CREATE_TEAM);
    }

    public void onCreateBugemon() {
        this.switchTo(Window.CREATE_BUGEMON);
    }

    public void onSaveMenu() {
        this.switchTo(Window.SAVE_MENU);
    }

    public void onMainMenu() {
        this.switchTo(Window.MAIN_MENU);
    }

    public void onAllPendingLevelUpsConsumed() {
        if (this.isTowerActive()) {
            this.switchTo(Window.TOWER);
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    public void receiveCombatResults(List<LevelUp> levels) {
        this.levelUpController.addLevelUps(levels);
    }

    public void onStartManualCombat() {
        this.playerState.getActiveTeam().ifPresent(team -> {
            RunTeam playerRunTeam = RunTeam.fromTeam(team);
            List<Bugemon> bugemons = this.bugemonService.getDefaultBugemons();
            TeamFactory opponentFactory = this.combatService.createRandomOpponentFactory();
            CombatFactory combatFactory = this.combatService.createManualCombatFactory(this.playerState.getInventory(),
                    this.combatController, opponentFactory, Configuration.Game.FLOOR_MIN, false);
            this.combatController.startCombat(playerRunTeam, combatFactory, bugemons);
            this.switchTo(Window.MANUAL_COMBAT);
        });
    }

    public void onStartAutomaticCombat() {
        this.playerState.getActiveTeam().ifPresent(team -> {
            RunTeam playerRunTeam = RunTeam.fromTeam(team);
            List<Bugemon> bugemons = this.bugemonService.getDefaultBugemons();
            TeamFactory opponentFactory = this.combatService.createRandomOpponentFactory();
            CombatFactory combatFactory = this.combatService.createAutoCombatFactory(opponentFactory,
                    Configuration.Game.FLOOR_MIN, false);
            this.combatController.startCombat(playerRunTeam, combatFactory, bugemons);
            this.switchTo(Window.AUTOMATIC_COMBAT);
        });
    }

    public void onTower() {
        this.switchTo(Window.TOWER);
    }

    public void onEditTeam() {
        this.switchTo(Window.EDIT_TEAM);
    }

    public void onSkillTree() {
        this.switchTo(Window.SKILL_TREE);
    }

    private void initTransitions() {
        this.transitions.put(Window.MAIN_MENU, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.mainMenuController.show();
        });
        this.transitions.put(Window.SAVE_MENU, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.saveMenuController.show();
        });
        this.transitions.put(Window.CREATE_TEAM, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.createTeamController.show();
        });
        this.transitions.put(Window.EDIT_TEAM, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.editTeamController.show();
        });
        this.transitions.put(Window.CREATE_BUGEMON, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.createBugemonController.show();
        });
        this.transitions.put(Window.MANUAL_COMBAT, () -> {
            this.musicService.playBackground(BackgroundAmbiance.COMBAT);
            this.combatController.show();
        });
        // TODO: check to have automatic combat
        this.transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            this.musicService.playBackground(BackgroundAmbiance.COMBAT);
            this.combatController.show();
        });
        // TODO: add tower transitions
        this.transitions.put(Window.COMBAT_VICTORY, () -> {
            this.combatVictoryController.show();
            this.musicService.playSoundEffect(SoundEffect.VICTORY);
        });
        this.transitions.put(Window.COMBAT_DEFEAT, () -> {
            this.combatDefeatController.show();
            this.musicService.playSoundEffect(SoundEffect.DEFEAT);
        });
        this.transitions.put(Window.LEVEL_UP, this.levelUpController::show);
        this.transitions.put(Window.SKILL_TREE, this.skillTreeController::show);
    }

    /**
     * Switches the current screen to the specified window.
     *
     * @param window
     *            target screen to display
     * @throws IllegalArgumentException
     *             if the window is invalid
     */
    private void switchTo(Window window) {
        Runnable transition = this.transitions.get(window);
        if (transition == null) {
            throw new IllegalArgumentException("Unknown window: " + window);
        }
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
        this.musicService.stopMusic();
        this.musicService.playBackground(BackgroundAmbiance.COMBAT);

        this.combatController.initialize(combat);
        this.combatController.show();
    }
}
