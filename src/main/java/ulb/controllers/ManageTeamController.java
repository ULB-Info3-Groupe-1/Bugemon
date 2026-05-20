package ulb.controllers;

import ulb.models.player.PlayerBugemon;
import ulb.models.player.PlayerState;
import ulb.models.team.Team;
import ulb.repositories.exceptions.TeamNotFoundException;
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
    private final PlayerState playerState;

    private Team tmpTeam; // The team that gets edited in this screen

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
            PlayerState playerState) {
        super(metaController, ViewLoader.load(() -> new ManageTeamView(mode)));
        this.teamService = teamService;
        this.playerState = playerState;
        this.tmpTeam = this.playerState.getActiveTeam().orElseGet(Team::new);

        this.view.setListener(this);
    }

    @Override
    public void onBugemonSelected(PlayerBugemon bugemon) {
        if (this.tmpTeam.contains(bugemon)) {
            this.tmpTeam.remove(bugemon);
        } else if (!this.tmpTeam.isFull()) {
            this.tmpTeam.add(bugemon);
        }
        this.view.refresh();
    }

    @Override
    public void onSave(String teamName) {
        if (this.teamService.teamExists(teamName)) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
            return;
        }

        this.tmpTeam.setName(teamName);
        this.teamService.save(this.tmpTeam); // TODO: show alert messages if operation fails
        this.clearTmpTeam();

        this.view.refresh();
    }

    @Override
    public void onDelete(String teamName) {
        this.teamService.deleteTeam(teamName); // TODO: what if operation fails
        this.clearTmpTeam();
        this.view.refresh();
    }

    @Override
    public void onRename(String newName) {
        if (this.teamService.teamExists(newName)) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
            return;
        }

        this.teamService.renameTeam(this.tmpTeam, newName); // TODO: what if operation fails

        this.view.refresh();
    }

    @Override
    public void onAddNewTeam() {
        this.clearTmpTeam();
        this.view.clearTeamNameToSave();
        this.view.refresh();
    }

    @Override
    public void onModifyTeam(String teamName) {
        if (teamName == null || teamName.isEmpty() || !this.teamService.teamExists(teamName)) {
            this.view.showAlertChooseTeamToModify();
            return;
        }
        // TODO: what if team doesn't exist or operation fails?

        this.tmpTeam.setName(teamName);
        this.teamService.save(this.tmpTeam);

        this.view.refresh();
    }

    @Override
    public void onTeamSelected(String teamName) {
        try {
            this.playerState.setActiveTeam(this.teamService.getTeam(teamName)
                    .orElseThrow(() -> new TeamNotFoundException("Active team not found")));
            this.tmpTeam = this.teamService.getTeam(teamName)
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

    private void executeIfActiveTeamNotEmpty(Runnable combat) {
        if (this.playerState.getActiveTeam().isEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            combat.run();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        if (!this.teamService.isTeamSaved(this.tmpTeam) && this.view.showAlertTeamChangesNotSave()) {
            this.clearTmpTeam();;
        }

        this.metaController.onMainMenu();
    }

    private void clearTmpTeam() {
        this.tmpTeam.clear();
        this.tmpTeam.setName(null);
    }
}
