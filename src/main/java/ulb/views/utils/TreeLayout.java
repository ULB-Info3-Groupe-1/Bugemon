package ulb.views.utils;

import java.util.ArrayList;

// This class has for purpose to properly layout a tree
// using the Reingold-Tilford Algorithm <3
// Maybe a static method or idk will more suitable
// It's obviously a prototype and in current work.
// https://rachel53461.wordpress.com/2014/04/20/algorithm-for-drawing-trees/ helped me so much to properly understand the algorithm it's not the most optimized and best though algorithm but will do the work
public class TreeLayout {

    private Node root;

    public TreeLayout(Node root) {
        this.root = root;
        this.buildLayout();
    }

    private void buildLayout() {
        // post-order pass
        this.calculateInitialX(this.root);

        // TODO: pass 2 and pass 3 of the tree
    }

    private void calculateInitialX(Node node) {
        for (Node child : node.getChildren()) {
            this.calculateInitialX(child);
        }

        if (!node.isLeftMost()) {
            node.x = node.getPreviousSibling().x + 1;
        } else {
            node.x = 0;
        }

        // computing the mod value and midpoint point
        if (!node.isLeaf()) {
            float midpoint = this.computeMidPoint(node);

            if (node.isLeftMost()) {
                node.x = midpoint;
            } else {
                node.mod = node.x - midpoint;
            }
        }

        // TODO: overlapp sanity check
    }

    private float computeMidPoint(Node node) {
        // compute the mid point from its children...
        ArrayList<Node> children = node.getChildren();
        int childrenSize = children.size();

        if (childrenSize == 1) {
            return children.get(0).x;
        }

        float leftMostX = children.get(0).x;
        float rightMostX = children.get(childrenSize - 1).x;
        return (leftMostX + rightMostX) / 2;
    }
}
