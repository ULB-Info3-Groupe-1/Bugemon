package ulb.controllers;

import ulb.models.skills.SkillNode;
import ulb.models.player.PlayerState;
import ulb.models.skills.exceptions.IllegalNodeStateException;
import ulb.services.SkillService;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;

/** Controller for the skill tree screen. */
public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    private final SkillService skillService;
    private final PlayerState playerState;

    public SkillTreeController(MetaController metaController, SkillService skillService, PlayerState playerState) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.view.setListener(this);
        this.skillService = skillService;
        this.playerState = playerState;
    }

    @Override
    public void show() {
        this.view.setAvailablePoints(this.playerState.getSkillTreeState().getSkillPoints());
        this.view.renderTree(this.skillService.getSkillTree(), this.playerState.getSkillTreeState());
        super.show();
    }

    @Override
    public void onSkillLeftClicked(SkillNode node) {
        try {
            this.skillService.addPoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(),
                    node.id());
            this.view.setAvailablePoints(this.playerState.getSkillTreeState().getSkillPoints());
            this.view.refresh();
        } catch (IllegalNodeStateException e) {
            // Invalid click or not enough points: keep the current tree as-is.
        }
    }

    @Override
    public void onSkillRightClicked(SkillNode node) {
        try {
            this.skillService.removePoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(),
                    node.id());
            this.view.setAvailablePoints(this.playerState.getSkillTreeState().getSkillPoints());
            this.view.refresh();
        } catch (IllegalNodeStateException e) {
            // Invalid click or locked dependency: keep the current tree as-is.
        }
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
