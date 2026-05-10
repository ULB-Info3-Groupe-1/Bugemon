package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.views.utils.Node;
import ulb.views.utils.TreeLayout;

public class SkillTreeView extends View {
    // Skill:
    // Click on a skill node
    // Use an algorithm to dispatch properly the node...
    // Node should have colors and unlocked or not...
    // Drawing the link between node ? Spline or straight line ?? Reingold–Tilford layout

    private static final double NODE_WIDTH = 120.0;
    private static final double NODE_HEIGHT = 80.0;
    private static final double HORIZONTAL_SPACING = 50.0;
    private static final double VERTICAL_SPACING = 100.0;

    final double[] lastMouse = new double[2];

    @FXML
    private StackPane mapContainer;

    @FXML
    private Pane innerMapPane;

    @FXML
    private Label availablePoints;

    private Listener listener;
    private Node treeRoot;
    private int availablePointsCount = 0;

    @FXML
    public void initialize() {
        // Those method are an AI solution from multiple forum adaptation
        // I could have used ScrollPane but add also some struggle and It's the
        // best version/solution so far that I have found!
        this.mapContainer.setOnScroll(e -> {
            if (e.getDeltaY() == 0) {
                return;
            }

            double oldScale = this.mapContainer.getScaleX();

            double zoomFactor;
            if (e.getDeltaY() > 0) {
                zoomFactor = 1.1;
            } else {
                zoomFactor = 0.9;
            }

            double newScale = oldScale * zoomFactor;
            newScale = Math.max(0.2, Math.min(5.0, newScale));

            this.mapContainer.setScaleX(newScale);
            this.mapContainer.setScaleY(newScale);

            e.consume();
        });

        this.mapContainer.setOnMousePressed(e -> {
            this.lastMouse[0] = e.getSceneX();
            this.lastMouse[1] = e.getSceneY();
        });

        this.mapContainer.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - this.lastMouse[0];
            double dy = e.getSceneY() - this.lastMouse[1];

            this.mapContainer.setTranslateX(this.mapContainer.getTranslateX() + dx);
            this.mapContainer.setTranslateY(this.mapContainer.getTranslateY() + dy);

            this.lastMouse[0] = e.getSceneX();
            this.lastMouse[1] = e.getSceneY();

            e.consume();
        });
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SKILL_TREE_VIEW;
    }

    @Override
    public void refresh() {
        if (this.treeRoot == null) {
            return;
        }
        this.doRender();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // temporary placeholder — replace once PlayerService exposes available skill points
    public void setAvailablePoints(int count) {
        this.availablePointsCount = count;
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        if (this.listener != null) {
            this.listener.onReturnToMainMenu();
        }
    }

    public void renderTree(Node root) {
        this.treeRoot = root;
        this.doRender();
    }

    private void doRender() {
        this.innerMapPane.getChildren().clear();
        this.availablePoints.setText("Points disponibles: " + this.availablePointsCount);

        TreeLayout.applyLayout(this.treeRoot);
        this.renderConnections(this.treeRoot);
        this.renderNode(this.treeRoot);

        float[] maxCoords = new float[]{0, 0};
        this.collectMaxCoords(this.treeRoot, maxCoords);

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

        Line join = new Line(parentCenterX, parentBottomY, childCenterX, childTopY);
        join.getStyleClass().add("skill-connection");

        if (parent.getState() == Node.NodeState.ACTIVE && child.getState() == Node.NodeState.ACTIVE) {
            join.getStyleClass().add("skill-connection-active");
        } else if (parent.getState() == Node.NodeState.ACTIVE) {
            join.getStyleClass().add("skill-connection-available");
        }

        this.innerMapPane.getChildren().add(join);
    }

    private void renderNode(Node node) {
        // seen as a box
        HBox skillBox = new HBox();
        skillBox.getStyleClass().add("action-menu");

        skillBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                this.listener.onSkillLeftClicked(node);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                this.listener.onSkillRightClicked(node);
            }
        });

        // adding data
        Label label = new Label(node.getData());
        label.getStyleClass().add("section-label");

        // placing it properly
        double pixelX = node.x * (NODE_WIDTH + HORIZONTAL_SPACING);
        double pixelY = node.y * (NODE_HEIGHT + VERTICAL_SPACING);

        StackPane skillNode = new StackPane(skillBox, label);
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setLayoutX(pixelX);
        skillNode.setLayoutY(pixelY);
        skillNode.getStyleClass().add("skill-node");
        skillNode.getStyleClass().add(this.stateClass(node.getState()));

        Tooltip description = new Tooltip(node.getDescription());
        description.setShowDelay(Duration.millis(300));
        Tooltip.install(skillNode, description);

        this.innerMapPane.getChildren().add(skillNode);

        for (Node child : node.getChildren()) {
            this.renderNode(child);
        }
    }

    String stateClass(Node.NodeState state) {
        return switch (state) {
            case ACTIVE -> "skill-node-active";
            case AVAILABLE -> "skill-node-available";
            case LOCKED -> "skill-node-locked";
        };
    }

    public interface Listener {
        void onSkillLeftClicked(Node node);

        void onSkillRightClicked(Node node);

        void onReturnToMainMenu();
    }

}
