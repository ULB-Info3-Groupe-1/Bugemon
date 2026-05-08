package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

import ulb.Configuration;
import ulb.views.utils.Node;
import ulb.views.utils.TreeLayout;

public class SkillTreeView extends View {
    // Skill:
    // Click on a skill node
    // Use an algorithm to dispatch properly the node...
    // Node should have colors and unlocked or not...
    // Drawing the link between node ? Spline or straight line ?? Reingold–Tilford layout

    private static final double NODE_WIDTH = 160.0;
    private static final double NODE_HEIGHT = 100.0;
    private static final double HORIZONTAL_SPACING = 100.0;
    private static final double VERTICAL_SPACING = 100.0;

    @FXML
    private StackPane mapContainer;

    @FXML
    private Pane innerMapPane;

    private Listener listener;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SKILL_TREE_VIEW;
    }

    @Override
    public void refresh() {

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        if (this.listener != null) {
            this.listener.onReturnToMainMenu();
        }
    }

    public void renderTree(Node root) {
        TreeLayout.applyLayout(root);
        this.renderConnections(root);
        this.renderNode(root);

        // AI fix to place the screen at the center
        float[] maxCoords = new float[] { 0, 0 };
        this.collectMaxCoords(root, maxCoords);
        double treeWidth = (maxCoords[0] + 1) * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
        double treeHeight = (maxCoords[1] + 1) * (NODE_HEIGHT + VERTICAL_SPACING) - VERTICAL_SPACING;
        this.innerMapPane.setPrefSize(treeWidth, treeHeight);
        this.innerMapPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    // AI fix to place the tree at the center
    private void collectMaxCoords(Node node, float[] maxCoords) {
        maxCoords[0] = Math.max(maxCoords[0], node.x);
        maxCoords[1] = Math.max(maxCoords[1], node.y);
        for (Node child : node.getChildren()) {
            this.collectMaxCoords(child, maxCoords);
        }
    }

    private void renderConnections(Node node) {
        for (Node child : node.getChildren()) {
            this.addConnection(node, child);
            this.renderConnections(child);
        }
    }

    private void addConnection(Node parent, Node child) {
        double parentCenterX = parent.x * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double parentBottomY = parent.y * (NODE_HEIGHT + VERTICAL_SPACING) + NODE_HEIGHT;
        double childCenterX = child.x * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double childTopY = child.y * (NODE_HEIGHT + VERTICAL_SPACING);
        double midY = (parentBottomY + childTopY) / 2;

        // parent and child on the same x axis (straight line)
        if (Double.compare(parent.x, child.x) == 0) {
            Line line = new Line(parentCenterX, parentBottomY, childCenterX, childTopY);
            line.getStyleClass().add("skill-connection");
            this.innerMapPane.getChildren().add(line);
        } else { // 3 line from bottom parent to half height and from top child to half heigth
            Line vertParent = new Line(parentCenterX, parentBottomY, parentCenterX, midY);
            Line horizontal = new Line(parentCenterX, midY, childCenterX, midY);
            Line vertChild = new Line(childCenterX, midY, childCenterX, childTopY);

            // TODO: handling the style
            vertParent.getStyleClass().add("skill-connection");
            horizontal.getStyleClass().add("skill-connection");
            vertChild.getStyleClass().add("skill-connection");

            this.innerMapPane.getChildren().addAll(vertParent, horizontal, vertChild);
        }
    }

    private void renderNode(Node node) {
        double pixelX = node.x * (NODE_WIDTH + HORIZONTAL_SPACING);
        double pixelY = node.y * (NODE_HEIGHT + VERTICAL_SPACING);

        // seen as a box
        HBox skillBox = new HBox();
        skillBox.getStyleClass().add("action-menu");
        skillBox.setOnMouseClicked(e -> {
            this.listener.onSkillClicked(node);
        });

        // adding data
        Label label = new Label(node.getData());
        label.getStyleClass().add("section-label");

        // placing it properly
        StackPane skillNode = new StackPane(skillBox, label);
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setLayoutX(pixelX);
        skillNode.setLayoutY(pixelY);
        skillNode.getStyleClass().add("skill-node"); // TODO: add styling <3

        this.innerMapPane.getChildren().add(skillNode);

        for (Node child : node.getChildren()) {
            this.renderNode(child);
        }
    }

    public interface Listener {
        void onSkillClicked(Node node);

        void onReturnToMainMenu();
    }

}
