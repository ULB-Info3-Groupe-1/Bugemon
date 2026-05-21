package ulb.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.common.dto.BugemonDisplayDTO;
import ulb.models.player.PlayerBugemon;
import ulb.models.player.PlayerState;
import ulb.models.team.Team;
import ulb.services.BugemonService;
import ulb.services.TeamService;
import ulb.services.exceptions.TeamNameEmptyException;
import ulb.services.exceptions.TeamNotFoundException;
import ulb.views.ManageTeamView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the team creation screen. Mutates the {@link BugemonTeam} model in response to player
 * actions, then calls {@code view.refresh()} so the view can pull the updated state from the model directly. The
 * controller never pushes data into the view.
 */
public class ManageTeamController extends Controller<ManageTeamView> implements ManageTeamView.Listener {

    private static final String NO_TEAM_SELECTED = "Pas d'équipe sélectionnée";
    private static final String TEAM_NOT_SAVED_MESSAGE = "Nouvelle équipe ou équipe existante modifiée non "
            + "sauvegardée.";

    private final TeamService teamService;
    private final PlayerState playerState;
    private final BugemonService bugemonService;

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
            BugemonService bugemonService, PlayerState playerState) {
        super(metaController, ViewLoader.load(() -> new ManageTeamView(mode)));
        this.teamService = teamService;
        this.bugemonService = bugemonService;
        this.playerState = playerState;

        this.view.setListener(this);
    }

    @Override
    protected void show() {
        if (this.tmpTeam == null) {
            this.tmpTeam = new Team();
        }
        this.playerState.getActiveTeam().ifPresent(t -> this.tmpTeam = t);
        this.updateAllUI();
        super.show();
    }

    private void updateDisplayedAvailableBugemons() {
        List<BugemonDisplayDTO> allDTOs = this.bugemonService.getPlayerBugemons().stream()
                .map(PlayerBugemon::toDisplayDTO).toList();
        Set<BugemonDisplayDTO> selectedDTOs = new HashSet<>(
                this.tmpTeam.getMembers().stream().map(PlayerBugemon::toDisplayDTO).toList());
        this.view.refreshAvailableBugemons(allDTOs, selectedDTOs);
    }

    private void updateWorkingTeamToShow() {
        List<BugemonDisplayDTO> memberDTOs = this.tmpTeam.getMembers().stream().map(PlayerBugemon::toDisplayDTO)
                .toList();
        this.view.refreshWorkingTeam(memberDTOs);
        if (this.tmpTeam.isEmpty()) {
            this.view.refreshWorkingTeamNameToShow(NO_TEAM_SELECTED);
        } else if (this.teamService.isTeamSaved(this.tmpTeam)) {
            this.view.refreshWorkingTeamNameToShow(this.tmpTeam.getName());
        } else {
            this.view.refreshWorkingTeamNameToShow(TEAM_NOT_SAVED_MESSAGE);
        }
    }

    private void updateTeamSelected() {
        this.view.refreshTeamSelected(this.playerState.getActiveTeamName().orElse(null));
    }

    private void updateDisplayedTeamNames() {
        this.view.refreshTeamNames(this.teamService.getTeamNames());
    }

    private void updateAllUI() {
        this.updateWorkingTeamToShow();
        this.updateTeamSelected();
        this.updateDisplayedTeamNames();
        this.updateDisplayedAvailableBugemons();
    }

    @Override
    public void onBugemonSelected(BugemonDisplayDTO dto) {
        PlayerBugemon playerBugemon = this.bugemonService.getPlayerBugemon(dto.getName());
        if (this.tmpTeam.contains(playerBugemon)) {
            this.tmpTeam.remove(playerBugemon);
        } else if (!this.tmpTeam.isFull()) {
            this.tmpTeam.add(playerBugemon);
        }
        this.updateAllUI();
    }

    @Override
    public void onSave(String teamName) {
        if (this.teamService.teamExists(teamName)) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
            return;
        }

        this.tmpTeam.setName(teamName);
        if (this.validateTeam()) {
            this.teamService.save(this.tmpTeam);
            this.clearTmpTeam();

            this.updateAllUI();
        }
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.teamService.deleteTeam(teamName);
            this.clearTmpTeam();
            this.updateAllUI();
        } catch (TeamNotFoundException e) {
            this.view.showDeleteTeamNoActiveTeamAlert();
        }
    }

    @Override
    public void onRename(String newName) {
        if (this.teamService.teamExists(newName)) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
            return;
        }

        try {
            this.teamService.renameTeam(this.tmpTeam, newName);
            this.updateAllUI();
        } catch (TeamNameEmptyException e) {
            this.view.showEmptyTeamNameAlert();
        } catch (TeamNotFoundException e) {
            this.view.showSelectTeamToRenameAlert();
        }
    }

    @Override
    public void onAddNewTeam() {
        this.clearTmpTeam();
        this.view.clearTeamNameToSave();
        this.updateAllUI();
    }

    @Override
    public void onModifyTeam() {
        if (this.tmpTeam == null || this.tmpTeam.isEmpty()) {
            this.view.showEmptyTeamAlert();
            return;
        }
 
        try {
            this.teamService.modify(this.tmpTeam);
            this.updateAllUI();
        } catch (TeamNotFoundException e) {
            this.view.showAlertChooseTeamToModify();
        }
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
        this.updateAllUI();
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
        boolean canLeave = this.teamService.isTeamSaved(this.tmpTeam) || this.tmpTeam.isEmpty();

        if (!canLeave && this.view.showAlertTeamChangesNotSave()) {
            this.clearTmpTeam();
            canLeave = true;
        }

        if (canLeave) {
            this.metaController.onMainMenu();
        }
    }

    private void clearTmpTeam() {
        this.tmpTeam.clear();
        this.tmpTeam.setName(null);
    }

    private boolean validateTeam() {
        if (this.tmpTeam == null || this.tmpTeam.isEmpty()) {
            this.view.showEmptyTeamAlert();
            return false;
        }

        if (this.tmpTeam.getName() == null || this.tmpTeam.getName().trim().isEmpty()) {
            this.view.showEmptyTeamNameAlert();
            return false;
        }
        return true;
    }
}
