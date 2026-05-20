package ulb.controllers;

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
    public void onSkillLeftClicked(String skillId) {
        try {
            this.skillService.addPoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(), skillId);
        } catch (IllegalNodeStateException e) {

        }

        this.view.refresh(this.playerState.getSkillTreeState(), this.skillService.getSkillTree());

    }

    @Override
    public void onSkillRightClicked(String skillId) {
        try {
            this.skillService.removePoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(),
                    skillId);
        } catch (IllegalNodeStateException e) {

        }

        this.view.refresh(this.playerState.getSkillTreeState(), this.skillService.getSkillTree());
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
