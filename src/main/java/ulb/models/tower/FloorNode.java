package ulb.models.tower;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ulb.models.tower.room.Room;
import ulb.models.utils.Position;
import ulb.models.tower.room.RoomVisitor;

public class FloorNode {
    private final Position position;
    private final int depth;
    private final List<FloorNode> children;
    private final Optional<FloorNode> parent;
    private Room room;

    public FloorNode(Position position, Room room, List<FloorNode> children, FloorNode parent, int depth) {
        this.position = position;
        this.depth = depth;
        this.room = room;
        this.children = children;
        this.parent = Optional.ofNullable(parent);
    }

    public boolean hasPlayerWon() {
        return this.room.hasPlayerWon();
    }

    // Getters
    public int getX() {
        return this.position.x();
    }

    public int getY() {
        return this.position.y();
    }

    public RoomPosition getPosition() {
        return new RoomPosition(this.position.x(), this.position.y());
    }

    public int getDepth() {
        return this.depth;
    }

    public Room getRoom() {
        return this.room;
    }

    public List<FloorNode> getChildren() {
        return this.children;
    }

    public Optional<FloorNode> getParent() {
        return this.parent;
    }

    public void addChild(FloorNode child) {
        this.children.add(child);
    }

    public int getBranchCount() {
        FloorNode n = this;
        while (n.getDepth() > 1 && n.getParent().isPresent()) {
            n = n.getParent().orElseThrow();
        }
        return this.countSubtree(n);
    }

    // Setters

    public void setRoom(Room room) {
        this.room = room;
    }

    // Private

    private int countSubtree(FloorNode n) {
        int count = 1;
        for (FloorNode child : n.getChildren()) {
            count += this.countSubtree(child);
        }
        return count;
    }

    // Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FloorNode other = (FloorNode) obj;
        return this.getPosition() == other.getPosition();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.position.x(), this.position.y());
    }

    public void visitIfNotVisited(RoomVisitor roomVisitor) {
        this.room.visitIfNotVisited(roomVisitor);
    }

    public record RoomPosition(int row, int col) {
    }
}
