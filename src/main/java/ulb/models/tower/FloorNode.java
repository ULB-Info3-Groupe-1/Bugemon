package ulb.models.tower;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ulb.models.tower.room.Room;

public class FloorNode {
    private final RoomPosition position;
    private final List<FloorNode> children;
    private final FloorNode parent;
    private Room room;

    public FloorNode(RoomPosition position, Room room, List<FloorNode> children, FloorNode parent) {
        this.position = position;
        this.room = room;
        this.children = children;
        this.parent = parent;
    }

    public int getX() {
        return this.position.col();
    }

    public int getY() {
        return this.position.row();
    }

    public RoomPosition getPosition() {
        return this.position;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<FloorNode> getChildren() {
        return this.children;
    }

    public Optional<FloorNode> getParent() {
        return Optional.ofNullable(this.parent);
    }

    public void addChild(FloorNode child) {
        this.children.add(child);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FloorNode other = (FloorNode) obj;
        return this.position.equals(other.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.position);
    }

    public record RoomPosition(int row, int col) {
    }
}
