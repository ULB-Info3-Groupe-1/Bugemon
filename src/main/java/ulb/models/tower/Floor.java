package ulb.models.tower;

import java.util.List;
import java.util.stream.Stream;

import ulb.models.tower.room.Room;

public class Floor {
    private FloorNode currentNode;
    private final FloorNode rootNode;

    public Floor(FloorNode rootNode) {
        this(rootNode, rootNode);
    }

    public Floor(FloorNode currentNode, FloorNode rootNode) {
        this.currentNode = currentNode;
        this.rootNode = rootNode;
    }

    public FloorNode getRoot() {
        return this.rootNode;
    }

    public List<FloorNode> getReachableNodes() {
        return Stream.concat(this.currentNode.getChildren().stream(), this.currentNode.getParent().stream()).toList();
    }

    public List<Room> getReachableRooms() {
        return this.getReachableNodes().stream().map(FloorNode::getRoom).toList();
    }

    public void movePlayerTo(FloorNode node) {
        if (this.checkCanMoveTo(node)) {
            this.currentNode = node;
        }
    }

    public boolean checkCanMoveTo(FloorNode node) {
        return this.getReachableNodes().contains(node);
    }

    public FloorNode getCurrentNode() {
        return this.currentNode;
    }

    public Room getCurrentRoom() {
        return this.currentNode.getRoom();
    }
}
