package ulb.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ulb.common.LevelUpResult;
import ulb.models.level_up.LevelUp;
import ulb.models.level_up.Upgrade;
import ulb.services.LevelUpService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the level-up screen.
 *
 * The {@link LevelUp} instances ares stored in a queue. The {@link LevelUp} at the head of the queue is always the one
 * being displayed. When an {@link Upgrade} is chosen, the head is popped.
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private final LevelUpService levelUpService;
    private final Queue<LevelUpResult> pendingLevelUps;

    /**
     * Constructs a {@code LevelUpController}, initialises its {@link LevelUpView}, and registers the choice callback.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     */
    public LevelUpController(MetaController metaController, LevelUpService levelUpService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.levelUpService = levelUpService;
        this.pendingLevelUps = new LinkedList<>();
        this.view.setListener(this);
    }

    // TODO: name? initialize or show or sth else ?
    public void initialize(List<LevelUpResult> levelUps) {
        this.pendingLevelUps.addAll(levelUps);
        this.processNextLevelUp();
    }

    public void processNextLevelUp() {
        // TODO
    }

    @Override
    public void onUpgradeChosen(int upgradeIdx) {
        LevelUp current = this.pendingLevelUps.peek();
        if (current != null) {
            current.apply(upgradeIdx);
            // TODO: this.bugemonService.saveLevelUp(current);
            this.pendingLevelUps.poll();
        }

        if (!this.pendingLevelUps.isEmpty()) {
            this.updateDisplayedLevelUp();
        } else {
            this.metaController.onAllPendingLevelUpsConsumed();
        }
    }

    @Override
    protected void show() {
        this.updateDisplayedLevelUp();
        super.show();
    }
}
