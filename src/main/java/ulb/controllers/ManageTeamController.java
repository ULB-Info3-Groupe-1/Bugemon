package ulb.controllers;

import java.io.IOException;
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
    private final ManageTeamView.TeamFormMode mode;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and performs an initial
     * {@link ulb.views.ManageTeamView#refresh()} to populate the Bugemon grid.
     *
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public ManageTeamController(ManageTeamView.TeamFormMode mode, MetaController metaController,
            PlayerService playerService, BugemonService bugemonService) throws IOException {
        super(metaController, ViewLoader.load(ManageTeamView::new));
        this.mode = mode;
        this.playerService = playerService;
        this.bugemonService = bugemonService;

        this.view.setTeam(playerService.getActiveTeam());
        this.view.setListener(this);

    }

    @Override
    protected void show(Stage stage) {
        this.udpateAvailableBugemons();
        this.updateTeamList();
        this.view.setMode(this.mode);
        this.refresh();
        super.show(stage);
    }

    private void refresh() {
        this.view.setTeam(this.playerService.getActiveTeam());
        this.view.setIsActiveTeamSaved(this.playerService.isActiveTeamSaved());
        this.view.refresh();
    }

    public void updateTeamList() {
        this.view.setTeamList(this.playerService.getTeamNames());
    }

    private void udpateAvailableBugemons() {
        this.view.setAvailableBugemons(this.bugemonService.getAllDefaultBugemons());
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        this.playerService.addOrRemoveBugemonOfActiveTeam(bugemon);
        this.refresh();
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }

    @Override
    public void onSave(String teamName) {
        if (this.teamNameIsEmpty(teamName)) {
            this.view.showEmptyTeamNameAlert();
            return;
        }

        try {
            this.playerService.saveTeam(teamName);
            this.view.setTeamList(this.playerService.getTeamNames());
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

    @Override
    public void onLoad(String teamName) {
        if (this.teamNameIsEmpty(teamName)) {
            this.view.showEmptyTeamNameAlert();
            return;
        }

        try {
            this.playerService.loadTeamAndSetActiveTeam(teamName);
            this.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.playerService.deleteTeam(teamName);
            this.view.setTeamList(this.playerService.getTeamNames());
            this.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
    }

    @Override
    public void onRename(String oldName, String newName) {
        if (this.teamNameIsEmpty(newName)) {
            this.view.showEmptyTeamNameAlert();
            return;
        }

        try {
            this.playerService.renameTeam(oldName, newName);
            this.view.setTeamList(this.playerService.getTeamNames());
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

    private boolean teamNameIsEmpty(String name) {
        return name.trim().isEmpty();
    }

    @Override
    public void onModifyTeam() {
        try {
            this.playerService.modifyActiveTeam();
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        } catch (NoActiveTeamException e) {
            this.view.showNoActiveTeamAlert("Veuillez choisir une equipe à modifier.");
        }
    }

    @Override
    public void onStartAutomaticCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.AUTOMATIC_COMBAT);
        }
    }

    @Override
    public void onStartManualCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.MANUAL_COMBAT);
        }
    }

    @Override
    public void onStartNOTowerCombat() {
        if (!this.isActiveTeamEmpty()) {
            this.metaController.switchTo(Window.NOTOWER);
        }
    }

    private boolean isActiveTeamEmpty() {
        if (this.playerService.isActiveTeamEmpty()) {
            this.view.showNoActiveTeamAlert("Veuillez choisir une equipe pour lancer un combat.");
            return true;
        }
        return false;
    }
}
