package ulb.models.tower;

import java.util.List;

import ulb.Configuration;
import ulb.models.run.RunTeam;
import ulb.models.tower.FloorMap.RoomPosition;

public class TowerState {

    private int seed;
    private final RunTeam runTeam;
    private int currentFloor;
    private FloorMap floorMap;

    public TowerState(int seed, RunTeam runTeam, int currentFloor, FloorMap floorMap) {
        this.seed = seed;
        this.runTeam = runTeam;
        this.currentFloor = currentFloor;
        this.floorMap = floorMap;
    }

    public void clear() {
        // we re-generate the seed to ensure a different floor layout for the next run
        this.seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        this.runTeam.getMembers().clear();
        this.currentFloor = Configuration.Game.FLOOR_MIN;
        this.floorMap = null;
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

    public List<RoomPosition> getVisitedRoomsPosition() {
        return this.floorMap.getVisitedRooms().stream().map(this.floorMap::getPosition).toList();
    }
}
