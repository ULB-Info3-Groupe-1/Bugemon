package bugemon.client.controllers;

import java.util.List;
import java.util.Random;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteSaveService;
import bugemon.client.services.RemoteTowerService;
import bugemon.client.views.FloorView;
import bugemon.client.views.ViewLoader;
import bugemon.common.Configuration;
import bugemon.common.RoomType;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.item.Item;
import bugemon.common.models.item.ItemType;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.skills.SkillContext;
import bugemon.common.models.skills.SkillTree;
import bugemon.common.models.team.Team;
import bugemon.common.models.tower.FloorMap;
import bugemon.common.models.tower.TowerEngine;
import bugemon.common.models.tower.TowerState;
import bugemon.common.models.tower.exceptions.IllegalMoveException;
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.utils.Position;
import bugemon.common.net.TowerInitDataPacket;

/**
 * Controller for the tower-run floor-navigation screen.
 *
 * <p>
 * The client owns the live {@link TowerState} during a run: it is created, advanced and rendered locally through
 * {@link TowerEngine}. The server is contacted only to fetch the run-init data once (saved snapshot + team + static
 * items + skill tree) and to persist snapshots ({@link RemoteTowerService}, {@link RemoteSaveService}). All persistence
 * is fire-and-forget; UI updates are marshalled onto the JavaFX thread via {@link Platform#runLater}.
 */
public class TowerController extends Controller<FloorView> implements FloorView.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private final RemoteTowerService towerService;
    private final RemoteSaveService saveService;
    private final PlayerState playerState;
    private final Random random = new Random();

    private TowerState towerState;

    public TowerController(MetaController metaController, PlayerState playerState, RemoteTowerService towerService,
            RemoteSaveService saveService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.view.setListener(this);
        this.playerState = playerState;
        this.towerService = towerService;
        this.saveService = saveService;
    }

    /**
     * Starts or resumes a tower run, then runs {@code onReady} once the run state is ready.
     *
     * <p>
     * Fetches the run-init data in a single round-trip; on arrival it either restores the saved run or creates a fresh
     * one (applying skill-based starter items), all on the JavaFX thread.
     *
     * @param onReady
     *            navigation to perform once the run is initialised (e.g. show the tower screen)
     */
    public void startRun(Runnable onReady) {
        this.towerService.getInitData().whenComplete((data, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to load tower init data", error);
                return;
            }
            this.initRun(data);
            onReady.run();
        }));
    }

    private void initRun(TowerInitDataPacket data) {
        Team activeTeam = this.playerState.getActiveTeam()
                .orElseThrow(() -> new IllegalStateException("Cannot start tower without an active team"));

        if (data.savedTower() != null && data.savedTeam() != null) {
            this.towerState = TowerEngine.restore(data.savedTower(), data.savedTeam());
            return;
        }
        if (data.savedTower() != null) {
            this.deleteTower();
        }
        this.towerState = TowerEngine.createTower(activeTeam);
        this.applyStarterItemsBonus(data.items(), data.skillTree());
    }

    private void applyStarterItemsBonus(List<Item> items, SkillTree skillTree) {
        SkillContext ctx = new SkillContext(this.playerState.getSkillTreeState(), skillTree);
        Inventory inventory = this.playerState.getInventory();
        for (ItemType type : ItemType.values()) {
            int quantity = ctx.getStarterItemQuantity(type);
            if (quantity <= 0) {
                continue;
            }
            List<Item> eligible = items.stream().filter(item -> item.type() == type).toList();
            if (eligible.isEmpty()) {
                continue;
            }
            for (int i = 0; i < quantity; i++) {
                inventory.addItem(eligible.get(this.random.nextInt(eligible.size())), 1);
            }
        }
    }

    @Override
    protected void show() {
        this.updateDisplayedFloor();
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
        this.persistTower();
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
     * @return {@code true} when a {@link TowerState} is loaded
     */
    public boolean isRunActive() {
        return this.towerState != null;
    }

    /**
     * Called by the {@link MetaController} when a tower combat ends. On defeat the run is deleted; on a boss victory the
     * floor is advanced.
     *
     * @param playerWon
     *            {@code true} if the player's team won the combat
     */
    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
        if (!playerWon) {
            this.deleteTower();
            this.towerState = null;
            return;
        }
        this.persistTower();
        Room currentRoom = this.towerState.getFloorMap().getCurrentRoom();
        if (currentRoom.getType() == RoomType.BOSS) {
            this.advanceFloorState();
        }
    }

    void onBonusRoomExited() {
        this.persistGame();
        this.updateDisplayedFloor();
    }

    private void dispatchRoomAction(Room room) {
        if (room.isVisited()) {
            this.updateDisplayedFloor();
            return;
        }
        switch (room.getType()) {
            case COMBAT -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), false);
            case BOSS -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), true);
            case REWARD -> this.metaController.startRewardFlow(this.towerState.getRunTeam());
            default -> this.updateDisplayedFloor();
        }
    }

    private void advanceFloorState() {
        int nextFloor = this.towerState.getCurrentFloor() + 1;
        this.playerState.addSkillPoint();
        if (nextFloor > Configuration.Game.FLOOR_MAX) {
            this.deleteTower();
            this.towerState = null;
            return;
        }
        FloorMap nextFloorMap = TowerEngine.generateFloor(this.towerState.getSeed(), nextFloor);
        this.towerState = new TowerState(this.towerState.getSeed(), this.towerState.getRunTeam(), nextFloor,
                nextFloorMap);
        this.persistGame();
    }

    private void updateDisplayedFloor() {
        this.view.setFloorState(this.towerState.getCurrentFloor(), TowerEngine.buildFloorDisplayDTO(this.towerState));
        super.show();
    }

    /** Persists the current run snapshot in the background. */
    private void persistTower() {
        TowerDTO dto = TowerEngine.toDTO(this.playerState.getPlayerName(), this.towerState);
        this.towerService.save(dto).whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.warn("Failed to persist tower run", error);
            }
        });
    }

    /** Deletes the saved run in the background. */
    private void deleteTower() {
        this.towerService.delete().whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.warn("Failed to delete tower run", error);
            }
        });
    }

    /** Persists the full game state (including the current run, if any) in the background. */
    private void persistGame() {
        TowerDTO dto = this.towerState != null ? TowerEngine.toDTO(this.playerState.getPlayerName(), this.towerState)
                : null;
        this.saveService
                .saveGame(this.playerState.getActiveTeam().orElse(null), this.playerState.getInventory(),
                        this.playerState.getSkillTreeState(), dto)
                .whenComplete((ignored, error) -> {
                    if (error != null) {
                        LOG.warn("Failed to persist game state", error);
                    }
                });
    }
}
