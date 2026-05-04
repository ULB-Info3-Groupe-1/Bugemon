package ulb.controllers;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.BugemonService;
import ulb.services.TeamService;
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
        this.view.setAvailableBugemons(this.bugemonService.getAllDefaultBugemons());
        this.teamService.getActiveTeam().ifPresentOrElse(team -> {
            this.teamService.setWorkingTeamEqualsActiveTeam();
            this.view.setTeam(team);
            this.view.setTeamListSelected(team.getName());
            this.view.setTeamList(this.teamService.getTeamNames());
        }, this::refresh);
        super.show();
    }

    private void refresh() {
        this.view.setTeam(this.teamService.getWorkingTeam());
        this.view.setIsTeamSaved(this.teamService.isWorkingTeamSaved());
        this.view.setTeamList(this.teamService.getTeamNames());
        this.view.refresh();
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        this.teamService.addOrRemoveBugemon(bugemon);
        this.refresh();
    }

    @Override
    public void onSave(String teamName) {
        try {
            this.teamService.saveTeam(teamName);
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
        this.refresh();
    }

    @Override
    public void onLoad(String teamName) {
        try {
            this.teamService.setActiveTeam(teamName);
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.refresh();
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.teamService.deleteTeam(teamName);
        } catch (TeamNotFoundException e) {
            this.view.showDeleteTeamNoActiveTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamAlert();
        }
        this.refresh();
    }

    @Override
    public void onRename(String oldName, String newName) {
        try {
            this.teamService.renameTeam(oldName, newName);
        } catch (TeamNotFoundException e) {
            this.view.showSelectTeamToRenameAlert();
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
        this.refresh();
    }

    @Override
    public void onAddNewTeam() {
        this.teamService.clearWorkingTeam();
        this.view.clearTeamNameToSave();
        this.refresh();
    }

    @Override
    public void onModifyTeam(String teamName) {
        if (teamName == null || teamName.isEmpty()) {
            this.view.showAlertChooseTeamToModify();
            return;
        }
        try {
            this.teamService.modifyTeam(teamName);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.refresh();
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
        boolean canLeave = this.teamService.isWorkingTeamSaved();

        if (!canLeave && this.view.showAlertTeamChangesNotSave()) {
            this.teamService.clearWorkingTeam();
            canLeave = true;
        }

        if (canLeave) {
            this.metaController.onReturnToMainMenu();
        }
    }
}
