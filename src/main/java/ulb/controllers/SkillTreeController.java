package ulb.controllers;

import java.util.List;

import ulb.models.skills.SkillNode;
import ulb.services.PlayerService;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;
import ulb.views.utils.Node;

/** Controller for the skill tree screen. */
public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    private final PlayerService playerService;

    public SkillTreeController(MetaController metaController, PlayerService playerService) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.view.setListener(this);
        this.playerService = playerService;
    }

    @Override
    public void show() {
        this.view.renderTree(this.buildTree());
        super.show();
    }

    @Override
    public void onSkillClicked(Node node) {
        // TODO: handle skill selection and apply effects
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }

    private Node buildTree() {
        List<SkillNode> skillNodes = this.playerService.getSkillTree();
        SkillNode rootNode = skillNodes.stream().filter(n -> "start".equals(n.getSkill().getId())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Start node is missing"));

        return this.buildViewNode(rootNode, null);
    }

    private Node buildViewNode(SkillNode currentNode, Node parentView) {
        String name = currentNode.getSkill().getName();
        String description = currentNode.getSkill().getDescription();

        Node currentView = new Node(name, description, parentView);
        if (parentView != null) {
            parentView.addChild(currentView);
        }
        for (SkillNode childNode : currentNode.getChildren()) {
            this.buildViewNode(childNode, currentView);
        }
        return currentView;
    }
}
