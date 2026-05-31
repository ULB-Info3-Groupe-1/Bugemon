package ulb.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import ulb.Configuration;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillStatus;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;

/**
 * View for the skill tree screen. Renders {@link ulb.models.skills.SkillNode} widgets on an absolute-positioned
 * {@link Pane} using pre-computed (x, y) coordinates, draws prerequisite connection lines between them, and forwards
 * left-click (unlock) and right-click (downgrade) events through {@link Listener}.
 */
public class SkillTreeView extends View {

    private static final int NODE_WIDTH = 160;
    private static final int NODE_HEIGHT = 80;
    private static final int CELL_W = 240;
    private static final int CELL_H = 170;
    private static final int PADDING = 60;

    private static final String SECTION_LABEL = "section-label";

    private SkillTree skillTree;

    private Listener listener;

    @FXML
    private Label availablePoints;

    @FXML
    private StackPane mapContainer;

    @FXML
    private Pane innerMapPane;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SKILL_TREE_VIEW;
    }

    @Override
    public void refresh() {
        // Nothing to refresh
    }

    /**
     * Rebuilds the entire skill tree canvas to reflect the given skill state and updates the available-points counter.
     *
     * @param state
     *            current skill tree state including point count and per-node levels
     */
    public void refreshSkillState(SkillTreeState state) {
        this.availablePoints.setText("Points disponibles : " + state.getSkillPoints());
        this.buildTree(state);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Stores the skill tree structure used by {@link #refreshSkillState(SkillTreeState)} to look up node coordinates
     * and prerequisites.
     *
     * @param tree
     *            the skill tree definition
     */
    public void setTree(SkillTree tree) {
        this.skillTree = tree;
    }

    @FXML
    private void onReturnClicked() {
        this.listener.onReturnClicked();
    }

    private void buildTree(SkillTreeState skillTreeState) {
        this.innerMapPane.getChildren().clear();
        var nodes = this.skillTree.getNodes();
        var xStats = nodes.stream().mapToInt(SkillNode::x).summaryStatistics();
        var yStats = nodes.stream().mapToInt(SkillNode::y).summaryStatistics();

        int minX = xStats.getMin();
        int maxX = xStats.getMax();
        int minY = yStats.getMin();
        int maxY = yStats.getMax();

        int rangeX = maxX - minX;
        int rangeY = maxY - minY;

        double paneW = (double) (rangeX + 1) * CELL_W + PADDING * 2;
        double paneH = (double) (rangeY + 1) * CELL_H + PADDING * 2;
        this.innerMapPane.setPrefSize(paneW, paneH);
        this.innerMapPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Map<String, StackPane> nodePanes = new HashMap<>();

        for (SkillNode node : nodes) {
            SkillStatus status = skillTreeState.getStatus(node.id(), this.skillTree);
            int level = skillTreeState.getNodeLevel(node.id());

            StackPane nodePane = this.buildSkillNode(node, status, level, minX, minY);
            nodePanes.put(node.id(), nodePane);
        }

        this.addConnections(nodePanes, nodes, skillTreeState);

        for (StackPane nodePane : nodePanes.values()) {
            this.innerMapPane.getChildren().add(nodePane);
        }
    }

    private StackPane buildSkillNode(SkillNode node, SkillStatus status, int level, int minX, int minY) {
        Label nameLabel = new Label(node.name());
        nameLabel.getStyleClass().add(SECTION_LABEL);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth((double) NODE_WIDTH - 8);
        nameLabel.setAlignment(Pos.CENTER);

        Label levelLabel = new Label(level + " / " + node.maxLevel());
        levelLabel.getStyleClass().add(SECTION_LABEL);

        Label costLabel = new Label(node.cost() + " pt(s)");
        costLabel.getStyleClass().add(SECTION_LABEL);

        VBox content = new VBox(4, nameLabel, new HBox(NODE_WIDTH / 4.0, levelLabel, costLabel));
        content.setAlignment(Pos.CENTER);
        content.setMouseTransparent(true);

        HBox actionMenu = new HBox(content);
        actionMenu.getStyleClass().add("action-menu");
        actionMenu.setAlignment(Pos.CENTER);
        actionMenu.setPrefWidth(NODE_WIDTH);
        actionMenu.setPrefHeight(NODE_HEIGHT);

        StackPane skillNode = new StackPane(actionMenu);
        skillNode.getStyleClass().add("skill-node");
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setMinSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setMaxSize(NODE_WIDTH, NODE_HEIGHT);
        this.applyStatusStyle(skillNode, status);
        skillNode.setLayoutX(this.nodeX(node, minX));
        skillNode.setLayoutY(this.nodeY(node, minY));

        Tooltip.install(skillNode, new Tooltip(this.buildTooltip(node)));

        skillNode.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                this.listener.onSkillLeftClicked(node);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                this.listener.onSkillRightClicked(node);
            }
            e.consume();
        });

        return skillNode;
    }

    private void addConnections(Map<String, StackPane> nodePanes, List<SkillNode> nodes, SkillTreeState state) {
        Set<String> connectedNodes = new HashSet<>();

        for (SkillNode node : nodes) {
            for (SkillNode prerequisite : this.skillTree.getDependents(node.id())) {
                String connectionId = node.id() + "-" + prerequisite.id();

                if (!connectedNodes.contains(connectionId)) {
                    this.addLine(nodePanes.get(prerequisite.id()), nodePanes.get(node.id()),
                            state.getStatus(node.id(), this.skillTree));
                    connectedNodes.add(connectionId);
                }
            }
        }
    }

    private void addLine(StackPane child, StackPane parent, SkillStatus childStatus) {
        double x1 = parent.getLayoutX() + NODE_WIDTH / 2.0;
        double y1 = parent.getLayoutY() + NODE_HEIGHT;

        double x2 = child.getLayoutX() + NODE_WIDTH / 2.0;
        double y2 = child.getLayoutY();

        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("skill-connection");
        if (childStatus == SkillStatus.ACTIVE) {
            line.getStyleClass().add("skill-connection-active");
        } else if (childStatus == SkillStatus.AVAILABLE) {
            line.getStyleClass().add("skill-connection-available");
        }

        this.innerMapPane.getChildren().add(line);
    }

    private void applyStatusStyle(StackPane widget, SkillStatus status) {
        widget.getStyleClass().removeIf(c -> c.startsWith("skill-node-"));
        widget.getStyleClass().add(switch (status) {
            case ACTIVE -> "skill-node-active";
            case AVAILABLE -> "skill-node-available";
            case LOCKED -> "skill-node-locked";
        });
    }

    private String buildTooltip(SkillNode node) {
        return node.name() + "\n" + node.description() + "\n" + "Coût : " + node.cost() + " pt(s) | Max : "
                + node.maxLevel() + " niv.";
    }

    private double nodeX(SkillNode node, int minX) {
        return (node.x() - minX) * CELL_W + PADDING + (CELL_W - NODE_WIDTH) / 2.0;
    }

    private double nodeY(SkillNode node, int minY) {
        return (node.y() - minY) * CELL_H + PADDING + (CELL_H - NODE_HEIGHT) / 2.0;
    }

    /** Callback interface for skill tree interactions. */
    public interface Listener {
        /**
         * Called when the player right-clicks a skill node (downgrade / refund).
         *
         * @param skillNode
         *            the node that was right-clicked
         */
        void onSkillRightClicked(SkillNode skillNode);

        /**
         * Called when the player left-clicks a skill node (unlock / upgrade).
         *
         * @param skillNode
         *            the node that was left-clicked
         */
        void onSkillLeftClicked(SkillNode skillNode);

        /** Called when the player navigates back from the skill tree. */
        void onReturnClicked();
    }
}
