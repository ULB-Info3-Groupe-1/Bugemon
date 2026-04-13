package ulb.models.tower;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ulb.models.tower.room.Room;

/**
 * A node in the floor's tree structure, holding grid coordinates, its {@link Room}, and links to parent and children.
 */
public class FloorNode {
    private final int x;
    private final int y;
    private final int depth;
    private final List<FloorNode> children;
    private final Optional<FloorNode> parent;
    private Room room;

    public FloorNode(int x, int y, Room room, List<FloorNode> children, FloorNode parent, int depth) {
        this.x = x;
        this.y = y;
        this.depth = depth;
        this.room = room;
        this.children = children;
        this.parent = Optional.ofNullable(parent);
    }

    // Getters
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public RoomPosition getPosition() {
        return new RoomPosition(this.x, this.y);
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

    /**
     * Returns the total number of nodes in the subtree whose root is at depth 1 on this node's branch. Used by the
     * floor generator to limit branch sprawl.
     */
    public int getBranchCount() {
        FloorNode n = this;
        while (n.getDepth() > 1 && n.getParent().isPresent()) {
            n = n.getParent().get();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloorNode other)) {
            return false;
        }
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public record RoomPosition(int row, int col) {
    }
}
