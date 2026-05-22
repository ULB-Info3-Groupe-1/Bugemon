package ulb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.player.PlayerState;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTreeState;
import ulb.models.skills.exceptions.IllegalNodeStateException;
import ulb.services.SkillService;
import ulb.views.SkillTreeView;
import ulb.views.ViewLoader;

/**
 * Controller for the skill-tree screen.
 *
 * <p>
 * Left-clicking a {@link ulb.models.skills.SkillNode} spends a skill point to increase its level; right-clicking
 * refunds the point. Changes are persisted immediately after each interaction.
 */
public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(SkillTreeController.class);

    private SkillService skillService;
    private final PlayerState playerState;

    public SkillTreeController(MetaController metaController, SkillService skillService, PlayerState playerState) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.skillService = skillService;
        this.playerState = playerState;
        this.initialize();
    }

    @Override
    protected void show() {
        this.view.refreshSkillState(this.playerState.getSkillTreeState());
        super.show();
    }

    public void initialize() {
        this.view.setListener(this);
        this.view.setTree(this.skillService.getSkillTree());
        this.view.refreshSkillState(this.playerState.getSkillTreeState());
    }

    @Override
    public void onSkillLeftClicked(SkillNode skillNode) {
        try {
            SkillTreeState state = this.playerState.getSkillTreeState();
            this.skillService.addPoint(state, this.skillService.getSkillTree(), skillNode.id());
            this.skillService.save(state);
            this.view.refreshSkillState(state);
            LOG.info("Added point to skill node: {}", skillNode.id());
        } catch (IllegalNodeStateException e) {
            // Show alert
            LOG.debug("Failed to add point to skill node: {}", skillNode.id());
        }
    }

    @Override
    public void onSkillRightClicked(SkillNode skillNode) {
        try {
            SkillTreeState state = this.playerState.getSkillTreeState();
            this.skillService.removePoint(state, this.skillService.getSkillTree(), skillNode.id());
            this.skillService.save(state);
            this.view.refreshSkillState(state);
            LOG.info("Removed point from skill node: {}", skillNode.id());
        } catch (IllegalNodeStateException e) {
            // Show alert
            LOG.debug("Failed to remove point from skill node: {}", skillNode.id());
        }
    }

    @Override
    public void onReturnClicked() {
        this.metaController.onMainMenu();
    }
}
