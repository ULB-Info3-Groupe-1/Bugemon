package ulb.controllers;

import ulb.models.player.PlayerState;
import ulb.models.skills.SkillNode;
import ulb.models.skills.exceptions.IllegalNodeStateException;
import ulb.services.SkillService;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;

public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    private SkillService skillService;
    private final PlayerState playerState;

    public SkillTreeController(MetaController metaController, SkillService skillService, PlayerState playerState) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.skillService = skillService;
        this.playerState = playerState;
        this.initialize();
    }

    public void initialize() {
        this.view.setListener(this);
        this.view.setTree(this.skillService.getSkillTree());
        this.view.refreshSkillState(this.skillService.getSkillTreeState());
    }

    @Override
    public void onSkillLeftClicked(SkillNode skillNode) {
        try {
            this.skillService.addPoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(),
                    skillNode.id());
            this.view.refreshSkillState(this.skillService.getSkillTreeState());
        } catch (IllegalNodeStateException e) {
            // Show alert or I fucking don't know <3
        }
    }

    @Override
    public void onSkillRightClicked(SkillNode skillNode) {
        try {
            this.skillService.removePoint(this.playerState.getSkillTreeState(), this.skillService.getSkillTree(),
                    skillNode.id());
            this.view.refreshSkillState(this.skillService.getSkillTreeState());
        } catch (IllegalNodeStateException e) {
            // Show alert or I fucking don't know <3
        }
    }

    @Override
    public void onReturnClicked() {
        this.metaController.onMainMenu();
    }
}
