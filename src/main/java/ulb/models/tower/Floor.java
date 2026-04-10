package ulb.models.tower;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

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

    public List<Room> getNextRooms() throws EmptyStackException {
        List<Room> nextRooms = new ArrayList<>();
        for (FloorNode child : this.currentPosition.getChildren()) {
            nextRooms.add(child.getRoom());
        }
        this.currentPosition.getParent().ifPresent(parent -> nextRooms.add(parent.getRoom()));
        return nextRooms;
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
