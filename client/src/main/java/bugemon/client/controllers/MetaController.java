package bugemon.client.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.controllers.combat.CombatController;
import bugemon.client.controllers.combat.CombatDefeatController;
import bugemon.client.controllers.combat.CombatVictoryController;
import bugemon.client.repositories.resource.ResourceMusicRepository;
import bugemon.client.services.MusicService;
import bugemon.client.views.View;
import bugemon.common.CombatSummary;
import bugemon.common.Configuration;
import bugemon.common.LevelUpResult;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.combat.factory.CombatFactory;
import bugemon.common.models.music.BackgroundAmbiance;
import bugemon.common.models.music.SoundEffect;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.run.RunTeam;
import bugemon.common.models.team.factory.TeamFactory;
import bugemon.common.models.tower.reward.Reward;
import bugemon.server.bootstrap.GameBootstrapper;
import bugemon.server.bootstrap.ServiceRegistry;
import bugemon.server.services.BugemonService;
import bugemon.server.services.CombatService;
import bugemon.server.services.LoginService;
import bugemon.server.services.RewardService;

/**
 * Instantiated once at startup; owns every concrete {@link Controller} and is the single authority for screen
 * navigation via {@link #switchTo(Window)}.
 */
public class MetaController {
    private static final Logger LOG = LoggerFactory.getLogger(MetaController.class);

    /**
     * All navigable screens. Pass to {@link #switchTo(Window)} to trigger a transition.
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
        REWARD,
    }

    private final Stage stage;
    private final GameBootstrapper bootstrapper;

    private Map<Window, Runnable> transitions = new EnumMap<>(Window.class);

    private SaveMenuController saveMenuController;
    private MainMenuController mainMenuController;
    private ManageTeamController createTeamController;
    private ManageTeamController editTeamController;
    private CreateBugemonController createBugemonController;
    private SkillTreeController skillTreeController;
    private CombatController combatController;
    private CombatVictoryController combatVictoryController;
    private CombatDefeatController combatDefeatController;
    private LevelUpController levelUpController;
    private TowerController towerController;
    private RewardController rewardController;

    private final CombatService combatService;
    private final MusicService musicService;

    private BugemonService bugemonService;
    private RewardService rewardService;

    private PlayerState playerState;

    private CombatSummary lastCombatSummary;
    private boolean isTowerActive;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage
     *            main JavaFX stage of the application
     * @throws IOException
     *             if the music fails to be initialized
     */
    public MetaController(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.bootstrapper = new GameBootstrapper();
        this.musicService = new MusicService(new ResourceMusicRepository());
        this.combatService = new CombatService(this.bootstrapper.getRandom());
    }

    /** Navigates to the login-menu screen to begin the application flow. */
    public void start() {
        LoginController loginController = new LoginController(this, new LoginService(
                this.bootstrapper.getRepositories().playerRepository, this.bootstrapper.getDefaultInventory()));
        this.musicService.playBackground(BackgroundAmbiance.MENU);
        loginController.show();
    }

    public void onLogged(String playerName) {
        this.initGame(playerName);
        this.switchTo(Window.SAVE_MENU);

    }

    public void onAccountCreated(String playerName) {
        this.initGame(playerName);
        this.switchTo(Window.MAIN_MENU);
    }

