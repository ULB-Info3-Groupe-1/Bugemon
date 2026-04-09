package ulb.controllers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import ulb.models.level_up.LevelUp;
import ulb.services.PlayerService;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the level-up screen.
 *
 * The {@link LevelUp} instances ares stored in a queue. The {@link LevelUp} at the head of the queue is always the one
 * being displayed. When an {@link Upgrade} is chosen, the head is popped.
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private Queue<LevelUp> levelUps = new ArrayDeque<>();
    private final PlayerService playerService;

    /**
     * Constructs a {@code LevelUpController}, initialises its {@link LevelUpView}, and registers the choice callback.
     *
     * @param metaController
     *            the application-level controller used for navigation.
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public LevelUpController(MetaController metaController, PlayerService playerService) throws IOException {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.playerService = playerService;
        this.view.setListener(this);
    }

    @Override
    public void onUpgradeChosen(int upgradeIdx) {
        LevelUp levelUp = this.levelUps.remove();
        levelUp.apply(upgradeIdx);

        this.playerService.saveBugemonState(levelUp.getBugemon());

        if (this.levelUps.isEmpty()) {
            this.metaController.onLevelUpfinished();
        } else {
            this.updateDisplayedLevelUp();
        }
    }

    public void updateDisplayedLevelUp() {
        this.view.setLevelUp(this.levelUps.peek());
        this.view.refresh();
    }

    /**
     * Initialises the session with the given list and navigates to the level-up screen, or goes directly to victory if
     * the list is empty.
     */
    public void setLevelUps(List<LevelUp> levelUps) {
        if (levelUps.isEmpty()) {
            throw new IllegalArgumentException("level-ups list cannot be empty");
        }

        this.levelUps = new ArrayDeque<>(levelUps);
        this.updateDisplayedLevelUp();
    }
}
