package ulb.views.utils;

import java.util.ArrayList;

// Dummy class while waiting a skill node...
public class Node {

    public enum NodeState {
        ACTIVE,
        AVAILABLE,
        LOCKED
    }

    // Data struct
    private String data;
    private String description;
    private Node parent;
    private ArrayList<Node> children = new ArrayList<>();
    private NodeState state = NodeState.LOCKED;

    // Visual part for the layout
    // Public to avoid useless getter for now
    public float x;
    public float y;
    public float mod;

    public Node(String data, String description, Node parent) {
        this.data = data;
        this.description = description;
        this.parent = parent;

        // fix
        this.y = (this.parent == null) ? 0 : this.parent.y + 1;
    }

    public String getData() {
        return this.data;
    }

    public String getDescription() {
        return this.description;
    }

    public Node getParent() {
        return this.parent;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public NodeState getState() {
        return this.state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public boolean isLeftMost() {
        return this.parent == null || this.parent.getChildren().indexOf(this) == 0;
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public Node getPreviousSibling() {
        int siblingPosLeft = this.parent.getChildren().indexOf(this);
        if (siblingPosLeft == 0) {
            return null;
        }
        return this.parent.getChildren().get(siblingPosLeft - 1);
    }
}
