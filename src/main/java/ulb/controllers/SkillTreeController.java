package ulb.controllers;

import ulb.models.player.PlayerState;
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
        // TODO: implement getSkillTree in the SkillService
        this.view.refresh(this.playerState.getSkillTreeState(), this.skillService.getSkillTree());
        super.show();
    }

    @Override
    public void onSkillLeftClicked(String nodeId) {
        // TODO: add point to corresponding node
        // TODO: refresh skillTree
    }

    @Override
    public void onSkillRightClicked(String nodeId) {
        // TODO: remove point to corresponding node
        // TODO: refresh skillTree
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
