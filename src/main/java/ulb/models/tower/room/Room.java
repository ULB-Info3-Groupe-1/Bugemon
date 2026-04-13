package ulb.models.tower.room;

public abstract class Room {
    RoomState state = RoomState.LOCKED; // default state
    private RoomPosition position;

    public RoomState getState() {
        return this.state;
    }

    public boolean isVisited() {
        return this.state.equals(RoomState.VISITED);
    }

    public void setVisited() {
        this.state = RoomState.VISITED;
    }

    public void setState(RoomState roomState) {
        this.state = roomState;
    }

    public RoomPosition getPosition() {
        return this.position;
    }

    public void setPosition(RoomPosition position) {
        this.position = position;
    }

    public abstract void visit(RoomVisitor roomVisitor);

    public abstract RoomType getType();

    public enum RoomState {
        AVAILABLE,
        VISITED,
        LOCKED
    }

    public enum RoomType {
        COMBAT,
        BOSS,
        REWARD,
        EMPTY,
    }

    public record RoomPosition(int row, int col) {
    }
}
