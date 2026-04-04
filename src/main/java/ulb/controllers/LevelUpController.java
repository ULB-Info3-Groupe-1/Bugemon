package ulb.controllers;

import java.io.IOException;
import java.util.List;

import ulb.controllers.MetaController.Window;
import ulb.models.level_up.LevelUp;
import ulb.models.level_up.LevelUpSession;
import ulb.models.level_up.Upgrade;
import ulb.services.PlayerService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the level-up screen.
 *
 * <p>
 * Manages a {@link LevelUpSession} model. After each user choice or advance, the controller mutates the session and
 * calls {@code view.refresh()} so the view pulls the updated event data directly from the session.
 * </p>
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private final LevelUpSession session = new LevelUpSession();
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
        this.view.setSession(this.session);
    }

    @Override
    public void onUpgradeChosen(int optionIdx) {
        this.chooseOption(optionIdx);
    }

    /** Applies the chosen stat bonus and advances to the next level-up event. */
    public void chooseOption(int optionIdx) {
        LevelUp levelUp = this.session.getCurrent();
        Upgrade upgrade = levelUp.get(optionIdx);
        levelUp.getBugemon().applyUpgrade(upgrade);
        this.playerService.saveBugemonState(levelUp.getBugemon());
        this.cont();
    }

    /**
     * Initialises the session with the given list and navigates to the level-up screen, or goes directly to victory if
     * the list is empty.
     */
    public void setLevelUp(List<LevelUp> lvlsUp) {
        if (!lvlsUp.isEmpty()) {
            this.session.start(lvlsUp);
            this.metaController.switchTo(Window.LEVEL_UP);
            this.view.refresh();
        } else {
            this.playerService.saveActiveTeamState();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }

    /** Advances to the next pending level-up event, or navigates to victory if done. */
    public void cont() {
        if (this.session.hasNext()) {
            this.session.advance();
            this.view.refresh();
        } else {
            this.playerService.saveActiveTeamState();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }
}
