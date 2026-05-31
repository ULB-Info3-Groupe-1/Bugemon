package ulb.controllers.tower;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.common.RoomType;
import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.player.PlayerState;
import ulb.models.skills.SkillContext;
import ulb.models.team.Team;
import ulb.models.tower.FloorMap;
import ulb.models.tower.TowerState;
import ulb.models.tower.exceptions.IllegalMoveException;
import ulb.models.tower.room.Room;
import ulb.models.utils.Position;
import ulb.services.game.InventoryService;
import ulb.services.game.SaveService;
import ulb.services.game.SkillService;
import ulb.services.game.TeamService;
import ulb.services.game.TowerService;
import ulb.views.ViewLoader;
import ulb.views.tower.FloorView;

/**
 * Controller for the tower-run floor-navigation screen.
 *
 * <p>
 * Manages the overall tower run: starting a new run or resuming a saved one, handling room-click navigation,
 * dispatching room-type actions (combat, boss, reward), advancing the floor after a boss victory, and cleaning up after
 * a defeat or run completion.
 */
public class TowerController extends Controller<FloorView> implements FloorView.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private final TowerService towerService;
    private final TeamService teamService;
    private final SkillService skillService;
    private final InventoryService inventoryService;
    private final SaveService saveService;

    private PlayerState playerState;
    private TowerState towerState;

    public TowerController(MetaController metaController, PlayerState playerState, TowerService towerService,
            TeamService teamService, SkillService skillService, InventoryService inventoryService,
            SaveService saveService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.view.setListener(this);
        this.playerState = playerState;
        this.towerService = towerService;
        this.teamService = teamService;
        this.skillService = skillService;
        this.inventoryService = inventoryService;
        this.saveService = saveService;
    }

    /**
     * Starts or resumes a tower run for the current active team.
     *
     * <p>
     * If a saved run exists for the active team it is restored; otherwise a fresh run is created and skill-based
     * starter item bonuses are applied to the player's inventory.
     *
     * @throws IllegalStateException
     *             if no active team is set on the player state
     */
    public void startRun() {
        Team activeTeam = this.playerState.getActiveTeam()
                .orElseThrow(() -> new IllegalStateException("Cannot start tower without an active team"));

        Optional<String> savedTeamName = this.towerService.getSavedTeamName();
        if (savedTeamName.isPresent()) {
            Optional<Team> savedTeam = this.teamService.getTeam(savedTeamName.get());
            if (savedTeam.isPresent()) {
                this.towerState = this.towerService.loadSaved(savedTeam.get())
                        .orElseGet(() -> this.towerService.createTower(activeTeam));
                return;
            }
            this.towerService.delete();
        }
        this.towerState = this.towerService.createTower(activeTeam);
        this.applyStarterItemsBonus();
    }

    private void applyStarterItemsBonus() {
        SkillContext ctx = this.skillService.buildSkillContext(this.playerState.getSkillTreeState());
        this.inventoryService.applyStarterItemsBonus(this.playerState.getInventory(), ctx);
    }

    @Override
    public void show() {
        this.udpateDisplayedFloor();
    }

    @Override
    public void onRoomClicked(int x, int y) {
        FloorMap floorMap = this.towerState.getFloorMap();
        Room room = floorMap.getAllRooms().stream().filter(r -> {
            Position p = floorMap.getPosition(r);
            return p.x() == x && p.y() == y;
        }).findFirst().orElseThrow(() -> new IllegalStateException("No room at (" + x + "," + y + ")"));
        try {
            floorMap.movePlayerTo(room);
        } catch (IllegalMoveException e) {
            LOG.warn("Illegal move attempted to room at ({}, {}): {}", x, y, e.getMessage());
            return;
        }
        this.towerService.save();
        this.view.animatePlayerTo(x, y, () -> this.dispatchRoomAction(room));
    }

    @Override
    public void onReturnToMainMenu() {
        LOG.info("Player returned to main menu from tower");
        this.metaController.endTowerFlow();
        this.metaController.onMainMenu();
    }

    /**
     * Returns {@code true} if a tower run is currently in progress.
     *
     * @return {@code true} when a {@link ulb.models.tower.TowerState} is loaded
     */
    public boolean isRunActive() {
        return this.towerState != null;
    }

    /**
     * Called by the {@link MetaController} when a tower combat ends.
     *
     * <p>
     * On defeat, the active tower run is deleted. On victory in a boss room, the floor is advanced (or the run
     * completed if the maximum floor was reached).
     *
     * @param playerWon
     *            {@code true} if the player's team won the combat
     */
    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
        if (!playerWon) {
            this.towerService.delete();
            this.towerState = null;
            return;
        }
        this.towerService.save();
        Room currentRoom = this.towerState.getFloorMap().getCurrentRoom();
        if (currentRoom.getType() == RoomType.BOSS) {
            this.advanceFloorState();
        }
    }

    public void onBonusRoomExited() {
        this.saveService.save(this.playerState);
        this.udpateDisplayedFloor();
    }

    private void dispatchRoomAction(Room room) {
        if (room.isVisited()) {
            this.udpateDisplayedFloor();
            return;
        }
        switch (room.getType()) {
            case COMBAT -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), false);
            case BOSS -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), true);
            case REWARD -> this.metaController.startRewardFlow(this.towerState.getRunTeam());
            default -> this.udpateDisplayedFloor();
        }
    }

    private void advanceFloorState() {
        int nextFloor = this.towerState.getCurrentFloor() + 1;
        this.playerState.addSkillPoint();
        if (nextFloor > Configuration.Game.FLOOR_MAX) {
            this.towerService.delete();
            this.towerState = null;
            return;
        }
        FloorMap nextFloorMap = this.towerService.generateFloor(this.towerState.getSeed(), nextFloor);
        this.towerState = new TowerState(this.towerState.getSeed(), this.towerState.getRunTeam(), nextFloor,
                nextFloorMap);
        this.towerService.setActiveTower(this.towerState);
        this.saveService.save(this.playerState);
    }

    private void udpateDisplayedFloor() {
        this.view.setFloorState(this.towerState.getCurrentFloor(), this.towerService.buildFloorDisplayDTO());
        super.show();
    }
}
