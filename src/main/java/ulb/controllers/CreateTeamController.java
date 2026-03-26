package ulb.controllers;

import java.io.IOException;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.PlayerService;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;
import ulb.views.CreateTeamView;

/**
 * Controller responsible for the team creation screen.
 *
 * <p>
 * Mutates the {@link BugemonTeam} model in response to user actions, then calls
 * {@code view.refresh()} so the view can pull the updated state from the model
 * directly. The controller never pushes data into the view.
 * </p>
 */
public class CreateTeamController extends Controller<CreateTeamView> {
    private final PlayerService playerService;
    private BugemonTeam selectedTeam;

    /**
     * Constructs a {@code CreateTeamController}, wires the view callbacks, and
     * performs an initial {@link ulb.views.CreateTeamView#refresh()} to populate
     * the Bugemon grid.
     *
     * @param metaController the application-level controller used for navigation.
     * @param playerService the service used to access and mutate player data.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public CreateTeamController(MetaController metaController, PlayerService playerService)
            throws IOException {
        super(metaController, new CreateTeamView());
        this.playerService = playerService;
        this.selectedTeam = new BugemonTeam();

        this.view.setModel(this.selectedTeam);
        this.view.setValidate(this::returnToMainMenu);
        this.view.setLoad(this::loadTeam);
        this.view.setSave(this::saveTeam);
        this.view.setDelete(this::deleteTeam);
        this.view.setRename(this::renameTeam);
        this.view.setAddNewTeam(this::addNewTeam);
        this.view.setAllBugemonsAvailable(this.playerService.getAllDefaultBugemons());
        this.view.setOnGridBugemonClicked(this::toggleBugemonSelection);
        this.view.updateTeamList(this.playerService.getTeamNames());
        this.view.refresh();
    }

    /** Toggles {@code bugemon} in the player's selected team. */
    public void toggleBugemonSelection(Bugemon bugemon) {
        if (this.selectedTeam.contains(bugemon)) {
            this.selectedTeam.remove(bugemon);
        } else if (!this.selectedTeam.isFull()) {
            this.selectedTeam.add(bugemon.clone());
        }
        this.view.refreshTeam(this.selectedTeam);
    }

    /**
     * Returns the user to the main menu by switching the current view in the MetaController.
     */
    public void returnToMainMenu() {
        this.metaController.switchTo(MetaController.Window.MAIN_MENU);
    }

    /**
     * Saves the player's currently selected team under the name specified in the view's
     * saveTeamNameInput field. If the team name is valid (not null, not empty), the team is saved
     * to the database through the PlayerService and set as the active team. If the team name is
     * empty or already used by another team owned by the user, an appropriate alert is shown to
     * the user and no changes are made to the active team or the view.
     */
    public void saveTeam() {
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

    /**
     * Loads the team with the name specified in the view's loadTeamNameInput field, sets it as the
     * active team in the PlayerService, and updates the view to display the loaded team. If the
     * team name is empty or does not correspond to an existing team, an appropriate alert is shown
     * to the user and no changes are made to the active team or the view.
     */
    public void loadTeam() {
        if (this.teamNameIsEmpty(this.view.getTeamNameToLoad())) {
            this.showEmptyNameAlert();
            return;
        }

        try {
            this.playerService.loadTeamAndSetActiveTeam(this.view.getTeamNameToLoad());
            this.selectedTeam = this.playerService.getActiveTeam();
            this.view.refreshTeam(this.selectedTeam);
        } catch (TeamNotFoundException e) {
            this.view.showAlert("Nom d'équipe introuvable",
                                "Aucune équipe sauvée avec le nom " + this.view.getTeamNameToLoad());
        }
    }

    public void deleteTeam(String teamName) {
        try {
            this.playerService.deleteTeam(teamName);
            this.view.updateTeamList(this.playerService.getTeamNames());
            if (this.selectedTeam != null && teamName.equals(this.selectedTeam.getName())) {
                this.selectedTeam = new BugemonTeam();
                this.view.refreshTeam(this.selectedTeam);
            }
        } catch (TeamNotFoundException e) {
            this.view.showAlert("Nom d'équipe introuvable", "Aucune équipe sauvegardée avec ce nom n'a été trouvée.");
        }
    }

    public void renameTeam(String oldTeamName, String newTeamName) {
        if (this.teamNameIsEmpty(newTeamName)) {
            this.showEmptyNameAlert();
            return;
        }

        try {
            this.playerService.renameTeam(oldTeamName, newTeamName);
            this.view.updateTeamList(this.playerService.getTeamNames());
            if (this.selectedTeam != null && oldTeamName.equals(this.selectedTeam.getName())) {
                this.selectedTeam.setName(newTeamName);
            }
        } catch (TeamNotFoundException e) {
            this.view.showAlert("Nom d'équipe introuvable", "L'équipe que vous souhaitez renommer n'existe pas");
        } catch (TeamNameAlreadyExistsException e) {
            this.view.showAlert(
                    "Nom d'équipe déjà utilisé",
                    "Vous avez déjà une équipe avec ce nom. Veuillez en choisir un autre.");
        }
    }

    public void addNewTeam() {
        this.selectedTeam = new BugemonTeam();
        this.view.refreshTeam(this.selectedTeam);
    }

    public void showEmptyNameAlert() {
        this.view.showAlert("Nom d'équipe invalide", "Le nom d'équipe ne peut pas être vide.");
    }

    public boolean teamNameIsEmpty(String name) {
        return name.trim().isEmpty();
    }
}
