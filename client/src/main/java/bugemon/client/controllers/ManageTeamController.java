package bugemon.client.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteTeamService;
import bugemon.client.services.TeamScreenData;
import bugemon.client.views.ManageTeamView;
import bugemon.client.views.ViewLoader;
import bugemon.common.dto.display.BugemonDisplayDTO;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.team.Team;
import bugemon.common.net.TeamOpResponsePacket;

/**
 * Controller responsible for the team creation and editing screen.
 *
 * <p>
 * On display it fetches a {@link TeamScreenData} snapshot (selectable Bugemons + saved teams) in one round-trip and
 * answers all read queries locally against it. The working {@link Team} is mutated in memory; persistence operations
 * (save / delete / rename / modify / set-active) are sent asynchronously through {@link RemoteTeamService}, after which
 * the snapshot is reloaded so the view reflects the server state. Operates in either {@link TeamFormMode#CREATE} or
 * {@link TeamFormMode#EDIT} mode.
 */
public class ManageTeamController extends Controller<ManageTeamView> implements ManageTeamView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(ManageTeamController.class);

    private final RemoteTeamService teamService;
    private final PlayerState playerState;

    private Team tmpTeam; // The team that gets edited in this screen
    private TeamScreenData data; // Cached server snapshot backing all read queries

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
     *            remote facade for team data and persistence
     * @param playerState
     *            the current player state, used to read and set the active team
     */
    public ManageTeamController(TeamFormMode mode, MetaController metaController, RemoteTeamService teamService,
            PlayerState playerState) {
        super(metaController, ViewLoader.load(() -> new ManageTeamView(mode)));
        this.teamService = teamService;
        this.playerState = playerState;
        this.view.setListener(this);
    }

    @Override
    protected void show() {
        this.playerState.getActiveTeam().ifPresent(t -> this.tmpTeam = new Team(t));
        if (this.tmpTeam == null) {
            this.tmpTeam = new Team();
        }
        super.show();
        this.reload();
    }

    /**
     * Fetches the team-screen snapshot and refreshes the whole UI on the JavaFX thread once it arrives.
     */
    private void reload() {
        this.teamService.getScreenData().whenComplete((screenData, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to load team screen data", error);
                return;
            }
            this.data = screenData;
            this.updateAllUI();
        }));
    }

    /**
     * Refreshes the available-Bugemon list, marking already-selected members as selected in the view.
     */
    private void updateDisplayedAvailableBugemons() {
        List<BugemonDisplayDTO> allDTOs = this.data.getPlayerBugemons().stream().map(PlayerBugemon::toDisplayDTO)
                .toList();
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
        } else if (this.data.isTeamSaved(this.tmpTeam)) {
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
        this.view.refreshTeamNames(this.data.getTeamNames());
    }

    /** Calls all four view-refresh helpers in the correct order. Does nothing until the snapshot has loaded. */
    private void updateAllUI() {
        if (this.data == null) {
            return;
        }
        this.updateWorkingTeamToShow();
        this.updateTeamSelected();
        this.updateDisplayedTeamNames();
        this.updateDisplayedAvailableBugemons();
    }

    @Override
    public void onBugemonSelected(BugemonDisplayDTO dto) {
        if (this.data == null) {
            return;
        }
        PlayerBugemon playerBugemon = this.data.getPlayerBugemon(dto.getName());
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
        if (!isUpdate && this.data.teamExists(teamName)) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
            return;
        }
        this.tmpTeam.setName(teamName);
        this.teamService.save(this.tmpTeam).whenComplete((ignored, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to save team {}", teamName, error);
                return;
            }
            this.clearTmpTeam();
            this.reload();
        }));
    }

    @Override
    public void onDelete(String teamName) {
        this.teamService.deleteTeam(teamName).whenComplete((status, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to delete team {}", teamName, error);
                return;
            }
            if (status == TeamOpResponsePacket.Status.NOT_FOUND) {
                this.view.showDeleteTeamNoActiveTeamAlert();
                return;
            }
            this.clearTmpTeam();
            this.reload();
        }));
    }

    @Override
    public void onRename(String newName) {
        if (this.data.teamExists(newName)) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
            return;
        }
        this.teamService.renameTeam(this.tmpTeam, newName).whenComplete((status, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to rename team to {}", newName, error);
                return;
            }
            this.handleRenameStatus(status);
        }));
    }

    private void handleRenameStatus(TeamOpResponsePacket.Status status) {
        switch (status) {
            case OK -> this.reload();
            case NAME_EMPTY -> this.view.showEmptyTeamNameAlert();
            case NOT_FOUND -> this.view.showSelectTeamToRenameAlert();
            default -> LOG.warn("Unexpected rename status: {}", status);
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
        this.teamService.modify(this.tmpTeam).whenComplete((status, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to modify team", error);
                return;
            }
            if (status == TeamOpResponsePacket.Status.NOT_FOUND) {
                this.view.showAlertChooseTeamToModify();
                return;
            }
            this.reload();
        }));
    }

    @Override
    public void onTeamSelected(String teamName) {
        Optional<Team> selected = this.data.getTeam(teamName);
        if (selected.isEmpty()) {
            this.view.showTeamNotFoundAlert(teamName);
            this.updateAllUI();
            return;
        }
        this.playerState.setActiveTeam(selected.get());
        this.tmpTeam = new Team(selected.get());
        this.updateAllUI();
        this.teamService.setActiveTeam(teamName).whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.error("Failed to set active team {}", teamName, error);
            }
        });
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
        boolean saved = this.data != null && this.data.isTeamSaved(this.tmpTeam);
        if (!saved && !this.tmpTeam.isEmpty()) {
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
}
