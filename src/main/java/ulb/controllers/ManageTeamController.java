package ulb.controllers;

import java.io.IOException;
import javafx.stage.Stage;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
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
    private BugemonTeam selectedTeam;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and performs an initial
     * {@link ulb.views.ManageTeamView#refresh()} to populate the Bugemon grid.
     *
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public ManageTeamController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) throws IOException {
        super(metaController, ViewLoader.load(ManageTeamView::new));
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.selectedTeam = new BugemonTeam();

        this.view.setListener(this);
    }

    @Override
    protected void show(Stage stage) {
        this.updateTeam();
        this.udpateAvailableBugemons();
        this.updateTeamList();

        super.show(stage);
    }

    public void updateTeamList() {
        this.view.setTeamList(this.playerService.getTeamNames());
    }

    public void updateTeam() {
        this.view.setTeam(this.selectedTeam);
    }

    private void udpateAvailableBugemons() {
        this.view.setAvailableBugemons(this.bugemonService.getAllDefaultBugemons());
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        if (this.selectedTeam.contains(bugemon)) {
            this.selectedTeam.remove(bugemon);
        } else if (!this.selectedTeam.isFull()) {
            this.selectedTeam.add(new Bugemon(bugemon));
        }

        this.view.refresh();
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
            this.playerService.saveTeam(teamName, this.selectedTeam);
            this.view.setTeamList(this.playerService.getTeamNames());
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(teamName);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
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
            this.selectedTeam = this.playerService.getActiveTeam();
            this.updateTeam();
            this.view.refresh();
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(teamName);
        }
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.playerService.deleteTeam(teamName);
            this.view.setTeamList(this.playerService.getTeamNames());
            if (this.selectedTeam != null && teamName.equals(this.selectedTeam.getName())) {
                this.selectedTeam = new BugemonTeam();
                this.updateTeam();
                this.view.refresh();
            }
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
            if (this.selectedTeam != null && oldName.equals(this.selectedTeam.getName())) {
                this.selectedTeam.setName(newName);
            }
        } catch (TeamNotFoundException e) {
            this.view.showTeamNotFoundAlert(oldName);
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showTeamNameAlreadyExistsAlert(newName);
        }
    }

    @Override
    public void onAddNewTeam() {
        this.selectedTeam = new BugemonTeam();
        this.updateTeam();
        this.view.refresh();
    }

    private boolean teamNameIsEmpty(String name) {
        return name.trim().isEmpty();
    }

    @Override
    public void onModifyTeam() {
        try {
            this.playerService.modifyTeam(this.selectedTeam);
        } catch (TeamEmptyException e) {
            this.view.showEmptyTeamAlert();
        }
    }
}
