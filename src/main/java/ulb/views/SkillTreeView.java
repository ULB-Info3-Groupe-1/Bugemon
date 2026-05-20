package ulb.views;

import java.util.HashSet;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillNodeState;

public class SkillTreeView extends View {
    // Skill:
    // Click on a skill node
    // Use an algorithm to dispatch properly the node...
    // Node should have colors and unlocked or not...
    // Drawing the link between node ? Spline or straight line ?? Reingold–Tilford
    // layout

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
    private SkillNode treeRoot;
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

    public void setAvailablePoints(int count) {
        this.availablePointsCount = count;
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        if (this.listener != null) {
            this.listener.onReturnToMainMenu();
        }
    }

    public void renderTree(SkillNode root) {
        this.treeRoot = root;
        this.doRender();
    }

    private int computeMinX(SkillNode node) {
        int min = node.getPosition().x();
        for (SkillNode child : node.getChildren()) {
            min = Math.min(min, this.computeMinX(child));
        }
        return min;
    }

    private void doRender() {
        this.innerMapPane.getChildren().clear();
        this.availablePoints.setText("Points disponibles: " + this.availablePointsCount);

        int minX = this.computeMinX(this.treeRoot);

        Set<SkillNode> visited = new HashSet<>();
        this.renderConnections(this.treeRoot, minX, visited);
        visited.clear();
        this.renderNode(this.treeRoot, minX, visited);

        int[] max = {0, 0};
        this.collectMaxCoords(this.treeRoot, max);
        double treeWidth = (max[0] - minX + 1) * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
        double treeHeight = (max[1] + 1) * (NODE_HEIGHT + VERTICAL_SPACING) - VERTICAL_SPACING;
        this.innerMapPane.setPrefSize(treeWidth, treeHeight);
        this.innerMapPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    // AI fix to place the tree at the center
    private void collectMaxCoords(SkillNode node, int[] maxCoords) {
        maxCoords[0] = Math.max(maxCoords[0], node.getPosition().x());
        maxCoords[1] = Math.max(maxCoords[1], node.getPosition().y());
        for (SkillNode child : node.getChildren()) {
            this.collectMaxCoords(child, maxCoords);
        }
    }

    private void renderConnections(SkillNode node, int minX, Set<SkillNode> visited) {
        if (!visited.add(node)) {
            return;
        }
        for (SkillNode child : node.getChildren()) {
            this.addConnection(node, child, minX);
            this.renderConnections(child, minX, visited);
        }
    }

    private void addConnection(SkillNode parent, SkillNode child, int minX) {
        double parentCenterX = (parent.getPosition().x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double parentBottomY = parent.getPosition().y() * (NODE_HEIGHT + VERTICAL_SPACING) + NODE_HEIGHT;
        double childCenterX = (child.getPosition().x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double childTopY = child.getPosition().y() * (NODE_HEIGHT + VERTICAL_SPACING);

        Line join = new Line(parentCenterX, parentBottomY, childCenterX, childTopY);
        join.getStyleClass().add("skill-connection");

        if (parent.getState() == SkillNodeState.ACTIVE && child.getState() == SkillNodeState.ACTIVE) {
            join.getStyleClass().add("skill-connection-active");
        } else if (parent.getState() == SkillNodeState.ACTIVE) {
            join.getStyleClass().add("skill-connection-available");
        }

        this.innerMapPane.getChildren().add(join);
    }

    private void renderNode(SkillNode node, int minX, Set<SkillNode> visited) {
        if (!visited.add(node)) {
            return;
        }
        StackPane skillNode = this.buildSkillNode(node, minX);
        this.innerMapPane.getChildren().add(skillNode);

        for (SkillNode child : node.getChildren()) {
            this.renderNode(child, minX, visited);
        }
    }

    private StackPane buildSkillNode(SkillNode node, int minX) {
        double pixelX = (node.getPosition().x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING);
        double pixelY = node.getPosition().y() * (NODE_HEIGHT + VERTICAL_SPACING);

        StackPane skillNode = new StackPane(this.buildBackground(), this.buildContent(node));
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setLayoutX(pixelX);
        skillNode.setLayoutY(pixelY);
        skillNode.getStyleClass().add("skill-node");
        skillNode.getStyleClass().add(this.stateClass(node.getState()));

        if (node.getState() != SkillNodeState.LOCKED) {
            skillNode.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.listener.onSkillLeftClicked(node);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    this.listener.onSkillRightClicked(node);
                }
                e.consume();
            });
        }

        Tooltip tooltip = new Tooltip(node.getDescription());
        tooltip.setShowDelay(Duration.millis(300));
        Tooltip.install(skillNode, tooltip);

        return skillNode;
    }

    private HBox buildBackground() {
        HBox bg = new HBox();
        bg.getStyleClass().add("action-menu");
        return bg;
    }

    private VBox buildContent(SkillNode node) {
        Label name = new Label(node.getName());
        name.getStyleClass().add("section-label");
        name.setWrapText(true);
        name.setMaxWidth(NODE_WIDTH - 8);

        Label info = new Label(node.getCurrentLevel() + "/" + node.getMaxLevel());
        Label cost = new Label(node.getCost() + " pt");

        VBox content = new VBox(4, name, info, cost);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setMouseTransparent(true);
        return content;
    }

    String stateClass(SkillNodeState state) {
        return switch (state) {
            case ACTIVE -> "skill-node-active";
            case AVAILABLE -> "skill-node-available";
            case LOCKED -> "skill-node-locked";
        };
    }

    public interface Listener {
        void onSkillLeftClicked(String nodeId);

        void onSkillRightClicked(String nodeId);

        void onReturnToMainMenu();
    }

}
