package ulb.controllers;

import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;
import ulb.views.utils.Node;

/** Controller for the skill tree screen. Hardcoded tree for now. */
public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    public SkillTreeController(MetaController metaController) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.view.setListener(this);
    }

    @Override
    public void show() {
        this.view.renderTree(this.buildDummyTree());
        super.show();
    }

    @Override
    public void onSkillClicked(Node node) {
        // TODO: handle skill selection
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }

    private Node buildDummyTree() {
        // AI hardcoded Dummy tree to test <3
        Node root = new Node("Combat", null);

        Node attack = new Node("Attaque", root);
        Node defense = new Node("Défense", root);
        Node speed = new Node("Vitesse", root);
        root.addChild(attack);
        root.addChild(defense);
        root.addChild(speed);

        Node superAttack = new Node("Super Attaque", attack);
        Node criticalHit = new Node("Coup Critique", attack);
        attack.addChild(superAttack);
        attack.addChild(criticalHit);

        Node shield = new Node("Bouclier", defense);
        defense.addChild(shield);

        Node dodge = new Node("Esquive", speed);
        Node dash = new Node("Dash", speed);
        speed.addChild(dodge);
        speed.addChild(dash);

        return root;
    }
}
