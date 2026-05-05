package ulb.views.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This class has for purpose to properly layout a tree
// using the Reingold-Tilford Algorithm <3
// Maybe a static method or idk will more suitable
// It's obviously a prototype and in current work.
// https://rachel53461.wordpress.com/2014/04/20/algorithm-for-drawing-trees/ helped me so much to properly understand the algorithm it's not the most optimized and best though algorithm but will do the work
public class TreeLayout {

    // Never instanciate
    private TreeLayout() {
    }

    public static void applyLayout(Node root) {
        // post-order pass
        calculateInitialX(root);

        // avoid node on negative X value
        checkAllChildrenOnScreen(root);

        // apply mod values
        calculateFinalX(root, 0);
    }

    private static void calculateInitialX(Node node) {
        for (Node child : node.getChildren()) {
            calculateInitialX(child);
        }

        if (!node.isLeftMost()) {
            node.x = node.getPreviousSibling().x + 1;
        } else {
            node.x = 0;
        }

        // computing the mod value and midpoint point
        if (!node.isLeaf()) {
            float midpoint = computeMidPoint(node);

            if (node.isLeftMost()) {
                node.x = midpoint;
            } else {
                node.mod = node.x - midpoint;
            }
        }

        if (!node.isLeftMost()) {
            checkForConflicts(node);
        }
    }

    private static float computeMidPoint(Node node) {
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

    // Check if any node overlap
    private static void checkForConflicts(Node node) {
        float minDistance = 1.0f; // Should be later define with the max width of the node visually...
        float shiftValue = 0.0f;

        Map<Integer, Float> leftContour = new HashMap<>();
        getContour(node, 0, leftContour, true);

        Node sibling = node.getPreviousSibling();
        while (sibling != null) {
            Map<Integer, Float> rightContour = new HashMap<>();
            getContour(sibling, 0, rightContour, false);

            for (int depth : leftContour.keySet()) {
                if (rightContour.containsKey(depth)) {
                    float overlap = rightContour.get(depth) + minDistance - leftContour.get(depth);
                    shiftValue = Math.max(shiftValue, overlap);
                }
            }

            sibling = sibling.getPreviousSibling();
        }

        // Shifting
        if (shiftValue > 0) {
            node.x += shiftValue;
            node.mod += shiftValue;

            // Maybe think about distribute evenly the shift value
            // but grosse flemme obviously... T^T
        }
    }

    private static void checkAllChildrenOnScreen(Node node) {
        Map<Integer, Float> leftContour = new HashMap<>();
        getContour(node, 0, leftContour, true);

        float shiftAmount = 0;
        for (int depth : leftContour.keySet()) {
            if (leftContour.get(depth) + shiftAmount < 0) {
                shiftAmount = -leftContour.get(depth);
            }
        }

        if (shiftAmount > 0) {
            node.x += shiftAmount;
            node.mod += shiftAmount;
        }
    }

    private static void calculateFinalX(Node node, float modSum) {
        node.x += modSum;
        modSum += node.mod;

        for (Node child : node.getChildren()) {
            calculateFinalX(child, modSum);
        }
    }

    private static void getContour(Node node, float modSum, Map<Integer, Float> contour, boolean leftContour) {
        int depth = (int) node.y;
        float finalX = node.x + modSum; // computing as it was the final x pos

        if (!contour.containsKey(depth)) {
            contour.put(depth, finalX);
        } else if (leftContour && finalX < contour.get(depth)) { // left
            contour.put(depth, finalX);
        } else if (!leftContour && finalX > contour.get(depth)) { // right
            contour.put(depth, finalX);
        }

        for (Node child : node.getChildren()) {
            getContour(child, modSum + node.mod, contour, leftContour);
        }
    }
}
