package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ulb.controllers.MetaController.Window;
import ulb.models.level_up.LevelUp;
import ulb.models.level_up.Upgrade;
import ulb.services.PlayerService;
import ulb.views.LevelUpView;

/**
 * Controller responsible for the level-up screen.
 *
 * <p>
 * Manages a {@link LevelUpSession} model. After each user choice or advance, the controller mutates the session and
 * calls {@code view.refresh()} so the view pulls the updated event data directly from the session.
 * </p>
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private List<LevelUp> levelUps = new ArrayList<>();
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
        super(metaController, new LevelUpView());
        this.playerService = playerService;
        this.view.setListener(this);
    }

    /** Applies the chosen stat bonus and advances to the next level-up event. */
    @Override
    public void onChooseUpgrade(int idx) {
        // retrieve upgrade that was chosen
        LevelUp levelUp = this.levelUps.removeLast();
        Upgrade upgrade = levelUp.getChoices().get(optionIdx);

        // apply the upgrade
        levelUp.getBugemon().applyChoice(upgrade);
        this.playerService.saveBugemonState(levelUp.getBugemon());

        if (this.levelUps.size() > 0) {
            this.view.refresh();
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }

    /**
     * Initialises the session with the given list and navigates to the level-up screen, or goes directly to victory if
     * the list is empty.
     */
    public void setLevelUp(List<LevelUp> newLevelUps) {
        this.levelUps = newLevelUps;
        this.view.refresh();
    }
}
