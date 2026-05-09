package ulb.models.tower;

import java.util.HashSet;
import java.util.Set;

import ulb.Configuration;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomState;
import ulb.models.tower.room.RoomVisitor;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.TowerService;

public class Tower {

    private final TowerService towerService;
    private final TowerFloors floors;
    private final Trainer playerTrainer;
    private Floor currentFloor;
    private boolean isFinished = false;

    public Tower(BugemonTeam playerTeam, BugemonService bugemonService, InventoryService inventoryService,
            TowerService towerService) {
        this.towerService = towerService;
        this.playerTrainer = new ManualTrainer(playerTeam, inventoryService);

        int currentFloorLevel = this.towerService.getCurrentFloor();
        this.floors = new TowerFloors();
        for (int i = Configuration.Game.FLOOR_MIN; i <= Configuration.Game.FLOOR_MAX; i++) {
            Floor newFloor = new Floor(this.playerTrainer, bugemonService, i);
            if (i == currentFloorLevel) {
                this.currentFloor = newFloor;
            }
            this.floors.add(newFloor);
        }
        this.updateRoomsState();
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public boolean isCompleted() {
        return this.isCurrentFloorComplete() && !this.hasNextFloor();
    }

    public FloorNode getPlayerPosition() {
        return this.currentFloor.getCurrentPosition();
    }

    public Floor getCurrentFloor() {
        return this.currentFloor;
    }

    public int getCurrentFloorNumber() {
        return this.currentFloor.getFloorLevel();
    }

    /**
     * Move the player to the given node from the floor it is currently on
     *
     * @param node
     *            The node to move the player to
     */
    public void currentFloorMoveTo(FloorNode node) {
        this.currentFloor.moveTo(node);
    }

    /**
     * Visit the current room if it has not been visited yet
     */
    public void visitCurrentRoom(RoomVisitor roomVisitor) {
        this.currentFloor.visitCurrentRoom(roomVisitor);
        this.updateRoomsState();
    }

    public void checkFloorCompletion() {
        if (this.isCurrentFloorComplete()) {
            if (this.getCurrentFloorNumber() == Configuration.Game.FLOOR_MAX) {
                this.isFinished = true;
            } else {
                this.goToNextFloor();
            }
        }
        this.updateRoomsState();
    }

    boolean isCurrentFloorComplete() {
        return this.currentFloor.isComplete();
    }

    void goToNextFloor() {
        if (!this.isCurrentFloorComplete()) {
            throw new IllegalStateException("Current floor is not complete");
        }
        if (!this.hasNextFloor()) {
            throw new IllegalStateException("No more floors");
        }
        this.currentFloor = this.floors.getFloorByLevel(this.getCurrentFloorNumber() + 1);
        this.towerService.saveFloor(this.getCurrentFloorNumber());
    }

    private boolean hasNextFloor() {
        return this.getCurrentFloorNumber() < Configuration.Game.FLOOR_MAX;
    }

    /**
     * Recomputes every room's {@link RoomState} based on the current player position.
     *
     * <p>
     * The view disables interaction for {@code LOCKED} rooms, so this must be called at least once before the floor is
     * displayed.
     */
    private void updateRoomsState() {
        Floor floor = this.currentFloor;
        Set<FloorNode> reachableNodes = new HashSet<>(floor.getReachableNodes());

        for (FloorNode node : floor.getFloorNodes()) {
            RoomState state;
            Room room = node.getRoom();
            if (node.equals(floor.getCurrentPosition()) || room.isVisited()) {
                state = RoomState.VISITED;
            } else if (reachableNodes.contains(node)) {
                state = RoomState.AVAILABLE;
            } else {
                state = RoomState.LOCKED;
            }
            room.setState(state);
        }
    }
}
