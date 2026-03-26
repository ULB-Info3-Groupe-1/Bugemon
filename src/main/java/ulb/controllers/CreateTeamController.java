package ulb.controllers;

import java.io.IOException;
import java.util.logging.Logger;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.PlayerService;
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
        // TODO: handle exceptions thrown by BugemonTeam
        // + change logic
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
     * saveTeamNameInput field. If the team name is valid (not null, not empty, not already used by
     * another team owned by the user), the team is saved to the database through the PlayerService,
     * set as the active team in the PlayerService, and the view is updated to reflect any changes.
     * If the team name is invalid, an appropriate alert is shown to the user and no changes are
     * made to the active team or the view.
     */
    public void saveTeam() {
        String teamName = this.view.getTeamNameToSave();
        if (this.playerService.teamNameExists(teamName)) {
            this.selectedTeam.setName(teamName);
            this.playerService.updateTeamMembers(teamName, this.selectedTeam);
            this.playerService.setActiveTeam(this.selectedTeam);
        } else if (this.saveTeamNameIsValid(teamName)) {
            this.selectedTeam.setName(teamName);
            this.playerService.saveTeam(teamName, this.selectedTeam);
            this.playerService.setActiveTeam(this.selectedTeam);
            this.view.updateTeamList(this.playerService.getTeamNames());
        }
    }

    /**
     * Loads the team with the name specified in the view's loadTeamNameInput field, sets it as the
     * active team in the PlayerService, and updates the view to display the loaded team. If the
     * team name is invalid (null, empty, or does not correspond to an existing team), an
     * appropriate alert is shown to the user and no changes are made to the active team or the
     * view.
     */
    public void loadTeam() {
        String teamName = this.view.getTeamNameToLoad();
        if (this.loadTeamNameIsValid(teamName)) {
            this.playerService.loadTeamAndSetActiveTeam(teamName);
            this.selectedTeam = this.playerService.getActiveTeam().orElseThrow(
                    ()
                            -> new IllegalStateException("an error occured while loading the team "
                                                         + this.selectedTeam));
            this.view.refreshTeam(this.selectedTeam);
        }
    }

    public void deleteTeam(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            this.view.showAlert("Nom d'équipe invalide", "Le nom d'équipe ne peut pas être vide.");
            return;
        }
        if (!this.playerService.teamNameExists(teamName)) {
            this.view.showAlert("Nom d'équipe introuvable",
                                "Vous n'avez aucune équipe avec ce nom.");
            return;
        }
        this.playerService.deleteTeam(teamName);
        this.view.updateTeamList(this.playerService.getTeamNames());
        if (this.selectedTeam != null && teamName.equals(this.selectedTeam.getName())) {
            this.selectedTeam = new BugemonTeam();
            this.view.refreshTeam(this.selectedTeam);
        }
    }

    public void renameTeam(String oldTeamName, String newTeamName) {
        if (Logger.getGlobal().isLoggable(java.util.logging.Level.INFO)) {
            Logger.getGlobal().info(String.format("Attempting to rename team from '%s' to '%s'",
                                                  oldTeamName, newTeamName));
        }

        if (oldTeamName == null || oldTeamName.trim().isEmpty()) {
            this.view.showAlert("Nom d'équipe invalide",
                                "Le nom de l'équipe à renommer ne peut pas être vide.");
            return;
        }
        if (newTeamName == null || newTeamName.trim().isEmpty()) {
            this.view.showAlert("Nouveau nom invalide",
                                "Le nouveau nom d'équipe ne peut pas être vide.");
            return;
        }
        if (!this.playerService.teamNameExists(oldTeamName)) {
            this.view.showAlert("Nom d'équipe introuvable",
                                "Vous n'avez aucune équipe avec ce nom.");
            return;
        }
        if (this.playerService.teamNameExists(newTeamName)) {
            this.view.showAlert(
                    "Nom d'équipe déjà utilisé",
                    "Vous avez déjà une équipe avec ce nom. Veuillez en choisir un autre.");
            return;
        }
        this.playerService.renameTeam(oldTeamName, newTeamName);
        this.view.updateTeamList(this.playerService.getTeamNames());
        if (this.selectedTeam != null && oldTeamName.equals(this.selectedTeam.getName())) {
            this.selectedTeam.setName(newTeamName);
        }
    }

    public void addNewTeam() {
        this.selectedTeam = new BugemonTeam();
        this.view.refreshTeam(this.selectedTeam);
    }

    /**
     * Checks if the given team name is valid for saving a team. A valid team name must not be null,
     * empty, or consist only of whitespace, and it must not already be used by another team owned
     * by the user. If the team name is invalid, an appropriate alert is shown to the user
     * explaining the reason.
     * @param teamName the name of the team to validate for saving
     * @return true if the team name is valid for saving a team, false otherwise
     */
    private boolean saveTeamNameIsValid(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            this.view.showAlert("Nom d'équipe invalide", "Le nom d'équipe ne peut pas être vide.");
            return false;
        } else if (this.playerService.teamNameExists(teamName)) {
            this.view.showAlert(
                    "Nom d'équipe déjà utilisé",
                    "Vous avez déjà une équipe avec ce nom. Veuillez en choisir un autre.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the given team name is valid for loading a team. A valid team name must not be
     * null, empty, or consist only of whitespace, and it must correspond to an existing team owned
     * by the user. If the team name is invalid, an appropriate alert is shown to the user
     * explaining the reason.
     * @param teamName the name of the team to validate for loading
     * @return true if the team name is valid for loading a team, false otherwise
     */
    private boolean loadTeamNameIsValid(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            this.view.showAlert("Nom d'équipe invalide", "Le nom d'équipe ne peut pas être vide.");
            return false;
        } else if (!this.playerService.teamNameExists(teamName)) {
            this.view.showAlert("Nom d'équipe introuvable",
                                "Vous n'avez aucune équipe avec ce nom. Veuillez vérifier "
                                        + "l'orthographe ou en choisir un autre.");
            return false;
        }
        return true;
    }
}
