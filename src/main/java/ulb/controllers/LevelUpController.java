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
 * Controller responsible for the level-up screen.
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
    public void onBonusChosen(BonusStats bonus) {
        // TODO
    }

    @Override
    protected void show() {
        super.show();
    }
}
