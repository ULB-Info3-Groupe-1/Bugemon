package ulb.models.tower;

import java.util.ArrayList;
import java.util.List;

import ulb.Configuration;
import ulb.models.run.RunTeam;
import ulb.models.tower.utils.Position;

public class TowerState {
    
    private int seed;
    private final RunTeam runTeam;
    private int floorNumber;
    private Floor currentFloor;
    private FloorNode playerPosition;

    public TowerState(int seed, RunTeam runTeam, int floorNumber, Floor currentFloor) {
        this.seed = seed;
        this.runTeam = runTeam;
        this.floorNumber = floorNumber;
        this.currentFloor = currentFloor;
    }

    public void clear() {
        // we re-generate the seed to ensure a different floor layout for the next run
        this.seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        this.runTeam.getMembers().clear();
        this.floorNumber = Configuration.Game.FLOOR_MIN;
        this.currentFloor = null;
        this.playerPosition = null;
    }

    public int getSeed() {
        return this.seed;
    }



    public RunTeam getRunTeam() {
        return this.runTeam;
    }
    
    public int getCurrentFloorNumber() {
        return this.floorNumber;
    }

    public Floor getCurrentFloor() {
        return this.currentFloor;
    }

    public FloorNode getPlayerPosition() {
        return this.playerPosition;
    }

    public String getTeamName(){
        return this.runTeam.getName();
    }

    public List<Position> getVisitedRoomsPosition() {
        List<Position> visitedPositions = new ArrayList<>();
        
        if (this.currentFloor != null && this.currentFloor.getRoot() != null) {
            collectVisitedRooms(this.currentFloor.getRoot(), visitedPositions);
        }
        
        return visitedPositions;
    }

    private void collectVisitedRooms(FloorNode node, List<Position> visitedPositions) {
        if (node.getRoom() != null && node.getRoom().isVisited()) {
            visitedPositions.add(new Position(node.getX(), node.getY()));
        }

        for (FloorNode child : node.getChildren()) {
            collectVisitedRooms(child, visitedPositions);
        }
    }

}
