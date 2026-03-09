package ulb.controllers.combat;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.combat.AutomaticCombat;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller responsible for the automatic combat screen, where both the
 * player's team and the opponent's team act without any human input.
 *
 * <p>
 * {@code AutomaticCombatController} extends {@link CombatController} and drives
 * an {@link AutomaticCombat} session to completion in a single blocking call to
 * {@link #runAutoCombat(AutoTrainer)}. Each turn is resolved by calling
 * {@link AutomaticCombat#turn()}, which causes both {@link AutoTrainer}s to
 * select random attacks and apply their damage simultaneously, until one side
 * has no remaining alive {@link ulb.models.bugemon.Bugemon}s.
 * </p>
 *
 * <p>
 * Once the combat ends, the inherited
 * {@link CombatController#handleCombatResult(ulb.models.trainer.Trainer, ulb.models.trainer.Trainer)}
 * method is called to navigate to either the
 * {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY} or the
 * {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT} screen depending
 * on whether the player won.
 * </p>
 *
 * <p>
 * The opponent team is constructed automatically by randomly sampling
 * {@link BugemonTeam#createRandomTeam(java.util.List, int)} from the full pool
 * of available Bugemons, using the same team size as the player.
 * </p>
 *
 * @see CombatController
 * @see AutomaticCombat
 * @see AutoTrainer
 * @see AutomaticCombatView
 */
public class AutomaticCombatController
    extends CombatController<AutomaticCombatView>
{

    /**
     * Constructs an {@code AutomaticCombatController}, initialises its
     * {@link AutomaticCombatView}, and registers this controller as the view's
     * event handler.
     *
     * <p>
     * The view is instantiated here so that its FXML layout is loaded and its
     * scene graph is ready before the controller is used for the first time.
     * The parent constructor ({@link CombatController}) also pre-populates the
     * view with placeholder {@link ulb.models.bugemon.Bugemon}s and calls
     * {@link ulb.views.combat.CombatView#initCombatMode()}.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and shared state access; must not be
     *                       {@code null}.
     * @throws IOException if the {@link AutomaticCombatView} fails to load its
     *                     FXML resource.
     */
    public AutomaticCombatController(MetaController metaController)
        throws IOException {
        super(metaController, new AutomaticCombatView());
    }

    /**
     * Runs a complete automatic combat session from start to finish using the
     * given player {@link AutoTrainer}.
     *
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Calls {@link ulb.views.combat.CombatView#initCombatMode()} on the
     *       view to (re-)initialise the combat UI before the session starts.</li>
     *   <li>Builds the opponent's {@link BugemonTeam} by randomly sampling from
     *       the pool of all available Bugemons (same size as the player's team)
     *       via {@link BugemonTeam#createRandomTeam(java.util.List, int)}.</li>
     *   <li>Creates an {@link AutomaticCombat} between the player and the
     *       opponent.</li>
     *   <li>Loops until {@link AutomaticCombat#getWinner()} returns a non-{@code null}
     *       value, calling {@link AutomaticCombat#turn()} and
     *       {@link AutomaticCombat#incrementTurn()} each iteration.</li>
     *   <li>Delegates to {@link #handleCombatResult(ulb.models.trainer.Trainer,
     *       ulb.models.trainer.Trainer)} to navigate to the appropriate outcome
     *       screen.</li>
     * </ol>
     * </p>
     *
     * <p>
     * <strong>Note:</strong> this method runs the entire combat synchronously on
     * the calling thread. Because it is currently invoked on the JavaFX
     * Application Thread, very long combats may cause the UI to become
     * unresponsive. A future refactor should move the combat loop to a background
     * thread.
     * </p>
     *
     * @param player the {@link AutoTrainer} representing the player's side;
     *               must not be {@code null} and must have a non-empty team.
     */
    public void runAutoCombat(final AutoTrainer player) {
        AutoTrainer opponent = new AutoTrainer(TeamFactory.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        updateCombatView(player, opponent, null);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            try {
                Bugemon enemyBugemon = opponent.getCurrentBugemon().clone();
                
                this.view.hideDialog();
                Attack allyAttack = combat.turn();
                
                
                Trainer winner = combat.getWinner();
                combat.incrementTurn();
                updateCombatView(player, opponent, allyAttack);
                if (enemyBugemon.getId() !=  opponent.getCurrentBugemon().getId()){
                    this.view.hideDialog();
                }

                if (winner != null) {
                    timeline.stop();
                    handleCombatResult(winner, player);
                }

            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1)); // Wait 1 second before starting the combat to let the player see the initial state
        timeline.play();
    }
}
