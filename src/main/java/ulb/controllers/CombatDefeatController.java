package ulb.controllers;

import java.io.IOException;

import ulb.controllers.MetaController.Window;
import ulb.models.player.Player;
import ulb.views.victory_view.CombatDefeatView;

/**
 * Controller responsible for the defeat screen shown when the player loses a
 * combat.
 *
 * <p>
 * {@code CombatDefeatController} is displayed by the {@link MetaController}
 * whenever the player's {@link ulb.models.bugemon_team.BugemonTeam} is fully
 * defeated during a combat session. It offers the player two recovery options:
 * <ul>
 *   <li>{@link #retry()} — resets the team and navigates back to the team
 *       creation screen so the player can try again.</li>
 *   <li>{@link #backToMainMenu()} — resets the team and returns to the main
 *       menu.</li>
 * </ul>
 *
 * <p>
 * In both cases the trainer's team is reset via {@link MetaController#resetTeam()}
 * so that Bugemon stats are restored to their initial values before the next
 * combat.
 * </p>
 *
 * @see MetaController
 * @see CombatDefeatView
 * @see Controller
 */
public class CombatDefeatController extends Controller<CombatDefeatView> {
    private final Player player;
    /**
     * Constructs a {@code CombatDefeatController}, initialises its
     * {@link CombatDefeatView}, and registers this controller as the view's
     * event handler.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its
     * scene graph is ready before the controller is used for the first time.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and team state management; must not
     *                       be {@code null}.
     * @throws IOException if the {@link CombatDefeatView} fails to load its FXML
     *                     resource.
     */
    public CombatDefeatController(MetaController metaController, Player player) throws IOException {
        super(metaController, new CombatDefeatView());

        this.player = player;

        this.view.setOnRetry(this::retry);
        this.view.setOnBackToMainMenu(this::backToMainMenu);
    }

    /**
     * Callback invoked when the user presses the retry button.
     *
     * <p>
     * Resets the trainer's team to initial stats via
     * {@link MetaController#resetTeam()} and then navigates to the
     * {@link Window#CREATE_TEAM} screen so the player can reassemble their team
     * and attempt the combat again.
     * </p>
     *
     * <p>
     * <em>Note:</em> full retry logic (e.g. preserving the same opponent) is not
     * yet implemented.
     * </p>
     */
    public void retry() {
        this.metaController.switchTo(Window.CREATE_TEAM);
        this.player.resetActiveTeam();
    }

    /**
     * Callback invoked when the user chooses to return to the main menu.
     *
     * <p>
     * Resets the trainer's team to initial stats via
     * {@link MetaController#resetTeam()} and then navigates to the
     * {@link Window#MAIN_MENU} screen.
     * </p>
     */
    public void backToMainMenu() {
        this.metaController.switchTo(Window.MAIN_MENU);
        this.player.resetActiveTeam();
    }
}
