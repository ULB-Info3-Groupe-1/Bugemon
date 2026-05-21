package ulb.controllers;

import ulb.models.skills.SkillNode;
import ulb.services.PlayerService;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;

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
        this.view.setAvailablePoints(this.playerService.getAvailableSkillPoints());
        this.view.renderTree(this.playerService.getSkillTreeRoot());
        super.show();
    }

    @Override
    public void onSkillLeftClicked(SkillNode node) {
        // Add a point
        // TODO: handle skill selection and apply effects
        if (this.playerService.unlockSkill(node)) {
            this.view.setAvailablePoints(this.playerService.getAvailableSkillPoints());
            this.view.refresh();
        }
    }

    @Override
    public void onSkillRightClicked(SkillNode node) {
        // Delete a point
        this.playerService.refundSkillNode(node);
        this.view.setAvailablePoints(this.playerService.getAvailableSkillPoints());
        this.view.refresh();
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
