package ulb.models.tower.room;

public abstract class Room {
    RoomState state = RoomState.LOCKED; // default state
    private RoomPosition position;

    public RoomState getState() {
        return this.state;
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
        REWARD,
        EMPTY
    }

    public record RoomPosition(int row, int col) {
    }
}
