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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import ulb.Configuration;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillStatus;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;

class SkillTreeView extends View {

    private static final int NODE_WIDTH = 120;
    private static final int NODE_HEIGHT = 60;
    private static final int CELL_W = 170;
    private static final int CELL_H = 130;
    private static final int PADDING = 40;

    private SkillTree skillTree;
    private Map<String, StackPane> nodeBoxs = new HashMap<>();

    private Listener listener;

    @FXML
    private Label availablePoints;

    @FXML
    private StackPane mapContainer;

    @FXML
    private Pane innerMap;

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SKILL_TREE_VIEW;
    }

    @Override
    public void refresh() {
    }

    public void refreshSkillState(SkillTreeState state) {
        availablePoints.setText(String.valueOf(state.getSkillPoints()));
        this.buildTree(state);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setTree(SkillTree skillTree) {
        this.skillTree = skillTree;
    }

    @FXML
    private void onReturnClicked() {
        this.listener.onReturnClicked();
    }

    public void buildTree(SkillTreeState skillTreeState) {
        var nodes = this.skillTree.getNodes();
        var xStats = nodes.stream().mapToInt(SkillNode::x).summaryStatistics();
        var yStats = nodes.stream().mapToInt(SkillNode::y).summaryStatistics();

        int minX = xStats.getMin();
        int maxX = xStats.getMax();
        int minY = yStats.getMin();
        int maxY = yStats.getMax();

        int rangeX = maxX - minX;
        int rangeY = maxY - minY;

        this.innerMap.setPrefWidth((rangeX + 1) * CELL_W + PADDING * 2);
        this.innerMap.setPrefHeight((rangeY + 1) * CELL_H + PADDING * 2);

        for (SkillNode node : nodes) {
            SkillStatus status = skillTreeState.getStatus(node.id(), this.skillTree);
            int level = skillTreeState.getNodeLevel(node.id());

            StackPane widget = this.buildSkillNode(node, status, level, minX, minY);
            this.nodeBoxs.put(node.id(), widget);
            this.innerMap.getChildren().add(widget);
        }

        this.addConnections(nodes, skillTreeState, minX, minY);
    }

    private StackPane buildSkillNode(SkillNode node, SkillStatus status, int level, int minX, int minY) {
        Label nameLabel = new Label(node.name());
        nameLabel.getStyleClass().add("section-label");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(NODE_WIDTH - 8);
        nameLabel.setAlignment(Pos.CENTER);

        Label levelLabel = new Label(level + " / " + node.maxLevel());
        levelLabel.getStyleClass().add("section-label");

        VBox content = new VBox(4, nameLabel, levelLabel);
        content.setAlignment(Pos.CENTER);
        content.setMouseTransparent(true);

        HBox actionMenu = new HBox(content);
        actionMenu.getStyleClass().add("action-menu");
        actionMenu.setAlignment(Pos.CENTER);
        actionMenu.setPrefWidth(NODE_WIDTH);
        actionMenu.setPrefHeight(NODE_HEIGHT);

        StackPane skillNode = new StackPane(actionMenu);
        skillNode.getStyleClass().add("skill-node");
        this.applyStatusStyle(skillNode, status);
        skillNode.setLayoutX(nodeX(node, minX));
        skillNode.setLayoutY(nodeY(node, minY));

        Tooltip.install(skillNode, new Tooltip(buildTooltip(node)));

        skillNode.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
                listener.onSkillLeftClicked(node);
            else if (e.getButton() == MouseButton.SECONDARY)
                listener.onSkillRightClicked(node);
            e.consume();
        });

        return skillNode;
    }

    private void addConnections(List<SkillNode> nodes, SkillTreeState state, int minX, int minY) {
        Set<String> connectedNodes = new HashSet<>();

        for (SkillNode node : nodes) {
            for (SkillNode prerequisite : this.skillTree.getDependents(node.id())) {
                String connectionId = node.id() + "-" + prerequisite.id();

                if (!connectedNodes.contains(connectionId)) {
                    this.addLine(node, prerequisite, minX, minY, state.getStatus(node.id(), this.skillTree));
                    connectedNodes.add(connectionId);
                }
            }
        }
    }

    private void addLine(SkillNode from, SkillNode to, int minX, int minY, SkillStatus fromStatus) {
        double x1 = nodeX(from, minX) + NODE_WIDTH / 2.0;
        double y1 = nodeY(from, minY) + NODE_HEIGHT / 2.0;
        double x2 = nodeX(to, minX) + NODE_WIDTH / 2.0;
        double y2 = nodeY(to, minY) + NODE_HEIGHT / 2.0;

        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("skill-connection");
        if (fromStatus == SkillStatus.ACTIVE) {
            line.getStyleClass().add("skill-connection-active");
        } else if (fromStatus == SkillStatus.AVAILABLE) {
            line.getStyleClass().add("skill-connection-available");
        }

        this.innerMap.getChildren().add(line);
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
        return (node.x() - minX) * CELL_W + PADDING;
    }

    private double nodeY(SkillNode node, int minY) {
        return (node.y() - minY) * CELL_H + PADDING;
    }

    public interface Listener {
        void onSkillRightClicked(SkillNode skillNode);

        void onSkillLeftClicked(SkillNode skillNode);

        void onReturnClicked();
    }
}
