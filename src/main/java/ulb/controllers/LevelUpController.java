package ulb.controllers;

import javafx.stage.Stage;

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

    /**
     * Constructs a {@code LevelUpController}, initialises its {@link LevelUpView}, and registers the choice callback.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     */
    public LevelUpController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    @Override
    public void onUpgradeChosen(int upgradeIdx) {
        this.bugemonService.applyNextLevelUp(upgradeIdx);

        if (this.bugemonService.hasPendingLevelUps()) {
            this.updateDisplayedLevelUp();
        } else {
            this.metaController.onLevelUpfinished();
        }
    }

    public void updateDisplayedLevelUp() {
        this.view.setLevelUp(this.bugemonService.peekNextLevelUp());
        this.view.refresh();
    }

    @Override
    protected void show(Stage stage) {
        this.updateDisplayedLevelUp();
        super.show(stage);
    }
}
