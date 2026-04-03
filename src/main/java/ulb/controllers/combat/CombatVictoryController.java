package ulb.controllers.combat;

import java.io.IOException;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.views.CombatVictoryView;

/**
 * Controller responsible for the victory screen shown when the player wins a combat.
 *
 * <p>
 * {@code CombatVictoryController} is displayed by the {@link MetaController} whenever the player's
 * {@link ulb.models.bugemon_team.BugemonTeam} defeats the opponent's team during a combat session. It presents the
 * player with a continue option:
 * <ul>
 * <li>{@link #cont()} — resets the team and navigates back to the main menu (or, in a future version, to a level-up
 * screen if a Bugemon levelled up during the fight).</li>
 * </ul>
 *
 * <p>
 * In all cases the trainer's team is reset via {@link MetaController#resetTeam()} so that Bugemon stats are restored to
 * their initial values before the next combat session.
 * </p>
 *
 * @see MetaController
 * @see CombatVictoryView
 * @see Controller
 */
public class CombatVictoryController extends Controller<CombatVictoryView> {
    /**
     * Constructs a {@code CombatVictoryController}, initialises its {@link CombatVictoryView}, and registers this
     * controller as the view's event handler.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its scene graph is ready before the
     * controller is used for the first time.
     * </p>
     *
     * @param metaController
     *            the application-level {@link MetaController} used for screen navigation and team state management;
     *            must not be {@code null}.
     * @throws IOException
     *             if the {@link CombatVictoryView} fails to load its FXML resource.
     */
    public CombatVictoryController(MetaController metaController) throws IOException {
        super(metaController, new CombatVictoryView());
        this.view.setOnContinue(this::continueToMainMenu);
    }

    /**
     * Callback invoked when the user presses the continue button after a victory.
     *
     * <p>
     * Resets the trainer's team to initial stats via {@link MetaController#resetTeam()} and then navigates to the
     * {@link Window#MAIN_MENU} screen.
     * </p>
     *
     * <p>
     * <em>Note:</em> if a Bugemon levelled up during the completed combat, a future implementation should navigate to a
     * dedicated level-up screen before returning to the main menu. This behaviour is not yet implemented.
     * </p>
     *
     * <p>
     * The name {@code cont} is short for <em>continue</em>, since {@code continue} is a reserved keyword in Java.
     * </p>
     */
    public void continueToMainMenu() {
        if (this.metaController.isNOTowerFlowActive()) {
            this.metaController.switchTo(Window.NOTOWER);
            return;
        }
        this.metaController.switchTo(Window.MAIN_MENU);
    }
}
