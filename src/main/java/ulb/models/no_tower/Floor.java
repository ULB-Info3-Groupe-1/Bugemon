package ulb.models.no_tower;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import ulb.models.no_tower.room.Room;
import ulb.models.no_tower.utils.FloorGenerator;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

public class Floor {
    private final Trainer playerTrainer;
    private final BugemonService bugemonService;

    private final FloorGenerator floorGenerator = new FloorGenerator();
    private FloorNode currentPosition;

    public Floor(Trainer playerTrainer, BugemonService bugemonService) {
        this.playerTrainer = playerTrainer;
        this.bugemonService = bugemonService;
        this.currentPosition = this.floorGenerator.getRoot();
    }

    public boolean isComplete() {
        return this.currentPosition.equals(this.floorGenerator.getBossNode());
    }

    public List<Room> getNextRooms() throws EmptyStackException {
        List<Room> nextRooms = new ArrayList<>();
        for (FloorNode child : this.currentPosition.getChildren()) {
            nextRooms.add(child.getRoom());
        }
        this.currentPosition.getParent().ifPresent(parent -> nextRooms.add(parent.getRoom()));
        return nextRooms;
    }

    public void moveTo(FloorNode node) {
        // check the movement before, if in childeren of parent of current pos
        // should probably set also logic like event depending on the node where
        // the player move...
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
}
