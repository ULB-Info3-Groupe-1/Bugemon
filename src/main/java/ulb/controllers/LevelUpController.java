package ulb.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ulb.models.level_up.LevelUp;
import ulb.models.level_up.Upgrade;
import ulb.services.BugemonService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the level-up screen.
 *
 * The {@link LevelUp} instances ares stored in a queue. The {@link LevelUp} at the head of the queue is always the one
 * being displayed. When an {@link Upgrade} is chosen, the head is popped.
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private final BugemonService bugemonService;
    private final Queue<LevelUp> pendingLevelUps;

    /**
     * Constructs a {@code LevelUpController}, initialises its {@link LevelUpView}, and registers the choice callback.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     */
    public LevelUpController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.bugemonService = bugemonService;
        this.pendingLevelUps = new LinkedList<>();
        this.view.setListener(this);
    }

    public void addLevelUps(List<LevelUp> levels) {
        this.pendingLevelUps.addAll(levels);
    }

    public boolean hasWorkToDo() {
        return !this.pendingLevelUps.isEmpty();
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

    public void updateDisplayedLevelUp() {
        this.view.setLevelUp(this.pendingLevelUps.peek());
        this.view.refresh();
    }

    @Override
    protected void show() {
        this.updateDisplayedLevelUp();
        super.show();
    }
}
