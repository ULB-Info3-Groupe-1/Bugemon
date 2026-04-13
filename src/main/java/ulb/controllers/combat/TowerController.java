package ulb.controllers.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.stage.Stage;

import ulb.controllers.Controller;
import ulb.controllers.LevelUpController;
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
import ulb.models.tower.room.Room.RoomState;
import ulb.models.tower.room.RoomVisitor;
import ulb.models.trainer.ManualTrainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.views.FloorView;
import ulb.views.ViewLoader;
import ulb.views.components.RoomView;

public class TowerController extends Controller<FloorView> implements FloorView.Listener, RoomVisitor {
    private Optional<Tower> tower;
    private final PlayerService playerService;
    private final BugemonService bugemonService;

    // Annex controllers used for the many tasks the TowerController has to handle
    // and delegate.
    private final ManualCombatController manualCombatController;
    private final LevelUpController levelUpController;

    public TowerController(MetaController metaController, PlayerService playerService, BugemonService bugemonService,
            ManualCombatController manualCombatController, LevelUpController levelUpController) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.tower = Optional.empty();
        this.manualCombatController = manualCombatController;
        this.levelUpController = levelUpController;
        this.view.setListener(this);
    }

    @Override
    public void onRoomClicked(RoomView roomNodeView) {
        FloorNode selectedNode = this.floorNodesByRoomNode.get(roomNodeView);
        if (selectedNode == null) {
            throw new IllegalStateException("Clicked room is not mapped to a floor node");
        }

        Floor currentFloor = this.tower.getCurrentFloor();
        FloorNode currentNode = currentFloor.getCurrentPosition();
        currentFloor.moveTo(selectedNode);
        FloorNode nextNode = currentFloor.getCurrentPosition();
        if (currentNode.equals(nextNode)) {
            return;
        }
        this.visitedNodes.add(currentNode);
        this.visitedNodes.add(nextNode);

        Room selectedRoom = nextNode.getRoom();
        RoomView targetRoomView = this.roomNodesByFloorNode.get(nextNode);
        if (targetRoomView != null) {
            this.view.animatePlayerTo(targetRoomView);
        }
        if (!selectedRoom.isCompleted()) {
            selectedRoom.visit(this);
        }
        this.refreshFloorViewState();
    }

    public void visitCombatRoom(CombatRoom combatRoom) {
        Combat combat = combatRoom.getCombat(new ManualTrainer(
                this.playerService.getActiveTeam().orElseThrow(() -> new IllegalStateException("No active team")),
                this.playerService.getInventory()));
        this.metaController.startTowerCombat(combat);
    }

    public void visitRewardRoom(RewardRoom rewardRoom) {
        // NOT IMPLEMENTED
    }

    public void visitEmptyRoom(EmptyRoom emptyRoom) {
        // No action needed for empty rooms, but method is here for clarity and future
        // extensibility.
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.endTowerFlow();
        this.runEnded = true;
        this.metaController.switchTo(Window.MAIN_MENU);
    }

    public void onTowerCombatFinished(boolean playerWon) {
        Floor currentFloor = this.tower.getCurrentFloor();
        Room currentRoom = currentFloor.getCurrentRoom();
        if (!(currentRoom instanceof CombatRoom combatRoom)) {
            throw new IllegalStateException("Tower combat finished outside a combat room");
        }

        if (!playerWon) {
            this.runEnded = true;
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            try {
                this.playerService.restoreHpActiveTeam();
            } catch (NoActiveTeamException e) {
                throw new IllegalStateException("No active team when combat ended is not possible", e);
            }
            return;
        }

        combatRoom.markCompleted();
        this.visitedNodes.add(currentFloor.getCurrentPosition());
        if (currentFloor.isComplete() && !this.advanceToNextFloorIfPossible()) {
            return;
        }

        this.metaController.switchTo(Window.NOTOWER);
    }

    /**
     * Runs the Tower flow until a combat starts, the run ends, or the tower is
     * completed. Reward rooms are resolved
     * immediately; combat rooms continue via callback.
     */
    public void runTower(Stage stage) {
        if (!this.ensureRunIsReady()) {
            return;
        }

        this.showFloorMap();
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void showFloorMap() {
        this.view.setFloorNumber(this.tower.getCurrentFloorNumber() + 1);
        this.view.setInstruction();
        this.updateFloorStructure();
        this.refreshFloorViewState();

        this.show();
    }

    private void updateFloorStructure() {
        int currentFloorNumber = this.tower.getCurrentFloorNumber();
        if (this.renderedFloorNumber == currentFloorNumber && !this.roomNodesByFloorNode.isEmpty()) {
            return;
        }

        this.setupFloorStructure(this.tower.getCurrentFloor());
        this.renderedFloorNumber = currentFloorNumber;
        this.visitedNodes.clear();
    }

    private void setupFloorStructure(Floor currentFloor) {
        List<FloorNode> floorNodes = currentFloor.getFloorNodes();

        this.floorNodesByRoomNode.clear();
        this.roomNodesByFloorNode.clear();
        this.view.clearMap();

        for (FloorNode node : floorNodes) {
            RoomView roomNodeView = new RoomView();
            roomNodeView.setPosition(node.getX(), node.getY());
            roomNodeView.setRoomType(this.resolveRoomType(node));
            this.floorNodesByRoomNode.put(roomNodeView, node);
            this.roomNodesByFloorNode.put(node, roomNodeView);
            this.view.addRoomNode(roomNodeView);
        }

        this.view.centerMap();
        for (FloorNode node : floorNodes) {
            RoomView source = this.roomNodesByFloorNode.get(node);
            if (source == null) {
                continue;
            }
            for (FloorNode child : node.getChildren()) {
                RoomView target = this.roomNodesByFloorNode.get(child);
                if (target != null) {
                    this.view.addConnectionBetweenRooms(source, target);
                }
            }
        }
    }

    private void refreshFloorViewState() {
        Floor currentFloor = this.tower.getCurrentFloor();
        FloorNode currentNode = currentFloor.getCurrentPosition();
        Set<FloorNode> reachableNodes = new HashSet<>(currentFloor.getReachableNodes());
        this.visitedNodes.add(currentNode);

        for (Map.Entry<FloorNode, RoomView> entry : this.roomNodesByFloorNode.entrySet()) {
            FloorNode node = entry.getKey();
            RoomView roomNodeView = entry.getValue();
            roomNodeView.setRoomState(this.resolveRoomState(node, currentNode, reachableNodes));
        }

        RoomView currentRoomView = this.roomNodesByFloorNode.get(currentNode);
        if (currentRoomView != null) {
            this.view.setupPlayer(currentRoomView, "/png/Trainer.png");
        }
    }

    private RoomType resolveRoomType(FloorNode node) {
        if (node.getDepth() == 0) {
            return RoomType.START;
        }
        return node.getRoom().getType();
    }

    private RoomState resolveRoomState(FloorNode node, FloorNode currentNode, Set<FloorNode> reachableNodes) {
        if (node.equals(currentNode)) {
            return RoomState.CURRENT;
        }
        if (node.getRoom().isCompleted() || this.visitedNodes.contains(node)) {
            return RoomState.VISITED;
        }
        if (reachableNodes.contains(node)) {
            return RoomState.AVAILABLE;
        }
        return RoomState.LOCKED;
    }

    /**
     * Ensures that a Tower run can be started or continued. If the player has no
     * active team, or if the current run has
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
            this.tower = new Tower(this.playerService, this.bugemonService);
            this.runEnded = false;
            this.renderedFloorNumber = -1;
            this.floorNodesByRoomNode.clear();
            this.roomNodesByFloorNode.clear();
            this.visitedNodes.clear();
        }

        return true;
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
}
