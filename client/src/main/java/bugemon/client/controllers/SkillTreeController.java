package bugemon.client.controllers;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteSkillService;
import bugemon.client.views.SkillTreeView;
import bugemon.client.views.ViewLoader;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.skills.SkillNode;
import bugemon.common.models.skills.SkillTree;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.models.skills.exceptions.IllegalNodeStateException;

/**
 * Controller for the skill-tree screen.
 *
 * <p>
 * The static {@link SkillTree} definition is fetched once from the server (for rendering and validation). Left-clicking
 * a {@link SkillNode} spends a skill point to increase its level; right-clicking refunds it. Point allocation is pure
 * model logic applied locally on the {@link SkillTreeState}; the resulting state is then persisted asynchronously
 * through {@link RemoteSkillService}.
 */
public class SkillTreeController extends Controller<SkillTreeView> implements SkillTreeView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(SkillTreeController.class);

    private final RemoteSkillService skillService;
    private final PlayerState playerState;

    private SkillTree skillTree;

    public SkillTreeController(MetaController metaController, RemoteSkillService skillService,
            PlayerState playerState) {
        super(metaController, ViewLoader.load(SkillTreeView::new));
        this.skillService = skillService;
        this.playerState = playerState;
        this.view.setListener(this);
        this.loadTree();
    }

    /**
     * Fetches the static skill-tree definition from the server and, once it arrives, wires it into the view and
     * refreshes the displayed state on the JavaFX thread.
     */
    private void loadTree() {
        this.skillService.getSkillTree().whenComplete((tree, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to load skill tree", error);
                return;
            }
            this.skillTree = tree;
            this.view.setTree(tree);
            this.view.refreshSkillState(this.playerState.getSkillTreeState());
        }));
    }

    /** Refreshes the view with the current skill-tree state before displaying it. */
    @Override
    protected void show() {
        this.view.refreshSkillState(this.playerState.getSkillTreeState());
        super.show();
    }

    @Override
    public void onSkillLeftClicked(SkillNode skillNode) {
        if (this.skillTree == null) {
            return;
        }
        try {
            SkillTreeState state = this.playerState.getSkillTreeState();
            state.addPoint(skillNode.id(), this.skillTree);
            this.persist(state);
            this.view.refreshSkillState(state);
            LOG.info("Added point to skill node: {}", skillNode.id());
        } catch (IllegalNodeStateException e) {
            LOG.debug("Failed to add point to skill node: {}", skillNode.id());
        }
    }

    @Override
    public void onSkillRightClicked(SkillNode skillNode) {
        if (this.skillTree == null) {
            return;
        }
        try {
            SkillTreeState state = this.playerState.getSkillTreeState();
            state.removePoint(skillNode.id(), this.skillTree);
            this.persist(state);
            this.view.refreshSkillState(state);
            LOG.info("Removed point from skill node: {}", skillNode.id());
        } catch (IllegalNodeStateException e) {
            LOG.debug("Failed to remove point from skill node: {}", skillNode.id());
        }
    }

    @Override
    public void onReturnClicked() {
        this.metaController.onMainMenu();
    }

    /**
     * Persists the updated skill-tree state in the background, logging any failure without blocking the UI.
     */
    private void persist(SkillTreeState state) {
        this.skillService.save(state).whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.warn("Failed to persist skill-tree state", error);
            }
        });
    }
}
