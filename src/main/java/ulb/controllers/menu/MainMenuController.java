package ulb.controllers.menu;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.models.player.PlayerState;
import ulb.views.ViewLoader;
import ulb.views.menu.MainMenuView;

/**
 * Controller for the main menu screen.
 *
 * <p>
 * Delegates all navigation actions to {@link MetaController}. Guards combat and tower entry points by checking that an
 * active team has been selected before forwarding the request.
 */
public class MainMenuController extends Controller<MainMenuView> implements MainMenuView.Listener {

    private final PlayerState playerState;

    public MainMenuController(MetaController metaController, PlayerState playerState) {
        super(metaController, ViewLoader.load(MainMenuView::new));
        this.playerState = playerState;

        this.view.setListener(this);
    }

    @Override
    public void onCreateTeam() {
        this.metaController.onCreateTeam();
    }

    @Override
    public void onEditTeam() {
        this.metaController.onEditTeam();
    }

    @Override
    public void onCreateBugemon() {
        this.metaController.onCreateBugemon();
    }

    /** Starts an automatic combat session. */
    @Override
    public void onStartAutomaticCombat() {
        if (this.isActiveTeamPresent()) {
            this.metaController.onStartAutomaticCombat();
        }
    }

    /** Starts a manual combat session. */
    @Override
    public void onStartManualCombat() {
        if (this.isActiveTeamPresent()) {
            this.metaController.onStartManualCombat();
        }
    }

    @Override
    public void onTower() {
        if (this.isActiveTeamPresent()) {
            this.metaController.onTower();
        }
    }

    @Override
    public void onSaveMenuReturnButton() {
        this.metaController.onSaveMenu();
    }

    @Override
    public void onSkillTree() {
        this.metaController.onSkillTree();
    }

    @Override
    public void onOpenSettings() {
        this.metaController.openSettings();
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }

    private boolean isActiveTeamPresent() {
        return this.playerState.getActiveTeam().map(team -> true).orElseGet(() -> {
            this.view.showAlertChooseTeamToLaunchCombat();
            return false;
        });
    }
}
