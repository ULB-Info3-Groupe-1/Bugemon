package ulb.controllers;

import java.util.List;
import java.util.Optional;

import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
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

    /**
     * The team that the player is currently modifying. It is used to keep track of the changes made to the team before
     * saving it to the database.
     */
    private Team workingTeam;

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
        this.workingTeam = new Team();
        this.view.setListener(this);
    }

    @Override
    protected void show() {
        this.teamService.getActiveTeam().ifPresent(t -> this.workingTeam = t);
        this.view.refresh();
        super.show();
    }

    @Override
    public List<PlayerBugemon> getAvailableBugemons() {
        return this.bugemonService.getAllBugemons();
    }

    @Override
    public Team getWorkingTeam() {
        return this.workingTeam;
    }

    @Override
    public boolean isWorkingTeamSaved() {
        return this.teamService.isTeamSaved(this.workingTeam);
    }

    @Override
    public List<String> getTeamNames() {
        return this.teamService.getTeamNames();
    }

    @Override
    public Optional<String> getActiveTeamName() {
        return this.teamService.getActiveTeamName();
    }

    @Override
    public void onBugemonSelected(PlayerBugemon bugemon) {
        if (this.workingTeam.contains(bugemon)) {
            this.workingTeam.remove(bugemon);
        } else if (!this.workingTeam.isFull()) {
            this.workingTeam.add(bugemon);
        }
        this.view.refresh();
    }

    @Override
    public void onSave(String teamName) {
        try {
            this.workingTeam.setName(teamName);
            this.teamService.saveTeam(this.workingTeam);
            this.workingTeam.clear();
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
        this.view.refresh();
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.teamService.deleteTeam(teamName);
            this.workingTeam.clear();
        } catch (TeamNotFoundException e) {
            this.view.showDeleteTeamNoActiveTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamAlert();
        }
        this.view.refresh();
    }

    @Override
    public void onRename(String newName) {
        try {
            this.teamService.renameActiveTeam(this.workingTeam.getName(), newName);
            this.workingTeam.setName(newName);
        } catch (TeamNotFoundException e) {
            this.view.showSelectTeamToRenameAlert();
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        }
        this.view.refresh();
    }

    @Override
    public void onAddNewTeam() {
        this.workingTeam.clear();
        this.view.clearTeamNameToSave();
        this.view.refresh();
    }

    @Override
    public void onModifyTeam(String teamName) {
        if (teamName == null || teamName.isEmpty() || this.teamService.getActiveTeam().isEmpty()) {
            this.view.showAlertChooseTeamToModify();
            return;
        }
        try {
            this.teamService.modifyTeam(this.workingTeam);
            this.workingTeam = this.teamService.getActiveTeam()
                    .orElseThrow(() -> new TeamNotFoundException("Active team not found"));
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.view.refresh();
    }

    @Override
    public void onTeamSelected(String teamName) {
        try {
            this.teamService.setActiveTeam(teamName);
            this.workingTeam = this.teamService.getActiveTeam()
                    .orElseThrow(() -> new TeamNotFoundException("Active team not found"));
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.view.refresh();
    }

    @Override
    public void onStartAutomaticCombat() {
        this.executeIfActiveTeamNotEmpty(this.metaController::onStartAutomaticCombat);
    }

    @Override
    public void onStartManualCombat() {
        this.executeIfActiveTeamNotEmpty(this.metaController::onStartManualCombat);
    }

    @Override
    public void onStartTowerCombat() {
        this.executeIfActiveTeamNotEmpty(this.metaController::onTower);
    }

    private void executeIfActiveTeamNotEmpty(Runnable combatAction) {
        if (this.teamService.getActiveTeam().isEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            combatAction.run();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        boolean canLeave = this.teamService.isTeamSaved(this.workingTeam);

        if (!canLeave && this.view.showAlertTeamChangesNotSave()) {
            this.workingTeam.clear();
            canLeave = true;
        }

        if (canLeave) {
            this.metaController.onMainMenu();
        }
    }
}
