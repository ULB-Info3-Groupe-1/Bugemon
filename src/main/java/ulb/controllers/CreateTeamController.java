package ulb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;
import ulb.views.CreateTeamView;

/**
 * Controller responsible for the team creation screen, where the player
 * assembles their {@link ulb.models.bugemon_team.BugemonTeam} before entering
 * a combat.
 *
 * <p>
 * {@code CreateTeamController} manages two synchronised views:
 * <ul>
 *   <li>A grid of all available {@link ulb.models.bugemon.Bugemon}s loaded
 *       from the game's JSON data files.</li>
 *   <li>The player's current team, showing up to
 *       {@code BugemonTeam.MAX_SIZE} selected members.</li>
 * </ul>
 * <p>
 * Clicking a Bugemon toggles its membership: if the Bugemon is already in the
 * team it is removed; otherwise it is added (provided the team is not full).
 * </p>
 *
 * <p>
 * Once satisfied with their team composition, the player can start either an
 * automatic combat (via {@link #startAutoCombat()}) or a manual combat (via
 * {@link #startManualCombat()}), both of which delegate to the
 * {@link MetaController}.
 * </p>
 *
 * @see MetaController
 * @see ulb.views.CreateTeamView
 * @see Controller
 * @see ulb.models.bugemon_team.BugemonTeam
 */
public class CreateTeamController extends Controller<CreateTeamView> {
    private BugemonTeam bugemonTeam;

    /**
     * Constructs a {@code CreateTeamController}, initialises its
     * {@link ulb.views.CreateTeamView}, and populates both the full Bugemon
     * grid and the current team view.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its
     * scene graph is ready before the controller is used for the first time.
     * Both sub-views are refreshed immediately after construction so the
     * player sees up-to-date content as soon as the screen is shown.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and shared state access; must not be
     *                       {@code null}.
     * @param bugemonTeam    the {@link ulb.models.bugemon_team.BugemonTeam} instance
     *                       that this controller will mutate as the player adds or
     *                       removes members; must not be {@code null}.
     * @throws IOException if the {@link ulb.views.CreateTeamView} fails to load
     *                     its FXML resource.
     */
    public CreateTeamController(MetaController metaController, BugemonTeam bugemonTeam)
            throws IOException {
        super(metaController, new CreateTeamView());
        this.view.setController(this);

        this.bugemonTeam = bugemonTeam;

        this.updateAllBugemonsView();
        this.updateBugemonsTeamView();
    }

    /**
     * Callback invoked when the user clicks on a Bugemon in either the full
     * grid or the current team panel.
     *
     * <p>
     * The click is interpreted as a toggle:
     * <ul>
     *   <li>If the Bugemon identified by {@code id} is already in the team,
     *       it is removed.</li>
     *   <li>If the team is full, an alert dialog is shown and no change is
     *       made.</li>
     *   <li>Otherwise the Bugemon is looked up in the list of all available
     *       Bugemons and added to the team.</li>
     * </ul>
     * <p>
     * After any mutation both sub-views are refreshed to reflect the new
     * team state.
     * </p>
     *
     * @param id the unique identifier of the clicked {@link ulb.models.bugemon.Bugemon};
     *           must not be {@code null}.
     */
    public void onBugemonClicked(String id) {
        if (this.bugemonTeam.contains(id)) {
            this.bugemonTeam.removeBugemon(id);
        } else if (this.bugemonTeam.isFull()) {
            this.view.showAlert("Équipe pleine",
                                "Votre équipe est déjà pleine. Veuillez en retirer un avant "
                                        + "d'en ajouter un nouveau.");
        } else {
            Parser.getInstance()
                    .getBugemons()
                    .stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst()
                    .ifPresent(bugemon -> this.bugemonTeam.addBugemon(bugemon));
        }

        this.updateBugemonsTeamView();
        this.updateAllBugemonsView();
    }

    /**
     * Refreshes the grid that displays all available Bugemons, reflecting the
     * current selection state (i.e., which ones are already in the team).
     */
    private void updateAllBugemonsView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(Parser.getInstance().getBugemons());
        this.view.showAll(bugemonList);
    }

    /**
     * Refreshes the team panel to display the current members of the player's
     * {@link ulb.models.bugemon_team.BugemonTeam}.
     */
    private void updateBugemonsTeamView() {
        List<BugemonDTO> bugemonList = new ArrayList<>();
        bugemonList.addAll(this.bugemonTeam);
        this.view.showTeam(bugemonList);
    }

    /**
     * Callback invoked when the player requests to start an automatic combat.
     *
     * <p>
     * Delegates to {@link MetaController#launchAutoCombat()}, which validates
     * that the team is non-empty, builds the opponent team, and navigates to
     * the combat screen.
     * </p>
     */
    public void startAutoCombat() {
        if (!teamIsEmpty()) {
            this.metaController.launchAutoCombat();
        }
    }

    /**
     * Callback invoked when the player requests to start a manual combat.
     *
     * <p>
     * Delegates to {@link MetaController#launchManualCombat()}, which validates
     * that the team is non-empty, builds the opponent team, and navigates to
     * the combat screen.
     * </p>
     */
    public void startManualCombat() {
        if (!teamIsEmpty()) {
            this.metaController.launchManualCombat();
        }
    }

    /**
     * Returns {@code true} if the {@link ulb.models.bugemon.Bugemon} identified
     * by {@code bugemonId} is currently a member of the player's team.
     *
     * <p>
     * This is used by the view layer to highlight Bugemons that have already
     * been selected.
     * </p>
     *
     * @param bugemonId the unique identifier of the Bugemon to check; must not
     *                  be {@code null}.
     * @return {@code true} if the Bugemon is in the team, {@code false} otherwise.
     */
    public boolean checkBugemonInTeam(String bugemonId) {
        return (this.bugemonTeam.contains(bugemonId));
    }

    /**
     * Checks whether the player's team is valid for starting a combat (i.e., non-empty).
     * @return {@code true} if the team is valid, {@code false} otherwise.
     */
    private boolean teamIsEmpty() {
        if (this.bugemonTeam.isEmpty()) {
            this.view.showAlert(
                    "Équipe incomplète",
                    "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
            return true;
        }
        return false;
    }
}
