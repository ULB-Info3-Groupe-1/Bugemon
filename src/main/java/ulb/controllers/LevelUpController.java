package ulb.controllers;

import java.io.IOException;
import java.util.List;

import ulb.controllers.MetaController.Window;
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUp;
import ulb.models.level_up.LevelUpSession;
import ulb.views.LevelUpView;

/**
 * Controller responsible for the level-up screen.
 *
 * <p>
 * Manages a {@link LevelUpSession} model. After each user choice or advance,
 * the controller mutates the session and calls {@code view.refresh()} so the
 * view pulls the updated event data directly from the session.
 * </p>
 */
public class LevelUpController extends Controller<LevelUpView> {
    private final LevelUpSession session = new LevelUpSession();

    /**
     * Constructs a {@code LevelUpController}, initialises its {@link LevelUpView},
     * and registers the choice callback.
     *
     * @param metaController the application-level controller used for navigation.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public LevelUpController(MetaController metaController) throws IOException {
        super(metaController, new LevelUpView());
        this.view.setSession(session);
        this.view.setOnChooseOption(this::chooseOption);
    }

    /** Applies the chosen stat bonus and advances to the next level-up event. */
    public void chooseOption(int optionIdx) {
        LevelUp levelUp = session.getCurrent();
        Choice choice = levelUp.getChoices().get(optionIdx);
        levelUp.getBugemon().applyChoice(choice);
        cont();
    }

    /**
     * Initialises the session with the given list and navigates to the level-up
     * screen, or goes directly to victory if the list is empty.
     */
    public void setLevelUp(List<LevelUp> lvlsUp) {
        if (!lvlsUp.isEmpty()) {
            session.start(lvlsUp);
            this.metaController.switchTo(Window.LEVEL_UP);
            this.view.refresh();
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }

    /** Advances to the next pending level-up event, or navigates to victory if done. */
    public void cont() {
        if (session.hasNext()) {
            session.advance();
            this.view.refresh();
        } else {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        }
    }
}
