package ulb.models.tower;

import ulb.models.run.RunTeam;

/**
 * Immutable snapshot of the player's current tower run.
 *
 * <p>
 * Captures the random seed (used to reproduce floor maps), the active run team, the current floor number, and the
 * floor's navigable map. This object is persisted via {@link ulb.repositories.TowerRepository} so a run can be resumed
 * across sessions.
 */
public class TowerState {
    private final int seed;
    private final RunTeam runTeam;
    private int currentFloor;
    private FloorMap floorMap;

    /**
     * @param seed
     *            the random seed for this run; drives procedural floor generation
     * @param runTeam
     *            the team actively participating in this run
     * @param currentFloor
     *            the 1-based index of the floor the player is currently on
     * @param floorMap
     *            the navigable map for {@code currentFloor}
     */
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
