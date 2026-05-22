package ulb.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ulb.common.LevelUpResult;
import ulb.models.player.BonusStats;
import ulb.services.LevelUpService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller for the level-up screen shown after combat.
 *
 * <p>
 * Processes a queue of {@link ulb.common.LevelUpResult} entries one at a time, displaying bonus-stat options for each
 * Bugemon that gained a level. When the queue is empty it notifies the {@link ulb.controllers.MetaController} so the
 * flow can continue.
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private final LevelUpService levelUpService;
    private final Queue<LevelUpResult> pendingLevelUps;

    private LevelUpResult currentLevelUp;

    public LevelUpController(MetaController metaController, LevelUpService levelUpService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.levelUpService = levelUpService;
        this.pendingLevelUps = new LinkedList<>();
        this.view.setListener(this);
    }

    /**
     * Loads the list of pending level-ups and presents the first one.
     *
     * @param levelUps
     *            the level-up results to process; may be empty
     */
    public void initialize(List<LevelUpResult> levelUps) {
        this.pendingLevelUps.clear();
        this.pendingLevelUps.addAll(levelUps);
        this.processNextLevelUp();
    }

    /**
     * Dequeues the next pending level-up and displays its options, or signals completion if the queue is empty.
     */
    public void processNextLevelUp() {
        if (this.pendingLevelUps.isEmpty()) {
            this.metaController.onAllPendingLevelUpsConsumed();
            return;
        }
        this.currentLevelUp = this.pendingLevelUps.poll();
        this.view.displayLevelUpOptions(this.currentLevelUp.bugemon().getName(), this.currentLevelUp.levelPassed(),
                this.currentLevelUp.bugemon().getSpritePath(), this.levelUpService.generateLevelUpOptions());
    }

    @Override
    public void onBonusChosen(BonusStats bonus) {
        this.levelUpService.applyLevelUp(this.currentLevelUp.bugemon(), bonus);
        this.processNextLevelUp();
    }
}
