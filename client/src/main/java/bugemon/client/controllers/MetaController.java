package bugemon.client.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.controllers.combat.CombatController;
import bugemon.client.controllers.combat.CombatDefeatController;
import bugemon.client.controllers.combat.CombatVictoryController;
import bugemon.client.net.NetworkManager;
import bugemon.client.repositories.resource.ResourceMusicRepository;
import bugemon.client.services.MusicService;
import bugemon.client.services.RemoteBugemonService;
import bugemon.client.services.RemoteInventoryService;
import bugemon.client.services.RemoteSaveService;
import bugemon.client.services.RemoteSkillService;
import bugemon.client.services.RemoteStaticDataService;
import bugemon.client.services.RemoteTeamService;
import bugemon.client.services.RemoteTowerService;
import bugemon.client.views.View;
import bugemon.common.CombatSummary;
import bugemon.common.Configuration;
import bugemon.common.LevelUpResult;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.combat.CombatService;
import bugemon.common.models.combat.factory.CombatFactory;
import bugemon.common.models.music.BackgroundAmbiance;
import bugemon.common.models.music.SoundEffect;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.run.RunTeam;
import bugemon.common.models.team.factory.TeamFactory;
import bugemon.common.models.tower.reward.Reward;
import bugemon.common.models.tower.reward.RewardGenerator;
import bugemon.common.net.PlayerSnapshotPacket;
import bugemon.common.net.RequestPlayerDataPacket;
import bugemon.common.net.StaticDataPacket;

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

    private final Random random = new Random();
    private final CombatService combatService;
    private final MusicService musicService;
    private final NetworkManager network;
    private final RemoteSaveService remoteSaveService;
    private final RemoteSkillService remoteSkillService;
    private final RemoteTeamService remoteTeamService;
    private final RemoteBugemonService remoteBugemonService;
    private final RemoteTowerService remoteTowerService;
    private final RemoteInventoryService remoteInventoryService;
    private final RemoteStaticDataService remoteStaticDataService;

    private PlayerState playerState;
    private StaticDataPacket staticData;

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
        this.musicService = new MusicService(new ResourceMusicRepository());
        this.combatService = new CombatService(this.random);
        this.network = NetworkManager.getInstance();
        this.remoteSaveService = new RemoteSaveService(this.network);
        this.remoteSkillService = new RemoteSkillService(this.network);
        this.remoteTeamService = new RemoteTeamService(this.network);
        this.remoteBugemonService = new RemoteBugemonService(this.network);
        this.remoteTowerService = new RemoteTowerService(this.network);
        this.remoteInventoryService = new RemoteInventoryService(this.network);
        this.remoteStaticDataService = new RemoteStaticDataService(this.network);
    }

    /** Navigates to the login-menu screen to begin the application flow. */
    public void start() {
        LoginController loginController = new LoginController(this, this.network);
        this.musicService.playBackground(BackgroundAmbiance.MENU);
        loginController.show();
    }

    public void onLogged(String playerName) {
        this.initGame(playerName, () -> this.switchTo(Window.SAVE_MENU));
    }

    public void onAccountCreated(String playerName) {
        this.initGame(playerName, () -> this.switchTo(Window.MAIN_MENU));
    }

    /**
     * Requests the player's initial state snapshot from the server and, once it arrives, builds the session and runs
     * {@code onReady}. The network round-trip happens off the JavaFX thread; session assembly and navigation are
     * marshalled back onto it via {@link Platform#runLater}.
     *
     * @param playerName
     *            the authenticated player
     * @param onReady
     *            navigation to perform once the session is built
     */
    private void initGame(String playerName, Runnable onReady) {
        this.network.sendAsync(new RequestPlayerDataPacket(), PlayerSnapshotPacket.class)
                .thenCombine(this.remoteStaticDataService.getStaticData(), Map::entry)
                .whenComplete((entry, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        LOG.error("Failed to load session data for {}", playerName, error);
                        return;
                    }
                    this.staticData = entry.getValue();
                    this.buildSession(playerName, entry.getKey());
                    onReady.run();
                }));
    }

    private void buildSession(String playerName, PlayerSnapshotPacket snapshot) {
        this.playerState = new PlayerState(playerName, snapshot.activeTeam(), snapshot.inventory(),
                snapshot.skillTreeState());

        this.saveMenuController = new SaveMenuController(this);
        this.mainMenuController = new MainMenuController(this, this.playerState);
        this.combatController = new CombatController(this, this.combatService, this.remoteSaveService,
                this.staticData.skillTree(), this.playerState);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                this.remoteTeamService, this.playerState);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this,
                this.remoteTeamService, this.playerState);
        this.createBugemonController = new CreateBugemonController(this, this.remoteBugemonService);
        this.skillTreeController = new SkillTreeController(this, this.remoteSkillService, this.playerState);
        this.levelUpController = new LevelUpController(this, this.remoteBugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.towerController = new TowerController(this, this.playerState, this.remoteTowerService,
                this.remoteSaveService);
        this.rewardController = new RewardController(this, this.remoteInventoryService, this.remoteBugemonService);
        this.initTransitions();
    }

    /**
     * Generates rewards for the cleared tower room and navigates to the reward screen.
     *
     * @param runTeam
     *            the player's current run team, used to tailor reward generation
     */
    public void startRewardFlow(RunTeam runTeam) {
        List<Reward> rewards = new RewardGenerator(this.random).generate(this.staticData.attacks(),
                this.staticData.items(), runTeam);
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

    /**
     * Starts a brand-new game: asks the server to wipe the player's progression, then rebuilds the session from the
     * fresh snapshot and navigates to the main menu. The reset round-trip runs off the JavaFX thread; session rebuild
     * and navigation are marshalled back onto it via {@link Platform#runLater}.
     */
    public void onNewGame() {
        String playerName = this.playerState.getPlayerName();
        this.remoteSaveService.resetGame().whenComplete((snapshot, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to reset game for {}", playerName, error);
                return;
            }
            this.buildSession(playerName, snapshot);
            this.switchTo(Window.MAIN_MENU);
        }));
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
            List<Bugemon> bugemons = this.staticData.bugemons();
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
            List<Bugemon> bugemons = this.staticData.bugemons();
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
        this.towerController.startRun(() -> this.switchTo(Window.TOWER));
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
        List<Bugemon> bugemons = this.staticData.bugemons();
        TeamFactory opponentFactory = this.combatService.createOpponentFactory(isBoss);
        CombatFactory combatFactory = this.combatService.createManualCombatFactory(this.playerState.getInventory(),
                this.combatController, opponentFactory, floor, isBoss);
        this.combatController.startCombat(runTeam, combatFactory, bugemons);
        this.switchTo(Window.MANUAL_COMBAT);
    }
}
