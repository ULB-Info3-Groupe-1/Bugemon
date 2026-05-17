package ulb.models.tower;

import ulb.models.item.Inventory;
import ulb.models.run.RunTeam;

public class Tower {

    // TODO: move this elsewhere
    private static final int STARTING_FLOOR = 2;

    private final RunTeam runTeam;
    private final Inventory inventory;

    // TODO: name is bad -> confusion between those two
    private int currentFloor;
    private Floor floor;

    public Tower(RunTeam runTeam, Inventory inventory) {
        this(runTeam, inventory, STARTING_FLOOR);
    }

    public Tower(RunTeam runTeam, Inventory inventory, int currentFloor) {
        this.runTeam = runTeam;
        this.inventory = inventory;
        this.currentFloor = currentFloor;
    }

    public RunTeam getRunTeam() {
        return this.runTeam;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Floor getCurrentFloor() {
        return this.floor;
    }

    public void advanceFloor() {
        this.currentFloor++;
    }

    public FloorNode getPlayerPosition() {
        return this.floor.getCurrentNode();
    }
}
