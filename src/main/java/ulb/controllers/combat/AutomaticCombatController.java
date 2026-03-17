package ulb.controllers.combat;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.trainer.AutoTrainer;
import ulb.views.combat.AutomaticCombatView;

/**
 * Controller for the automatic combat screen.
 *
 * <p>
 * Drives the {@link Combat} loop via a JavaFX {@link Timeline}. After each
 * turn it calls {@code view.refresh()} so the view can pull the updated state
 * from the model; no data is pushed into the view.
 * </p>
 */
public class AutomaticCombatController extends CombatController<AutomaticCombatView> {

    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
    }

    /** Starts a complete automatic combat session and drives it to completion. */
    public void runAutoCombat(final AutoTrainer player) {
        AutoTrainer opponent = createRandomOpponent(player);
        Combat combat = new Combat(player, opponent);

        view.setModel(player, opponent, combat);
        view.refresh();

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
