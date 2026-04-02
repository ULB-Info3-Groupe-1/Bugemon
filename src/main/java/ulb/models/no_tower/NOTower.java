package ulb.models.no_tower;

import java.util.ArrayList;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.ManualTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.PlayerService;

public class NOTower {
    //

    private static final int MAX_FLOORS = 9;
    private int currentFloor = 0;
    private ArrayList<Floor> floors = new ArrayList<>();

    // TODO: change to Player class when it will be implemented
    public NOTower(BugemonTeam playerTeam, PlayerService playerService) {
        this.generateFloors(playerTeam, playerService);
    }

    public int getCurrentFloorNumber() {
        return this.currentFloor;
    }

    // TODO: is it really useful ?
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

    private void generateFloors(BugemonTeam playerTeam, PlayerService playerService) {
        Trainer playerTrainer = new ManualTrainer(playerTeam, playerService.getInventory());
        for (int i = 0; i < MAX_FLOORS; i++) {
            this.floors.add(new Floor(playerTrainer, playerService));
        }
    }
}
