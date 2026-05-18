package ulb.controllers;

import java.util.HashSet;
import java.util.List;

import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
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
    private final Team team;

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

        this.team = new Team();

        this.view.setListener(this);
    }

    public void preloadTeam(Team preloaded) {
        if (preloaded == null) {
            return;
        }

        preloaded.getMembers().forEach(this.team::add);
        this.updateDisplayedTeam();
    }

    @Override
    protected void show() {
        super.show();

        this.updateDisplayedTeam();
        this.updateDisplayedTeamNames();
        this.updateDisplayedAvailableBugemons();
    }

    public void updateDisplayedTeam() {
        this.view.refreshTeam(this.team, this.isWorkingTeamSaved());
    }

    public void updateDisplayedTeamNames() { 
        this.view.refreshTeamNames(this.getTeamNames());
    }

    public void updateDisplayedAvailableBugemons() {
        this.view.refreshAvailableBugemons(this.getAvailableBugemons(), new HashSet<>(this.team.getMembers()));
    }

    public List<PlayerBugemon> getAvailableBugemons() {
        return this.bugemonService.getAllBugemons();
    }

    public boolean isWorkingTeamSaved() {
        return this.teamService.isWorkingTeamSaved();
    }

    public List<String> getTeamNames() {
        return this.teamService.getTeamNames();
    }

    @Override
    public void onBugemonSelected(PlayerBugemon bugemon) {
        if (this.team.contains(bugemon)) {
            this.team.remove(bugemon);
        } else {
            this.team.add(bugemon);
        }
        this.updateDisplayedTeam();
        this.updateDisplayedAvailableBugemons();
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
        this.view.refresh();
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.teamService.deleteTeam(teamName);
        } catch (NoActiveTeamException | TeamNotFoundException e) {
            this.view.showDeleteTeamNoActiveTeamAlert();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamAlert();
        }
        this.view.refresh();
    }

    @Override
    public void onRename(String newName) {
        try {
            this.teamService.renameActiveTeam(newName);
        } catch (TeamNotFoundException | NoActiveTeamException e) {
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
        this.team.clear();
        this.updateDisplayedTeam();
        this.view.clearTeamNameToSave();
        this.view.refresh();
    }

    @Override
    public void onModifyTeam(String teamName) {
        if (teamName == null || teamName.isEmpty() || this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToModify();
            return;
        }
        try {
            this.teamService.modifyActiveTeam();
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (TeamNotFoundException | NoActiveTeamException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.view.refresh();
    }

    @Override
    public void onTeamSelected(String teamName) {
        try {
            this.teamService.setActiveTeam(teamName);
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
        this.teamService.setWorkingTeamAsActiveTeam();
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
        if (this.teamService.isActiveTeamEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            combatAction.run();
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
            this.metaController.onMainMenu();
        }
    }
}