    private void initGame(String playerName) {
        ServiceRegistry services = this.bootstrapper.createServices(playerName);

        this.bugemonService = services.bugemon;
        this.rewardService = services.reward;
        this.playerState = new PlayerState(playerName, services.team.getActiveTeam().orElse(null),
                services.inventory.getInventory(), services.skill.getSkillTreeState());

        this.saveMenuController = new SaveMenuController(this, services.save, this.playerState);
        this.mainMenuController = new MainMenuController(this, this.playerState);
        this.combatController = new CombatController(this, this.combatService, services.skill, services.save,
                this.playerState);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                services.team, this.bugemonService, this.playerState);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this, services.team,
                this.bugemonService, this.playerState);
        this.createBugemonController = new CreateBugemonController(this, this.bugemonService);
        this.skillTreeController = new SkillTreeController(this, services.skill, this.playerState);
        this.levelUpController = new LevelUpController(this, services.levelUp);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.towerController = new TowerController(this, this.playerState, services.tower, services.team,
                services.skill, services.inventory, services.save);
        this.rewardController = new RewardController(this, this.rewardService);
        this.initTransitions();
    }

    /**
     * Generates rewards for the cleared tower room and navigates to the reward screen.
     *
     * @param runTeam
     *            the player's current run team, used to tailor reward generation
     */
    public void startRewardFlow(RunTeam runTeam) {
        List<Reward> rewards = this.rewardService.generateRewards(runTeam);
        this.rewardController.initialize(rewards, runTeam, this.playerState.getInventory());
        this.switchTo(Window.REWARD);
    }

    /** Called when the player finishes choosing rewards; returns control to the tower floor screen. */
    public void onRewardFlowFinished() {
        this.towerController.onBonusRoomExited();
    }

    /**
     * Routes to the appropriate post-combat screen for a standalone (non-tower) combat.
     *
     * @param won
     *            {@code true} if the player won
     */
    public void onCombatFinished(boolean won) {
        this.switchTo(won ? Window.COMBAT_VICTORY : Window.COMBAT_DEFEAT);
    }

    /**
     * Routes to the appropriate post-combat screen after a combat that produced a {@link CombatSummary}.
     *
     * <p>
     * During a tower run, a defeat clears the active tower and goes to the defeat screen; a victory may trigger floor
     * advancement and then XP / level-up processing. Outside a tower run, behaviour is the same as
     * {@link #onCombatFinished(boolean)}.
     *
     * @param won
     *            {@code true} if the player won
     * @param summary
     *            the summary produced by {@link bugemon.server.services.CombatService#finalizeCombat}
     */
    public void onCombatFinished(boolean won, CombatSummary summary) {
        LOG.info("onCombatFinished, won: {}", won);
        this.lastCombatSummary = summary;

        if (this.isTowerActive) {
            this.towerController.onTowerCombatFinished(won);
            if (!won) {
                this.isTowerActive = false;
                this.switchTo(Window.COMBAT_DEFEAT);
                return;
            }
            if (!this.towerController.isRunActive()) {
                this.isTowerActive = false;
            }
            this.receiveCombatSummary(summary);
            return;
        }

        this.switchTo(won ? Window.COMBAT_VICTORY : Window.COMBAT_DEFEAT);
    }

    /**
     * Called when the player dismisses the victory screen.
     *
     * <p>
     * Processes any pending {@link CombatSummary} (level-ups, etc.) and then hands control back to the main menu or
     * tower floor screen.
     */
    public void onCombatVictoryFinished() {
        if (this.lastCombatSummary != null) {
            this.receiveCombatSummary(this.lastCombatSummary);
            this.lastCombatSummary = null;
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    private void receiveCombatSummary(CombatSummary combatSummary) {
        List<LevelUpResult> levelUps = combatSummary.levelUpResults();
        if (levelUps != null && !levelUps.isEmpty()) {
            this.levelUpController.initialize(levelUps);
            this.switchTo(Window.LEVEL_UP);
        } else {
            this.onAllPendingLevelUpsConsumed();
        }
    }

    /**
     * Called by {@link LevelUpController} when all queued level-ups have been processed.
     *
     * <p>
     * Returns to the tower floor screen if a run is active, otherwise to the main menu.
     */
    public void onAllPendingLevelUpsConsumed() {
        if (this.isTowerActive) {
            this.switchTo(Window.TOWER);
        } else {
            this.switchTo(Window.MAIN_MENU);
        }
    }

    /** Routes to the team-edit screen after a defeat, allowing the player to adjust their team. */
    public void onCombatDefeatRetry() {
        this.onEditTeam();
    }

    /** Navigates to the team-creation screen. */
    public void onCreateTeam() {
        this.switchTo(Window.CREATE_TEAM);
    }

    /** Navigates to the custom-Bugemon creation screen. */
    public void onCreateBugemon() {
        this.switchTo(Window.CREATE_BUGEMON);
    }

    /** Navigates to the save/load menu. */
    public void onSaveMenu() {
        this.switchTo(Window.SAVE_MENU);
    }

    /** Navigates to the main menu. */
    public void onMainMenu() {
        this.switchTo(Window.MAIN_MENU);
    }

    /**
     * Starts a player-controlled (manual) combat session using the active team and navigates to the combat screen. Does
     * nothing if no active team is set.
     */
    public void onStartManualCombat() {
        this.playerState.getActiveTeam().ifPresent(team -> {
            RunTeam playerRunTeam = RunTeam.fromTeam(team);
            List<Bugemon> bugemons = this.bugemonService.getDefaultBugemons();
            TeamFactory opponentFactory = this.combatService.createOpponentFactory(false);
            CombatFactory combatFactory = this.combatService.createManualCombatFactory(this.playerState.getInventory(),
                    this.combatController, opponentFactory, Configuration.Game.FLOOR_MIN, false);
            this.combatController.startCombat(playerRunTeam, combatFactory, bugemons);
            this.switchTo(Window.MANUAL_COMBAT);
        });
    }

    /**
     * Starts an AI-driven (automatic) combat session using the active team and navigates to the combat screen. Does
     * nothing if no active team is set.
     */
    public void onStartAutomaticCombat() {
        this.playerState.getActiveTeam().ifPresent(team -> {
            RunTeam playerRunTeam = RunTeam.fromTeam(team);
            List<Bugemon> bugemons = this.bugemonService.getDefaultBugemons();
            TeamFactory opponentFactory = this.combatService.createOpponentFactory(false);
            CombatFactory combatFactory = this.combatService.createAutoCombatFactory(opponentFactory,
                    Configuration.Game.FLOOR_MIN, false);
            this.combatController.startCombat(playerRunTeam, combatFactory, bugemons);
            this.switchTo(Window.AUTOMATIC_COMBAT);
        });
    }

    /**
     * Starts or resumes a tower run for the active team and navigates to the tower floor screen. Does nothing if no
     * active team is set.
     */
    public void onTower() {
        this.isTowerActive = true;
        this.towerController.startRun();
        this.switchTo(Window.TOWER);
    }

    /** Marks the tower flow as inactive without navigating away from the current screen. */
    public void endTowerFlow() {
        this.isTowerActive = false;
    }

    /** Navigates to the team-editing screen. */
    public void onEditTeam() {
        this.switchTo(Window.EDIT_TEAM);
    }

    /** Navigates to the skill-tree screen. */
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
        this.transitions.put(Window.SKILL_TREE, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.skillTreeController.show();
        });
        this.transitions.put(Window.MANUAL_COMBAT, () -> {
            this.musicService.playBackground(BackgroundAmbiance.COMBAT);
            this.combatController.show();
        });
        this.transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            this.musicService.playBackground(BackgroundAmbiance.COMBAT);
            this.combatController.show();
        });
        this.transitions.put(Window.TOWER, () -> {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.towerController.show();
        });
        this.transitions.put(Window.COMBAT_VICTORY, () -> {
            this.combatVictoryController.show();
            this.musicService.playSoundEffect(SoundEffect.VICTORY);
        });
        this.transitions.put(Window.COMBAT_DEFEAT, () -> {
            this.combatDefeatController.show();
            this.musicService.playSoundEffect(SoundEffect.DEFEAT);
        });
        this.transitions.put(Window.LEVEL_UP, this.levelUpController::show);
        this.transitions.put(Window.REWARD, this.rewardController::show);
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

    /**
     * Delegates to {@link View#show(javafx.stage.Stage)} to display the given view on the primary stage.
     *
     * @param view
     *            the view to display
     */
    void showView(View view) {
        view.show(this.stage);
    }

    /**
     * Starts a tower-combat session for the given run team and navigates to the manual-combat screen.
     *
     * @param runTeam
     *            the player's current run team
     * @param floor
     *            the floor number, used for XP and difficulty scaling
     * @param isBoss
     *            {@code true} to generate a boss opponent, {@code false} for a regular combat
     */
    public void onStartTowerCombat(RunTeam runTeam, int floor, boolean isBoss) {
        List<Bugemon> bugemons = this.bugemonService.getDefaultBugemons();
        TeamFactory opponentFactory = this.combatService.createOpponentFactory(isBoss);
        CombatFactory combatFactory = this.combatService.createManualCombatFactory(this.playerState.getInventory(),
                this.combatController, opponentFactory, floor, isBoss);
        this.combatController.startCombat(runTeam, combatFactory, bugemons);
        this.switchTo(Window.MANUAL_COMBAT);
    }
}
