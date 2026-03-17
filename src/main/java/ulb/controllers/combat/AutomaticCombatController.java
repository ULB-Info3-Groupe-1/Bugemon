package ulb.controllers.combat;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import ulb.controllers.MetaController;
import ulb.factory.TeamFactory;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;
import ulb.utils.Parser;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller responsible for the automatic combat screen, where both the
 * player's team and the opponent's team act without any human input.
 *
 * <p>
 * {@code AutomaticCombatController} extends {@link CombatController} and drives
 * a {@link Combat} session to completion via a JavaFX {@link Timeline}.
 * Each tick of the timeline resolves one full turn by calling
 * {@link Combat#turn()}, which causes both {@link AutoTrainer}s to select
 * random attacks and apply their damage simultaneously, until one side has no
 * remaining alive {@link ulb.models.bugemon.Bugemon}s.
 * </p>
 *
 * <p>
 * The turn loop is non-blocking: a new {@link KeyFrame} fires every 3 seconds
 * on the JavaFX Application Thread, keeping the UI responsive between turns.
 * An initial delay of 1 second lets the player see the starting state before
 * the first turn fires.
 * </p>
 *
 * <p>
 * Once {@link Combat#getWinner()} returns a non-empty
 * {@link java.util.Optional},
 * the timeline is stopped and the inherited
 * {@link CombatController#handleCombatResult(ulb.models.trainer.Trainer,
 * ulb.models.trainer.Trainer)} method navigates to either
 * {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY} or
 * {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT} depending on
 * whether the player won.
 * </p>
 *
 * <p>
 * The opponent team is constructed automatically by randomly sampling from
 * the full pool of available Bugemons via
 * {@link ulb.factory.TeamFactory#createRandomTeam(java.util.List, int)},
 * using the same team size as the player.
 * </p>
 *
 * @see CombatController
 * @see Combat
 * @see AutoTrainer
 * @see AutomaticCombatView
 */
public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    /**
     * Constructs an {@code AutomaticCombatController}, instantiates its
     * {@link AutomaticCombatView}, and wires the view into the controller
     * hierarchy.
     *
     * <p>
     * The view's FXML layout is loaded at construction time so that the scene
     * graph is ready before the controller is ever used.
     * </p>
     *
     * @param metaController the application-level {@link MetaController} used for
     *                       screen navigation and shared state access; must not be
     *                       {@code null}.
     * @throws IOException if the {@link AutomaticCombatView} fails to load its
     *                     FXML resource.
     */
    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
    }

    /**
     * Starts a complete automatic combat session for the given player
     * {@link AutoTrainer} and drives it to completion via a {@link Timeline}.
     *
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Builds the opponent's team by randomly sampling from the pool of all
     * available Bugemons (same size as the player's team) via
     * {@link ulb.factory.TeamFactory#createRandomTeam(java.util.List, int)}.</li>
     * <li>Creates a {@link Combat} between the player and the opponent.</li>
     * <li>Initialises the view with the starting state via
     * {@link #updateCombatView(ulb.models.trainer.Trainer,
     * ulb.models.trainer.AutoTrainer, ulb.models.bugemon.Attack)}.</li>
     * <li>Schedules a {@link KeyFrame} that fires every 3 seconds:
     * <ul>
     * <li>Calls {@link Combat#turn()} and retrieves the
     * {@link TurnResult}.</li>
     * <li>Refreshes both Bugemon panels in the view.</li>
     * <li>Displays the efficiency dialog for the first hit of the turn
     * if an attack occurred.</li>
     * <li>If {@link Combat#getWinner()} is now present, stops the
     * timeline and delegates to
     * {@link #handleCombatResult(ulb.models.trainer.Trainer,
     * ulb.models.trainer.Trainer)}.</li>
     * </ul>
     * </li>
     * </ol>
     *
     * @param player the {@link AutoTrainer} representing the player's side;
     *               must not be {@code null} and must have a non-empty team.
     */
    public void runAutoCombat(final AutoTrainer player) {
        AutoTrainer opponent = new AutoTrainer(TeamFactory.createRandomTeam(
                Parser.getInstance().getBugemons(), player.getTeamSize()));
        Combat combat = new Combat(player, opponent);

        updateCombatView(player, opponent, null);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            TurnResult turnResult = combat.turn();
            animateTurn(turnResult, player, opponent,
                    () -> finalizeTurn(combat, timeline, player, opponent));
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }

    /**
     * Formats the efficiency message for an attack result.
     * 
     * @param combat   the {@link TurnResult.AttackResult} to format; must not be
     *                 {@code null} and must represent an attack.
     * @param timeline the {@link Timeline} driving the combat; must not be
     *                 {@code null}.
     * @param player   the player's {@link AutoTrainer}; must not be {@code null}.
     * @param opponent the opponent's {@link AutoTrainer}; must not be {@code null}.
     */
    private void finalizeTurn(Combat combat, Timeline timeline, AutoTrainer player,
            AutoTrainer opponent) {
        view.updateTrainerBugemon(player.getCurrentBugemon());
        view.updateOpponentBugemon(opponent.getCurrentBugemon());

        combat.getWinner().ifPresent(winner -> {
            timeline.stop();
            handleCombatResult(winner, player);
        });
    }
}
