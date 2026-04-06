package ulb.controllers;

import java.io.IOException;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;
import ulb.views.CreateTeamView;
import ulb.views.ViewLoader;

/**
 * Controller responsible for the team creation screen. Mutates the {@link BugemonTeam} model in response to user
 * actions, then calls {@code view.refresh()} so the view can pull the updated state from the model directly. The
 * controller never pushes data into the view.
 */
public class CreateTeamController extends Controller<CreateTeamView> implements CreateTeamView.Listener {
    private static final String STR_TEAM_NAME_NOT_FOUND = "Nom d'équipe introuvable";

    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private BugemonTeam selectedTeam;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and performs an initial
     * {@link ulb.views.CreateTeamView#refresh()} to populate the Bugemon grid.
     *
     * @throws IOException
     *             if the view fails to load its FXML resource.
     */
    public CreateTeamController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) throws IOException {
        super(metaController, ViewLoader.load(CreateTeamView::new));
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.selectedTeam = new BugemonTeam();

        this.view.setListener(this);

        // TODO: is this the right way to do things now that we have listeners ?
        this.view.setModel(this.selectedTeam);
        this.view.setAllBugemonsAvailable(this.bugemonService.getAllDefaultBugemons());
        this.view.updateTeamList(this.playerService.getTeamNames());

        this.view.refresh();
    }

    @Override
    public void onBugemonSelected(Bugemon bugemon) {
        if (this.selectedTeam.contains(bugemon)) {
            this.selectedTeam.remove(bugemon);
        } else if (!this.selectedTeam.isFull()) {
            this.selectedTeam.add(new Bugemon(bugemon));
        }
        this.view.refreshTeam(this.selectedTeam);
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }

    @Override
    public void onSave() {
        if (this.teamNameIsEmpty(this.view.getTeamNameToSave())) {
            this.showEmptyNameAlert();
            return;
        }

        try {
            this.playerService.saveTeam(this.view.getTeamNameToSave(), this.selectedTeam);
            this.view.updateTeamList(this.playerService.getTeamNames());
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showAlert("Nom d'équipe déjà utilisé",
                    "Une équipe est déjà sauvée avec le nom " + this.view.getTeamNameToSave());
        }
    }

    @Override
    public void onLoad() {
        if (this.teamNameIsEmpty(this.view.getTeamNameToLoad())) {
            this.showEmptyNameAlert();
            return;
        }

        try {
            this.playerService.loadTeamAndSetActiveTeam(this.view.getTeamNameToLoad());
            this.selectedTeam = this.playerService.getActiveTeam();
            this.view.refreshTeam(this.selectedTeam);
        } catch (TeamNotFoundException e) {
            this.view.showAlert(STR_TEAM_NAME_NOT_FOUND,
                    "Aucune équipe sauvée avec le nom " + this.view.getTeamNameToLoad());
        }
    }

    @Override
    public void onDelete(String teamName) {
        try {
            this.playerService.deleteTeam(teamName);
            this.view.updateTeamList(this.playerService.getTeamNames());
            if (this.selectedTeam != null && teamName.equals(this.selectedTeam.getName())) {
                this.selectedTeam = new BugemonTeam();
                this.view.refreshTeam(this.selectedTeam);
            }
        } catch (TeamNotFoundException e) {
            this.view.showAlert(STR_TEAM_NAME_NOT_FOUND, "Aucune équipe sauvegardée avec ce nom n'a été trouvée.");
        }
    }

    @Override
    public void onRename(String oldName, String newName) {
        if (this.teamNameIsEmpty(newName)) {
            this.showEmptyNameAlert();
            return;
        }

        try {
            this.playerService.renameTeam(oldName, newName);
            this.view.updateTeamList(this.playerService.getTeamNames());
            if (this.selectedTeam != null && oldName.equals(this.selectedTeam.getName())) {
                this.selectedTeam.setName(newName);
            }
        } catch (TeamNotFoundException e) {
            this.view.showAlert(STR_TEAM_NAME_NOT_FOUND, "L'équipe que vous souhaitez renommer n'existe pas");
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showAlert("Nom d'équipe déjà utilisé",
                    "Vous avez déjà une équipe avec ce nom. Veuillez en choisir un autre.");
        }
    }

    @Override
    public void onAddNewTeam() {
        this.selectedTeam = new BugemonTeam();
        this.view.refreshTeam(this.selectedTeam);
    }

    private void showEmptyNameAlert() {
        this.view.showAlert("Nom d'équipe invalide", "Le nom d'équipe ne peut pas être vide.");
    }

    private boolean teamNameIsEmpty(String name) {
        return name.trim().isEmpty();
    }
}
