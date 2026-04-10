package ulb.models.tower;

import java.util.ArrayList;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;

public class Tower {

    private static final int MAX_FLOORS = 9;
    private int currentFloor = 0;
    private final ArrayList<Floor> floors = new ArrayList<>();
    private final Trainer playerTrainer;

    // TODO: change to Player class when it will be implemented
    public Tower(BugemonTeam playerTeam, PlayerService playerService, BugemonService bugemonService) {
        this.playerTrainer = new ManualTrainer(playerTeam, playerService.getInventory());
        for (int i = 0; i < MAX_FLOORS; i++) {
            this.floors.add(new Floor(this.playerTrainer, bugemonService, i));
        }
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
}
