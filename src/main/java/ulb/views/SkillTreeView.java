package ulb.views;

import java.util.List;
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
import ulb.models.skills.SkillStatus;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;

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
    private SkillTree tree;
    private SkillTreeState state;
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
        if (this.tree == null || this.state == null) {
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

    public void renderTree(SkillTree tree, SkillTreeState state) {
        this.tree = tree;
        this.state = state;
        this.doRender();
    }

    private int computeMinX(List<SkillNode> nodes) {
        int min = 0;
        for (SkillNode node : nodes) {
            min = Math.min(min, node.x());
        }
        return min;
    }

    private void doRender() {
        this.innerMapPane.getChildren().clear();
        this.availablePoints.setText("Points disponibles: " + this.availablePointsCount);

        if (this.tree == null || this.state == null) {
            return;
        }

        List<SkillNode> nodes = this.tree.getNodes();
        int minX = this.computeMinX(nodes);

        this.renderConnections(nodes, minX);
        this.renderNodes(nodes, minX);

        int[] max = {0, 0};
        this.collectMaxCoords(nodes, max);
        double treeWidth = (max[0] - minX + 1) * (NODE_WIDTH + HORIZONTAL_SPACING) - HORIZONTAL_SPACING;
        double treeHeight = (max[1] + 1) * (NODE_HEIGHT + VERTICAL_SPACING) - VERTICAL_SPACING;
        this.innerMapPane.setPrefSize(treeWidth, treeHeight);
        this.innerMapPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    private void collectMaxCoords(List<SkillNode> nodes, int[] maxCoords) {
        for (SkillNode node : nodes) {
            maxCoords[0] = Math.max(maxCoords[0], node.x());
            maxCoords[1] = Math.max(maxCoords[1], node.y());
        }
    }

    private void renderConnections(List<SkillNode> nodes, int minX) {
        for (SkillNode child : nodes) {
            for (String prerequisiteId : child.prerequisites()) {
                this.tree.findById(prerequisiteId).ifPresent(parent -> this.addConnection(parent, child, minX));
            }
        }
    }

    private void addConnection(SkillNode parent, SkillNode child, int minX) {
        double parentCenterX = (parent.x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double parentBottomY = parent.y() * (NODE_HEIGHT + VERTICAL_SPACING) + NODE_HEIGHT;
        double childCenterX = (child.x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING) + NODE_WIDTH / 2;
        double childTopY = child.y() * (NODE_HEIGHT + VERTICAL_SPACING);

        Line join = new Line(parentCenterX, parentBottomY, childCenterX, childTopY);
        join.getStyleClass().add("skill-connection");

        SkillStatus parentStatus = this.state.getStatus(parent.id(), this.tree);
        SkillStatus childStatus = this.state.getStatus(child.id(), this.tree);

        if (parentStatus == SkillStatus.ACTIVE && childStatus == SkillStatus.ACTIVE) {
            join.getStyleClass().add("skill-connection-active");
        } else if (parentStatus == SkillStatus.ACTIVE) {
            join.getStyleClass().add("skill-connection-available");
        }

        this.innerMapPane.getChildren().add(join);
    }

    private void renderNodes(List<SkillNode> nodes, int minX) {
        for (SkillNode node : nodes) {
            StackPane skillNode = this.buildSkillNode(node, minX);
            this.innerMapPane.getChildren().add(skillNode);
        }
    }

    private StackPane buildSkillNode(SkillNode node, int minX) {
        double pixelX = (node.x() - minX) * (NODE_WIDTH + HORIZONTAL_SPACING);
        double pixelY = node.y() * (NODE_HEIGHT + VERTICAL_SPACING);

        SkillStatus status = this.state.getStatus(node.id(), this.tree);

        StackPane skillNode = new StackPane(this.buildBackground(), this.buildContent(node));
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setLayoutX(pixelX);
        skillNode.setLayoutY(pixelY);
        skillNode.getStyleClass().add("skill-node");
        skillNode.getStyleClass().add(this.stateClass(status));

        if (status != SkillStatus.LOCKED) {
            skillNode.setOnMouseClicked(e -> {
                if (this.listener == null) {
                    return;
                }
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.listener.onSkillLeftClicked(node);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    this.listener.onSkillRightClicked(node);
                }
                e.consume();
            });
        }

        Tooltip tooltip = new Tooltip(node.description());
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
        Label name = new Label(node.name());
        name.getStyleClass().add("section-label");
        name.setWrapText(true);
        name.setMaxWidth(NODE_WIDTH - 8);

        Label info = new Label(this.state.getNodeLevel(node.id()) + "/" + node.maxLevel());
        Label cost = new Label(node.cost() + " pt");

        VBox content = new VBox(4, name, info, cost);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setMouseTransparent(true);
        return content;
    }

    String stateClass(SkillStatus state) {
        return switch (state) {
            case ACTIVE -> "skill-node-active";
            case AVAILABLE -> "skill-node-available";
            case LOCKED -> "skill-node-locked";
        };
    }

    public interface Listener {
        void onSkillLeftClicked(SkillNode node);

        void onSkillRightClicked(SkillNode node);

        void onReturnToMainMenu();
    }

}
