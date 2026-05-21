package ulb.models.tower;

import ulb.models.run.RunTeam;

public class TowerState {
    private final int seed;
    private final RunTeam runTeam;
    private int currentFloor;
    private FloorMap floorMap;

    public TowerState(int seed, RunTeam runTeam, int currentFloor, FloorMap floorMap) {
        this.seed = seed;
        this.runTeam = runTeam;
        this.currentFloor = currentFloor;
        this.floorMap = floorMap;
    }

    public int getSeed() {
        return this.seed;
    }

    public RunTeam getRunTeam() {
        return this.runTeam;
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public FloorMap getFloorMap() {
        return this.floorMap;
    }

    public String getTeamName() {
        return this.runTeam.getName();
    }
}
