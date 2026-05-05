package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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
        this.renderNode(root);
    }

    private void renderNode(Node node) {
        double pixelX = node.x * (NODE_WIDTH + HORIZONTAL_SPACING);
        double pixelY = node.y * (NODE_HEIGHT + VERTICAL_SPACING);

        // seen as a circle
        HBox skillBox = new HBox();
        skillBox.getStyleClass().add("hover-info-panel");

        // adding data
        Label label = new Label(node.getData());
        label.getStyleClass().add("section-label");

        // placing it properly
        StackPane skillNode = new StackPane(skillBox, label);
        skillNode.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        skillNode.setLayoutX(pixelX);
        skillNode.setLayoutY(pixelY);
        skillNode.getStyleClass().add("skill-node");
        skillNode.setOnMouseClicked(e -> {
            if (this.listener != null) {
                this.listener.onSkillClicked(node);
            }
        });

        this.innerMapPane.getChildren().add(skillNode);

        for (Node child : node.getChildren()) {
            renderNode(child);
        }
    }

    public interface Listener {
        void onSkillClicked(Node node);

        void onReturnToMainMenu();
    }

}
