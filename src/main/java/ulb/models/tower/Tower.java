package ulb.models.tower;

import java.util.HashSet;
import java.util.Set;

import ulb.Configuration;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomState;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

public class Tower {

    private final TowerFloors floors;
    private final Trainer playerTrainer;
    private int currentFloor;
    private boolean isFinished = false;

    public Tower(BugemonTeam playerTeam, Inventory inventory, BugemonService bugemonService, int currentFloor) {
        this.currentFloor = currentFloor;
        this.playerTrainer = new ManualTrainer(playerTeam, inventory);

        this.floors = new TowerFloors();
        for (int i = Configuration.Game.FLOOR_MIN; i <= Configuration.Game.FLOOR_MAX; i++) {
            this.floors.add(new Floor(this.playerTrainer, bugemonService, i));
        }
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public boolean isCompleted() {
        return this.isFloorComplete() && !this.hasNextFloor();
    }

    public Trainer getPlayerTrainer() {
        return this.playerTrainer;
    }

    public int getCurrentFloorNumber() {
        return this.currentFloor;
    }

    public boolean isFloorComplete() {
        return this.getCurrentFloor().isComplete();
    }

    public Floor getCurrentFloor() {
        return this.floors.getFloorByLevel(this.currentFloor);
    }

    public void goToNextFloor() {
        if (!this.getCurrentFloor().isComplete()) {
            throw new IllegalStateException("Current floor is not complete");
        }
        if (!this.hasNextFloor()) {
            throw new IllegalStateException("No more floors");
        }
        this.currentFloor++;
    }

    private boolean hasNextFloor() {
        return this.currentFloor < Configuration.Game.FLOOR_MAX;
    }

    public void updateRoomsState() {
        Floor floor = this.getCurrentFloor();
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
