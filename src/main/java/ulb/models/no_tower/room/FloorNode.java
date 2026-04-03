package ulb.models.no_tower.room;

import java.util.List;
import java.util.Optional;

public class FloorNode {
    private final int x;
    private final int y;
    private final int depth;
    private Room room;
    private final List<FloorNode> children;
    private final Optional<FloorNode> parent;

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
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDepth() {
        return depth;
    }

    public Room getRoom() {
        return room;
    }

    public List<FloorNode> getChildren() {
        return children;
    }

    public Optional<FloorNode> getParent() {
        return parent;
    }

    public void addChild(FloorNode child) {
        this.children.add(child);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getBranchCount() {
        FloorNode n = this;
        while (n.getDepth() > 1 && n.getParent().isPresent()) {
            n = n.getParent().get();
        }
        return countSubtree(n);
    }

    private int countSubtree(FloorNode n) {
        int count = 1;
        for (FloorNode child : n.getChildren()) {
            count += countSubtree(child);
        }
        return count;
    }
}
