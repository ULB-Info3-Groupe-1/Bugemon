package ulb.controllers;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.controllers.combat.CombatController;
import ulb.controllers.combat.CombatDefeatController;
import ulb.controllers.combat.CombatVictoryController;
import ulb.controllers.music.Ambiance;
import ulb.controllers.music.MusicLoader;
import ulb.controllers.music.MusicPlayer;
import ulb.models.combat.Combat;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.level_up.LevelUp;
import ulb.models.player.PlayerState;
import ulb.models.run.RunTeam;
import ulb.models.team.factory.TeamFactory;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.SkillService;
import ulb.services.TeamService;
import ulb.services.TowerService;
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

    private final BugemonService bugemonService;
    private final TeamService teamService;
    private final CombatService combatService;
    private final InventoryService inventoryService;
    private final SkillService skillService;

    private final PlayerState playerState;

    private final MusicPlayer musicPlayer;
    private final MusicLoader musicLoader;
    private boolean isTowerActive;

    private final Random random = new Random();

    /**
     * Creates the meta-controller and initializes all screen controllers.
     *
     * @param primaryStage
     *            main JavaFX stage of the application
     * @throws IOException
     *             if the music fails to be initialized
     */
    public MetaController(Stage primaryStage, BugemonService bugemonService, TeamService teamService,
            TowerService towerService, InventoryService inventoryService, SkillService skillService,
            PlayerState playerState) throws IOException {
        this.stage = primaryStage;
        this.bugemonService = bugemonService;
        this.teamService = teamService;
        this.inventoryService = inventoryService;
        this.skillService = skillService;

        this.playerState = playerState;

        this.combatService = new CombatService(new DamageCalculator(), new EffectProcessor(), this.random);

        this.saveMenuController = new SaveMenuController(this, bugemonService, teamService, towerService,
                inventoryService);
        this.mainMenuController = new MainMenuController(this, teamService);
        this.combatController = new CombatController(this, this.combatService);
        this.createTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.CREATE, this,
                teamService, bugemonService);
        this.editTeamController = new ManageTeamController(ManageTeamController.TeamFormMode.EDIT, this, teamService,
                bugemonService);
        this.createBugemonController = new CreateBugemonController(this, bugemonService);
        this.levelUpController = new LevelUpController(this, bugemonService);
        this.combatVictoryController = new CombatVictoryController(this);
        this.combatDefeatController = new CombatDefeatController(this);
        this.skillTreeController = new SkillTreeController(this, skillService);
        this.musicPlayer = new MusicPlayer();
        this.musicLoader = new MusicLoader();
        this.initializeMusicResources();
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
        CombatFactory combatFactory = this.combatService.createManualCombatFactory(
                this.inventoryService.getDefaultInventory(), this.combatController, Configuration.Game.FLOOR_MIN,
                false);
        this.startCombat(combatFactory, Window.MANUAL_COMBAT);
    }

    public void onStartAutomaticCombat() {
        CombatFactory combatFactory = this.combatService.createAutoCombatFactory(Configuration.Game.FLOOR_MIN, false);
        this.startCombat(combatFactory, Window.AUTOMATIC_COMBAT);
    }

    private void startCombat(CombatFactory combatFactory, Window window) {
        this.teamService.getActiveTeam().ifPresent(playerTeam -> {
            RunTeam playerRunTeam = RunTeam.fromTeam(playerTeam);
            Inventory playerInventory = this.inventoryService.loadInventory();
            TeamFactory opponentFactory = this.teamService
                    .createOpponentFactory(this.bugemonService.getAllDefaultBugemons(), this.random);
            this.combatController.startCombat(playerRunTeam, playerInventory, opponentFactory, combatFactory);
            this.switchTo(window);
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

    private void initializeMusicResources() throws IOException {
        this.musicLoader.loadAllResources(this.musicPlayer);
    }

    private void initTransitions() {
        this.transitions.put(Window.MAIN_MENU, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.mainMenuController.show();
        });
        this.transitions.put(Window.SAVE_MENU, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.saveMenuController.show();
        });
        this.transitions.put(Window.CREATE_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.createTeamController.show();
        });
        this.transitions.put(Window.EDIT_TEAM, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.editTeamController.show();
        });
        this.transitions.put(Window.CREATE_BUGEMON, () -> {
            this.musicPlayer.playAmbiance(Ambiance.MENU, false);
            this.createBugemonController.show();
        });
        this.transitions.put(Window.MANUAL_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.combatController.show();
        });
        // TODO: check to have automatic combat
        this.transitions.put(Window.AUTOMATIC_COMBAT, () -> {
            this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);
            this.combatController.show();
        });
        // TODO: add tower transitions
        this.transitions.put(Window.COMBAT_VICTORY, () -> {
            this.combatVictoryController.show();
            this.musicPlayer.playAmbiance(Ambiance.VICTORY, true);
        });
        this.transitions.put(Window.COMBAT_DEFEAT, () -> {
            this.combatDefeatController.show();
            this.musicPlayer.playAmbiance(Ambiance.DEFEAT, true);
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
        this.musicPlayer.stopMusic();
        this.musicPlayer.playAmbiance(Ambiance.COMBAT, false);

        this.combatController.initialize(combat);
        this.combatController.show();
    }
}
