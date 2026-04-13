package ulb.models.tower.room;

/**
 * Abstract base for all tower rooms. Tracks {@link RoomState} and delegates concrete behaviour to subclasses via the
 * Visitor pattern ({@link #visit(RoomVisitor)}).
 */
public abstract class Room {
    RoomState state = RoomState.LOCKED; // default state

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
}
