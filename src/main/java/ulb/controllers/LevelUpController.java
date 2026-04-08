package ulb.controllers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import ulb.controllers.MetaController.Window;
import ulb.models.level_up.LevelUp;
import ulb.models.level_up.LevelUpSession;
import ulb.models.level_up.Upgrade;
import ulb.services.PlayerService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the level-up screen. Manages a {@link LevelUpSession} model. After each player choice or
 * advance, the controller mutates the session and calls {@code view.refresh()} so the view pulls the updated event data
 * directly from the session.
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
    public void onUpgradeChosen(int optionIdx) {
        LevelUp levelUp = this.levelUps.remove();
        Upgrade upgrade = levelUp.get(optionIdx);
        levelUp.getBugemon().applyUpgrade(upgrade);
        this.playerService.saveBugemonState(levelUp.getBugemon());


        if (this.levelUps.isEmpty()) {
            this.playerService.saveActiveTeamState();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        } else {
            this.view.refresh();
        }
    }

    /**
     * Initialises the session with the given list and navigates to the level-up screen, or goes directly to victory if
     * the list is empty.
     */
    public void setLevelUp(List<LevelUp> lvlsUp) {
        if (!lvlsUp.isEmpty()) {
            this.levelUps = new ArrayDeque<>(lvlsUp);
            this.metaController.switchTo(Window.LEVEL_UP);
            this.view.refresh();
        } else {
            this.playerService.saveActiveTeamState();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }
}
