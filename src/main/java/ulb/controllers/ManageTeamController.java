package ulb.controllers;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.views.ManageTeamView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the team creation screen. Mutates the {@link BugemonTeam} model in response to player
 * actions, then calls {@code view.refresh()} so the view can pull the updated state from the model directly. The
 * controller never pushes data into the view.
 */
public class ManageTeamController extends Controller<ManageTeamView> implements ManageTeamView.Listener {
    private final TeamService teamService;
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
    public ManageTeamController(TeamFormMode mode, MetaController metaController, TeamService teamService,
            BugemonService bugemonService) {
        super(metaController, ViewLoader.load(() -> new ManageTeamView(mode)));
        this.teamService = teamService;
        this.bugemonService = bugemonService;

        this.view.setListener(this);
    }

    @Override
    protected void show() {
        this.view.clearTeamNameToSave();
        this.view.setAvailableBugemons(this.bugemonService.getAllDefaultBugemons());
        this.refresh();
        super.show();
    }

    private void refresh() {
        this.view.setTeam(this.teamService.getActiveTeam());
        this.view.setIsActiveTeamSaved(this.teamService.isActiveTeamSaved());
        this.view.setTeamList(this.teamService.getTeamNames());
        this.view.refresh();
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        this.teamService.addOrRemoveBugemonOfActiveTeam(bugemon);
        this.refresh();
    }

    @Override
    public void onSave(String teamName) {
        try {
            this.teamService.saveTeam(teamName);
            this.refresh();
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (NoActiveTeamException e) {
            throw new IllegalStateException(
                    "The player team to save doesn't exist. The active team should exist now and be modified.");
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
    }

    @Override
    public void onLoad(String teamName) {
        try {
            this.teamService.setActiveTeam(teamName);
            this.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
    }

    @Override
    public void onDelete() {
        try {
            this.teamService.deleteActiveTeam();
            this.refresh();
        } catch (NoActiveTeamException e) {
            this.view.showDeletTeamNoActiveTeamAlert();
        } catch (TeamNotFoundException e) {
            this.teamService.getActiveTeam().ifPresentOrElse(team -> this.view.showTeamNotFoundAlert(team.getName()),
                    this.view::showDeletTeamNoActiveTeamAlert);
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamAlert();
        }
    }

    @Override
    public void onRename(String oldName, String newName) {
        try {
            this.teamService.renameTeam(oldName, newName);
            this.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(oldName);
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
        } catch (NoActiveTeamException e) {
            this.view.showRenameTeamNoActiveTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
    }

    @Override
    public void onAddNewTeam() {
        this.teamService.clearActiveTeam();
        this.view.clearTeamNameToSave();
        this.refresh();
    }

    @Override
    public void onModifyTeam() {
        try {
            this.teamService.modifyActiveTeam();
            this.refresh();
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (NoActiveTeamException e) {
            this.view.showAlertChooseTeamToModify();
        } catch (TeamNotFoundException e) {
            this.teamService.getActiveTeam().ifPresentOrElse(team -> this.view.showTeamNotFoundAlert(team.getName()),
                    this.view::showAlertChooseTeamToModify);
        }
    }

    @Override
    public void onStartAutomaticCombat() {
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            this.metaController.onStartAutomaticCombat();
        }
    }

    @Override
    public void onStartManualCombat() {
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            this.metaController.onStartManualCombat();
        }
    }

    @Override
    public void onStartNOTowerCombat() {
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            this.metaController.onTower();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        boolean canLeave = this.teamService.isActiveTeamSaved();

        if (!canLeave && this.view.showAlertTeamChangesNotSave()) {
            this.teamService.clearActiveTeam();
            canLeave = true;
        }

        if (canLeave) {
            this.metaController.onMainMenu();
        }
    }
}
