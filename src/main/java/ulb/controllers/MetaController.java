package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.bootstrap.GameBootstrapper;
import ulb.bootstrap.ServiceRegistry;
import ulb.common.CombatSummary;
import ulb.common.LevelUpResult;
import ulb.controllers.combat.CombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.controllers.menu.LoginController;
import ulb.controllers.menu.MainMenuController;
import ulb.controllers.menu.SaveMenuController;
import ulb.controllers.menu.SettingsController;
import ulb.controllers.team.CreateBugemonController;
import ulb.controllers.team.ManageTeamController;
import ulb.controllers.tower.LevelUpController;
import ulb.controllers.tower.RewardController;
import ulb.controllers.tower.SkillTreeController;
import ulb.controllers.tower.TowerController;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.music.BackgroundAmbiance;
import ulb.models.music.SoundEffect;
import ulb.models.player.PlayerState;
import ulb.models.run.RunTeam;
import ulb.models.team.factory.TeamFactory;
import ulb.models.tower.reward.Reward;
import ulb.repositories.resource.ResourceMusicRepository;
import ulb.services.game.BugemonService;
import ulb.services.game.CombatService;
import ulb.services.game.RewardService;
import ulb.services.session.LoginService;
import ulb.services.session.SessionService;
import ulb.services.system.MusicService;
import ulb.services.system.SettingsService;
import ulb.views.View;

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

    private final GameBootstrapper bootstrapper;
    private final Stage stage;

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
    private final SettingsService settingsService;
    private final SettingsController settingsController;
    private final SessionService sessionService;

    private BugemonService bugemonService;
    private RewardService rewardService;

    private PlayerState playerState;

    private CombatSummary lastCombatSummary;
    private boolean isTowerActive;

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @throws IOException
     *             if the music fails to be initialized
     */
    public MetaController(Stage stage) {
        this.stage = stage;
        this.bootstrapper = new GameBootstrapper();
        this.settingsService = new SettingsService();
        this.musicService = new MusicService(new ResourceMusicRepository());
        this.musicService.setVolume(this.settingsService.getVolume());
        this.combatService = new CombatService(this.bootstrapper.getRandom());
        this.settingsController = new SettingsController(this.musicService, this.settingsService, this.stage);
        this.sessionService = new SessionService();
    }

    /**
     * Opens the settings dialog (audio volume + display mode) as a modal overlay above the current screen.
     */
    public void openSettings() {
        this.settingsController.open();
    }

    /**
     * Begins the application flow. If a player is remembered from a previous session, the login screen is skipped and
     * the save menu is shown directly; otherwise the login screen is displayed.
     */
    public void start() {
        Optional<String> rememberedPlayer = this.sessionService.getLoggedInPlayer();
        if (rememberedPlayer.isPresent()) {
            this.autoLogin(rememberedPlayer.get());
        } else {
            this.showLogin();
        }
        // Applied after the stage is shown: setting fullscreen before show() is unreliable on Windows.
        this.stage.setFullScreen(this.settingsService.isFullScreen());
    }

    /** Creates and shows the login screen. */
    private void showLogin() {
        LoginController loginController = new LoginController(this, new LoginService(
                this.bootstrapper.getRepositories().playerRepository, this.bootstrapper.getDefaultInventory()));
        this.musicService.playBackground(BackgroundAmbiance.MENU);
        loginController.show();
    }

    /** Restores a remembered session straight to the save menu; falls back to the login screen on any failure. */
    private void autoLogin(String playerName) {
        try {
            this.musicService.playBackground(BackgroundAmbiance.MENU);
            this.initGame(playerName);
            this.switchTo(Window.SAVE_MENU);
        } catch (RuntimeException e) {
            LOG.warn("Auto-login failed for '{}', returning to login: {}", playerName, e.toString());
            this.sessionService.clearLoggedInPlayer();
            this.showLogin();
        }
    }

    public void onLogged(String playerName) {
        this.sessionService.setLoggedInPlayer(playerName);
        this.initGame(playerName);
        this.switchTo(Window.SAVE_MENU);
    }

    public void onAccountCreated(String playerName) {
        this.sessionService.setLoggedInPlayer(playerName);
        this.initGame(playerName);
        this.switchTo(Window.MAIN_MENU);
    }

    /** Logs the current player out: forgets the remembered session and returns to the login screen. */
    public void onLogout() {
        this.sessionService.clearLoggedInPlayer();
        this.showLogin();
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

    /**
     * Called when the player finishes choosing rewards; returns control to the tower floor screen.
     */
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
     *            the summary produced by {@link ulb.services.game.CombatService#finalizeCombat}
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

    /**
     * Routes to the team-edit screen after a defeat, allowing the player to adjust their team.
     */
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

    /**
     * Marks the tower flow as inactive without navigating away from the current screen.
     */
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

    /**
     * Delegates to {@link View#show(javafx.stage.Stage)} to display the given view on the primary stage.
     *
     * @param view
     *            the view to display
     */
    void showView(View view) {
        view.show(this.stage);
    }
}
