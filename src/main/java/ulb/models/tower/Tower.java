package ulb.models.tower;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomState;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;

/**
 * A dungeon run composed of {@value #MAX_FLOORS} sequential {@link Floor}s. The player advances one floor at a time;
 * completing the last floor marks the run as a victory.
 */
public class Tower {

    private static final int MAX_FLOORS = 9;
    private int currentFloor = 0;
    private final ArrayList<Floor> floors = new ArrayList<>();
    private final Trainer playerTrainer;
    private boolean isFinished = false;

    public Tower(BugemonTeam playerTeam, PlayerService playerService, BugemonService bugemonService) {
        this.playerTrainer = new ManualTrainer(playerTeam, playerService.getInventory());
        for (int i = 0; i < MAX_FLOORS; i++) {
            this.floors.add(new Floor(this.playerTrainer, bugemonService, i));
        }
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    /** Returns {@code true} when the current floor is complete and no further floors remain. */
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
        return this.floors.get(this.currentFloor).isComplete();
    }

    public Floor getCurrentFloor() {
        return this.floors.get(this.currentFloor);
    }

    /**
     * Advances the tower to the next floor.
     *
     * @throws IllegalStateException
     *             if the current floor is not yet complete or there are no more floors
     */
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
        return this.currentFloor < MAX_FLOORS - 1;
    }

    /**
     * Recomputes the {@link ulb.models.tower.room.Room.RoomState} of every node on the current floor based on the
     * player's position: visited nodes stay {@code VISITED}, adjacent ones become {@code AVAILABLE}, the rest remain
     * {@code LOCKED}.
     */
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
