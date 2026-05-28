package bugemon.client.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import bugemon.client.views.ManageTeamView;
import bugemon.client.views.ViewLoader;
import bugemon.common.dto.display.BugemonDisplayDTO;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.team.Team;
import bugemon.server.services.BugemonService;
import bugemon.server.services.TeamService;
import bugemon.server.services.exceptions.TeamNameEmptyException;
import bugemon.server.services.exceptions.TeamNotFoundException;

/**
 * Controller responsible for the team creation and editing screen.
 *
 * <p>
 * Mutates a temporary {@link bugemon.common.models.team.Team} in response to player actions, then triggers a view
 * refresh so the view can pull the updated state. The controller never pushes data directly into the view. Operates in
 * either {@link TeamFormMode#CREATE} or {@link TeamFormMode#EDIT} mode.
 */
public class ManageTeamController extends Controller<ManageTeamView> implements ManageTeamView.Listener {

    private final TeamService teamService;
    private final PlayerState playerState;
    private final BugemonService bugemonService;

    private Team tmpTeam; // The team that gets edited in this screen

    /**
     * Operating modes for the team management form.
     *
     * <p>
     * In {@link #CREATE} mode the working team starts empty. In {@link #EDIT} mode it is seeded from the player's
     * current active team on every {@code show()} call.
     */
    public enum TeamFormMode {
        EDIT,
        CREATE
    }

    /**
     * Constructs a {@code ManageTeamController}, wires the view callbacks, and configures the view for the given mode.
     *
     * @param mode
     *            whether the screen is used for creating or editing a team
     * @param metaController
     *            the application-wide navigation controller
     * @param teamService
     *            service for team persistence
     * @param bugemonService
     *            service for loading player Bugemon data
     * @param playerState
     *            the current player state, used to read and set the active team
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
        this.playerState.getActiveTeam().ifPresent(t -> this.tmpTeam = new Team(t));
        if (this.tmpTeam == null) {
            this.tmpTeam = new Team();
        }
        this.updateAllUI();
        super.show();
    }

    /**
     * Refreshes the available-Bugemon list, marking already-selected members as selected in the view.
     */
    private void updateDisplayedAvailableBugemons() {
        List<BugemonDisplayDTO> allDTOs = this.bugemonService.getPlayerBugemons().stream()
                .map(PlayerBugemon::toDisplayDTO).toList();
        Set<String> selectedNames = this.tmpTeam.getMembers().stream().map(PlayerBugemon::getName)
                .collect(Collectors.toSet());
        Set<BugemonDisplayDTO> selectedDTOs = allDTOs.stream().filter(dto -> selectedNames.contains(dto.getName()))
                .collect(Collectors.toSet());
        this.view.refreshAvailableBugemons(allDTOs, selectedDTOs);
    }

    /**
     * Refreshes the working-team display, including member list and team-name label (showing the saved name, "not
     * saved" notice, or an empty-team placeholder).
     */
    private void updateWorkingTeamToShow() {
        List<BugemonDisplayDTO> memberDTOs = this.tmpTeam.getMembers().stream().map(PlayerBugemon::toDisplayDTO)
                .toList();
        this.view.refreshWorkingTeam(memberDTOs);
        if (this.tmpTeam.isEmpty()) {
            this.view.refreshWorkingTeamNameNoTeamSelected();
        } else if (this.teamService.isTeamSaved(this.tmpTeam)) {
            this.view.refreshWorkingTeamNameToShow(this.tmpTeam.getName());
        } else {
            this.view.refreshWorkingTeamNameTeamNotSaved();
        }
    }

    /** Updates the active-team indicator in the view. */
    private void updateTeamSelected() {
        this.view.refreshTeamSelected(this.playerState.getActiveTeamName().orElse(null));
    }

    /** Refreshes the list of saved team names shown in the view. */
    private void updateDisplayedTeamNames() {
        this.view.refreshTeamNames(this.teamService.getTeamNames());
    }

    /** Calls all four view-refresh helpers in the correct order. */
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
        if (teamName.isBlank()) {
            this.view.showEmptyTeamNameAlert();
            return;
        }
        if (this.tmpTeam.isEmpty()) {
            this.view.showEmptyTeamAlert();
            return;
        }
        boolean isUpdate = teamName.equals(this.tmpTeam.getName());
        if (!isUpdate && this.teamService.teamExists(teamName)) {
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
            this.teamService.setActiveTeam(teamName);
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

    /**
     * Executes the given action only when the player has a non-empty active team selected; shows an alert otherwise.
     *
     * @param combat
     *            the action to run when the guard passes
     */
    private void executeIfActiveTeamNotEmpty(Runnable combat) {
        if (this.playerState.getActiveTeam().isEmpty()) {
            this.view.showAlertChooseTeamToLaunchCombat();
        } else {
            combat.run();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        if (!this.teamService.isTeamSaved(this.tmpTeam) && !this.tmpTeam.isEmpty()) {
            if (this.view.showAlertTeamChangesNotSave()) {
                this.clearTmpTeam();
            } else {
                return;
            }
        }
        this.metaController.onMainMenu();

    }

    /** Resets the working team to an empty, nameless state. */
    private void clearTmpTeam() {
        this.tmpTeam = new Team();
    }

    /**
     * Validates that the working team is non-null, non-empty, and has a non-blank name. Shows the appropriate alert and
     * returns {@code false} on the first failure.
     *
     * @return {@code true} if the team passes all checks
     */
    private boolean validateTeam() {
        if (this.tmpTeam == null || this.tmpTeam.isEmpty()) {
            this.view.showEmptyTeamAlert();
            return false;
        }

        if (this.tmpTeam.getName() == null || this.tmpTeam.getName().isBlank()) {
            this.view.showEmptyTeamNameAlert();
            return false;
        }
        return true;
    }
}
