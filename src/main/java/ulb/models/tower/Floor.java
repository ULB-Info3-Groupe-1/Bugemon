package ulb.models.tower;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.models.tower.room.Room;
import ulb.models.tower.utils.CombatFactory;
import ulb.models.tower.utils.FloorGenerator;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

public class Floor {
    private final Trainer playerTrainer;
    private final FloorGenerator floorGenerator;
    private FloorNode currentPosition;

    public Floor(Trainer playerTrainer, BugemonService bugemonService, int floorLevel) {
        this.playerTrainer = playerTrainer;
        CombatFactory combatFactory = new CombatFactory(bugemonService);
        this.floorGenerator = new FloorGenerator(combatFactory, floorLevel);
        this.currentPosition = this.floorGenerator.getRoot();
    }

    public boolean isComplete() {
        return this.currentPosition.equals(this.floorGenerator.getBossNode());
    }

    public List<Room> getNextRooms() {
        List<Room> nextRooms = new ArrayList<>();
        for (FloorNode node : this.getReachableNodes()) {
            nextRooms.add(node.getRoom());
        }
        return nextRooms;
    }

    public List<FloorNode> getFloorNodes() {
        List<FloorNode> floorNodes = new ArrayList<>();
        Set<FloorNode> visited = new HashSet<>();
        Deque<FloorNode> queue = new ArrayDeque<>();
        queue.add(this.floorGenerator.getRoot());

        while (!queue.isEmpty()) {
            FloorNode node = queue.removeFirst();
            if (!visited.add(node)) {
                continue;
            }
            floorNodes.add(node);
            queue.addAll(node.getChildren());
        }
        return floorNodes;
    }

    public List<FloorNode> getReachableNodes() {
        List<FloorNode> reachableNodes = new ArrayList<>();
        reachableNodes.addAll(this.currentPosition.getChildren());
        this.currentPosition.getParent().ifPresent(reachableNodes::add);
        return reachableNodes;
    }

    public void moveTo(FloorNode node) {
        boolean isChild = this.currentPosition.getChildren().contains(node);
        boolean isParent = this.currentPosition.getParent().map(parent -> parent.equals(node)).orElse(false);
        if (isChild || isParent) {
            this.currentPosition = node;
        }
    }

    public FloorNode getCurrentPosition() {
        return this.currentPosition;
    }

    public Room getCurrentRoom() {
        return this.currentPosition.getRoom();
    }

    public Trainer getPlayerTrainer() {
        return this.playerTrainer;
    }
}
