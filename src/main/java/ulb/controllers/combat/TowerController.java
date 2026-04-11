package ulb.controllers.combat;

import java.io.IOException;
import javafx.stage.Stage;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.tower.Floor;
import ulb.models.tower.Tower;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.tower.room.Room;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.FloorMapView;
import ulb.views.ViewLoader;
import ulb.views.components.RoomNodeView;
import ulb.views.components.RoomNodeView.RoomState;

public class TowerController extends Controller<FloorMapView> implements FloorMapView.Listener {
    private Tower tower;
    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private boolean runEnded;

    public TowerController(MetaController metaController, PlayerService playerService, BugemonService bugemonService)
            throws IOException {
        super(metaController, ViewLoader.load(FloorMapView::new));
        this.tower = null;
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    @Override
    public void onRoomClicked(int row, int col) {
        System.out.println("Room clicked at (" + row + ", " + col + ")");
        this.continueRun();
    }

    @Override
    public void onBackToMainMenu() {
        this.runEnded = true;
        this.metaController.endTowerFlow();
        this.metaController.switchTo(Window.MAIN_MENU);
    }

    /**
     * Runs the Tower flow until a combat starts, the run ends, or the tower is completed. Reward rooms are resolved
     * immediately; combat rooms continue via callback.
     */
    public void runTower(Stage stage) {
        if (!this.ensureRunIsReady()) {
            return;
        }

        this.showFloorMap(stage);
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void showFloorMap(Stage stage) {
        // Configure la vue de la carte
        this.view.setFloorNumber(this.tower.getCurrentFloorNumber() + 1);
        this.view.setInstructions("Cliquez sur une salle disponible pour continuer votre ascension");

        // TODO: Populate the floor map with rooms from currentFloor
        // For now, show a simple test setup
        this.setupTestFloorMap();

        // Show the view using the Controller's show() method
        this.show(stage);
    }

    /**
     * Temporary method to show a test floor map. TODO: Replace with actual floor data.
     */
    private void setupTestFloorMap() {
        this.view.clearMap();

        // Create a simple test layout using only row/col
        RoomNodeView startRoom = new RoomNodeView();
        startRoom.setRoomType(RoomType.START);
        startRoom.setRoomState(RoomState.CURRENT);
        startRoom.setPosition(2, 1); // Row 1, Column 1
        this.view.addRoomNode(startRoom);

        RoomNodeView combatRoom = new RoomNodeView();
        combatRoom.setRoomType(RoomType.COMBAT);
        combatRoom.setRoomState(RoomState.AVAILABLE);
        combatRoom.setPosition(2, 2); // Row 1, Column 2
        this.view.addRoomNode(combatRoom);

        RoomNodeView rewardRoom = new RoomNodeView();
        rewardRoom.setRoomType(RoomType.REWARD);
        rewardRoom.setRoomState(RoomState.VISITED);
        rewardRoom.setPosition(1, 3); // Row 2, Column 1
        this.view.addRoomNode(rewardRoom);

        RoomNodeView bossRoom = new RoomNodeView();
        bossRoom.setRoomType(RoomType.BOSS);
        bossRoom.setRoomState(RoomState.AVAILABLE);
        bossRoom.setPosition(3, 3); // Row 2, Column 3
        this.view.addRoomNode(bossRoom);

        // Center the map and generate connections
        this.view.centerMap();
        this.view.generateConnections();
    }

    /**
     * Ensures that a Tower run can be started or continued. If the player has no active team, or if the current run has
     * ended, a new run is initialised. If a new run cannot be started.
     *
     * @return
     */
    private boolean ensureRunIsReady() {
        if (this.playerService.getActiveTeam().isEmpty()) {
            this.runEnded = true;
            this.metaController.switchTo(Window.CREATE_TEAM);
            return false;
        }

        if (this.tower == null || this.runEnded) {
            this.tower = new Tower(this.playerService.getActiveTeam(), this.playerService, this.bugemonService);
            this.runEnded = false;
        }

        return true;
    }

    /**
     * Continues the current Tower run until a combat room is reached, the run ends, or the tower is completed.
     */
    private void continueRun() {
        while (!this.runEnded) {
            Floor currentFloor = this.tower.getCurrentFloor();

            if (currentFloor.isComplete()) {
                if (!this.advanceToNextFloorIfPossible()) {
                    return;
                }
                continue;
            }

            Room currentRoom = currentFloor.getCurrentRoom();
            this.handleRoom(currentFloor, currentRoom);

            if (currentRoom instanceof CombatRoom) {
                return;
            }
        }
    }

    private void handleRoom(Floor floor, Room room) {
        if (room instanceof CombatRoom combatRoom) {
            try {
                ManualCombatController manualCombatController = new ManualCombatController(this.metaController,
                        this.playerService, this.bugemonService);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize manual combat", e);
            }
            return;
        }

        if (room instanceof RewardRoom rewardRoom) {
            this.handleRewardRoom(rewardRoom);
            return;
        }

        throw new IllegalStateException("Unknown room type: " + room.getClass().getSimpleName());
    }

    private void handleCombatResult(boolean playerWon, Floor floor) {
        if (!playerWon) {
            this.runEnded = true;
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            this.playerService.restoreHpActiveTeam();
            return;
        }
        this.continueRun();
    }

    private void handleRewardRoom(RewardRoom rewardRoom) {
        // TODO: apply player reward once RewardRoom exposes concrete reward choices.
    }

    private boolean advanceToNextFloorIfPossible() {
        if (!this.tower.isFloorComplete()) {
            throw new IllegalStateException("Current floor is not complete");
        }

        // Reaching the end of floor 9 means the Tower run is complete.
        if (this.tower.getCurrentFloorNumber() == 8) {
            this.runEnded = true;
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            return false;
        }

        this.tower.goToNextFloor();
        return true;
    }

    public boolean hasActiveRun() {
        return this.tower != null && !this.runEnded;
    }

    public enum RoomType {
        START,
        COMBAT,
        BOSS,
        REWARD,
        EMPTY
    }
}
