package ulb.controllers.combat;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.stage.Stage;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.combat.Combat;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.EmptyRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.tower.room.Room;
import ulb.models.trainer.ManualTrainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.FloorMapView;
import ulb.views.ViewLoader;
import ulb.views.components.RoomNodeView;

public class TowerController extends Controller<FloorMapView> implements FloorMapView.Listener {
    private Tower tower;
    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private final Map<RoomNodeView, FloorNode> floorNodesByRoomNode;
    private boolean runEnded;

    public TowerController(MetaController metaController, PlayerService playerService, BugemonService bugemonService)
            throws IOException {
        super(metaController, ViewLoader.load(FloorMapView::new));
        this.tower = null;
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.floorNodesByRoomNode = new HashMap<>();
        this.view.setListener(this);
    }

    @Override
    public void onRoomClicked(RoomNodeView roomNodeView) {
        FloorNode selectedNode = this.floorNodesByRoomNode.get(roomNodeView);
        if (selectedNode == null) {
            throw new IllegalStateException("Clicked room is not mapped to a floor node");
        }

        Floor currentFloor = this.tower.getCurrentFloor();
        FloorNode currentNode = currentFloor.getCurrentPosition();
        currentFloor.moveTo(selectedNode);
        if (currentNode.equals(currentFloor.getCurrentPosition())) {
            return;
        }

        this.view.animatePlayerTo(roomNodeView);
        Room selectedRoom = selectedNode.getRoom();
        if (selectedRoom instanceof CombatRoom) {
            this.handleCombatRoom((CombatRoom) selectedRoom);
        } else if (selectedRoom instanceof RewardRoom) {
            this.handleRewardRoom((RewardRoom) selectedRoom);
            this.setupFloorView();
        } else if (selectedRoom instanceof EmptyRoom) {
            this.handleEmptyRoom((EmptyRoom) selectedRoom);
            this.setupFloorView();
        } else {
            throw new IllegalStateException("Unknown room type: " + selectedRoom.getClass().getSimpleName());
        }
    }

    private void handleCombatRoom(CombatRoom combatRoom) {
        Combat combat = combatRoom
                .getCombat(new ManualTrainer(this.playerService.getActiveTeam(), this.playerService.getInventory()));
        ManualCombatController manualCombatController = this.metaController.getCombatController();
        manualCombatController.startCombat(combat);

    }

    private void handleRewardRoom(RewardRoom rewardRoom) {
        // TODO: Implement reward room handling
    }

    private void handleEmptyRoom(EmptyRoom emptyRoom) {
        // No action needed for empty rooms, but method is here for clarity and future
        // extensibility.
    }

    @Override
    public void onBackToMainMenu() {
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

        this.setupFloorView();

        // Show the view using the Controller's show() method
        this.show(stage);
    }

    private void setupFloorView() {
        Floor currentFloor = this.tower.getCurrentFloor();
        FloorNode currentNode = currentFloor.getCurrentPosition();
        List<FloorNode> floorNodes = currentFloor.getFloorNodes();
        Set<FloorNode> reachableNodes = new HashSet<>(currentFloor.getReachableNodes());
        Set<FloorNode> visitedNodes = this.collectVisitedNodes(currentNode);
        Map<FloorNode, RoomNodeView> roomNodeByFloorNode = new HashMap<>();

        this.floorNodesByRoomNode.clear();
        this.view.clearMap();

        for (FloorNode node : floorNodes) {
            RoomNodeView roomNodeView = new RoomNodeView();
            roomNodeView.setPosition(node.getX(), node.getY());
            roomNodeView.setRoomType(this.resolveRoomType(node));
            roomNodeView.setRoomState(this.resolveRoomState(node, currentNode, reachableNodes, visitedNodes));
            this.floorNodesByRoomNode.put(roomNodeView, node);
            roomNodeByFloorNode.put(node, roomNodeView);
            this.view.addRoomNode(roomNodeView);
        }
        RoomNodeView currentRoomView = roomNodeByFloorNode.get(currentNode);
        if (currentRoomView != null) {
            this.view.setupPlayer(currentRoomView, "/png/Trainer.png");
        }

        this.view.centerMap();
        for (FloorNode node : floorNodes) {
            RoomNodeView source = roomNodeByFloorNode.get(node);
            if (source == null) {
                continue;
            }
            for (FloorNode child : node.getChildren()) {
                RoomNodeView target = roomNodeByFloorNode.get(child);
                if (target != null) {
                    this.view.addConnectionBetweenRooms(source, target);
                }
            }
        }
    }

    private Set<FloorNode> collectVisitedNodes(FloorNode currentNode) {
        Set<FloorNode> visitedNodes = new HashSet<>();
        FloorNode cursor = currentNode;
        while (cursor != null) {
            visitedNodes.add(cursor);
            cursor = cursor.getParent().orElse(null);
        }
        return visitedNodes;
    }

    private RoomType resolveRoomType(FloorNode node) {
        if (node.getDepth() == 0) {
            return RoomType.START;
        }
        Room room = node.getRoom();
        if (room instanceof CombatRoom combatRoom) {
            return combatRoom.isBoss() ? RoomType.BOSS : RoomType.COMBAT;
        }
        if (room instanceof RewardRoom) {
            return RoomType.REWARD;
        }
        if (room instanceof EmptyRoom) {
            return RoomType.EMPTY;
        }
        throw new IllegalStateException("Unknown room type: " + room.getClass().getSimpleName());
    }

    private RoomState resolveRoomState(FloorNode node, FloorNode currentNode, Set<FloorNode> reachableNodes,
            Set<FloorNode> visitedNodes) {
        if (node.equals(currentNode)) {
            return RoomState.CURRENT;
        }
        if (reachableNodes.contains(node)) {
            return RoomState.AVAILABLE;
        }
        if (visitedNodes.contains(node)) {
            return RoomState.VISITED;
        }
        return RoomState.LOCKED;
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

            if (currentRoom instanceof CombatRoom) {
                return;
            }
        }
    }

    private void handleCombatResult(boolean playerWon, Floor floor) {
        if (!playerWon) {
            this.runEnded = true;
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            this.playerService.restoreHpActiveTeam();
            return;
        }
        this.continueRun();
    }

    private boolean advanceToNextFloorIfPossible() {
        if (!this.tower.isFloorComplete()) {
            throw new IllegalStateException("Current floor is not complete");
        }

        // Reaching the end of floor 9 means the Tower run is complete.
        if (this.tower.getCurrentFloorNumber() == 8) {
            this.runEnded = true;
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

    public enum RoomState {
        CURRENT,
        AVAILABLE,
        VISITED,
        LOCKED
    }
}
