package ulb.controllers;

import javafx.stage.Stage;

import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.services.exceptions.TeamEmptyException;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;
import ulb.views.ManageTeamView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the team creation screen. Mutates the {@link BugemonTeam} model in response to player
 * actions, then calls {@code view.refresh()} so the view can pull the updated state from the model directly. The
 * controller never pushes data into the view.
 */
public class ManageTeamController extends Controller<ManageTeamView> implements ManageTeamView.Listener {
    private final PlayerService playerService;
    private final BugemonService bugemonService;

    public enum TeamFormMode {
        EDIT,
        CREATE
    }

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and performs an initial
     * {@link ulb.views.ManageTeamView#refresh()} to populate the Bugemon grid.
     *
     */
    public ManageTeamController(TeamFormMode mode, MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) {
        super(metaController, ViewLoader.load(() -> new ManageTeamView(mode)));
        this.playerService = playerService;
        this.bugemonService = bugemonService;

        this.view.setListener(this);
    }

    @Override
    protected void show(Stage stage) {
        this.view.setAvailableBugemons(this.bugemonService.getAllDefaultBugemons());
        this.refresh();
        super.show(stage);
    }

    private void refresh() {
        this.view.setTeam(this.playerService.getActiveTeam());
        this.view.setIsActiveTeamSaved(this.playerService.isActiveTeamSaved());
        this.view.setTeamList(this.playerService.getTeamNames());
        this.view.refresh();
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        this.playerService.addOrRemoveBugemonOfActiveTeam(bugemon);
        this.refresh();
    }

    @Override
    public void onSave(String teamName) {
        if (this.valideName(teamName)) {
            try {
                this.playerService.saveTeam(teamName);
                this.refresh();
            } catch (TeamNameAlreadyExistsException e) {
                this.view.showTeamNameAlreadyExistsAlert(teamName);
            } catch (TeamEmptyException e) {
                this.view.showEmptyTeamAlert();
            } catch (NoActiveTeamException e) {
                throw new IllegalStateException(
                        "The player team to save doesn't exist. The active team should exist now and be modified.");
            }
        }
    }

    @Override
    public void onLoad(String teamName) {
        if (this.valideName(teamName)) {
            try {
                this.playerService.setActiveTeam(teamName);
                this.refresh();
            } catch (TeamNotFoundException e) {
                this.view.showTeamNotFoundAlert(teamName);
            }
        }
    }

    @Override
    public void onDelete() {
        try {
            this.playerService.deleteActiveTeam();
            this.refresh();
        } catch (NoActiveTeamException e) {
            this.view.showDeletTeamNoActiveTeamAlert();
        }
    }

    @Override
    public void onRename(String oldName, String newName) {
        if (!this.valideName(newName)) {
            return;
        }

        try {
            this.playerService.renameTeam(oldName, newName);
            this.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(oldName);
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
        } catch (NoActiveTeamException e) {
            this.view.showRenameTeamNoActiveTeamAlert();
        }
    }

    @Override
    public void onAddNewTeam() {
        this.playerService.clearActiveTeam();
        this.view.clearTeamNameToSave();
        this.refresh();
    }

    @Override
    public void onModifyTeam() {
        try {
            this.playerService.modifyActiveTeam();
            this.refresh();
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (NoActiveTeamException e) {
            this.view.showAlertChooseTeamToModify();
        }
    }

    private boolean valideName(String name) {
        if (name == null || name.isBlank()) {
            this.view.showEmptyTeamNameAlert();
            return false;
        }
        return true;
    }

    private void launchCombat(Window window) {
        if (this.playerService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            this.metaController.switchTo(window);
        }
    }

    @Override
    public void onLaunchAutomaticCombat() {
        this.launchCombat(Window.AUTOMATIC_COMBAT);
    }

    @Override
    public void onLaunchManualCombat() {
        this.launchCombat(Window.MANUAL_COMBAT);
    }

    @Override
    public void onLaunchNOTowerCombat() {
        this.launchCombat(Window.NOTOWER);
    }

    @Override
    public void onReturnToMainMenu() {
        boolean canLeave = this.playerService.isActiveTeamSaved();

        if (!canLeave && this.view.showAlertTeamChangesNotSave()) {
            this.playerService.clearActiveTeam();
            canLeave = true;
        }

        if (canLeave) {
            this.metaController.switchTo(MetaController.Window.MAIN_MENU);
        }
    }
}
