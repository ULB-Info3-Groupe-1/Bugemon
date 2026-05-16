package ulb.models.tower.room;

public abstract class Room {
    RoomState state = RoomState.LOCKED; // default state
    private boolean visited = false;

    public boolean hasPlayerWon() {
        return false;
    }

    public RoomState getState() {
        return this.state;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited() {
        this.visited = true;
        this.state = RoomState.VISITED;
    }

    public void setState(RoomState roomState) {
        this.state = roomState;
    }

    public abstract RoomType getType();

    public enum RoomState {
        AVAILABLE,
        VISITED,
        VISITED_AVAILABLE,
        LOCKED
    }

    public enum RoomType {
        COMBAT,
        BOSS,
        REWARD,
        EMPTY,
    }
}
