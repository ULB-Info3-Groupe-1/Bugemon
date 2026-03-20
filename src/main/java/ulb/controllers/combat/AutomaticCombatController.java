package ulb.controllers.combat;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.player.Player;
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
    private Combat combat;
    private AutoTrainer playerTrainer;
    private AutoTrainer opponentTrainer;

    /**
     * Constructs an {@code AutomaticCombatController} and initialises its
     * {@link AutomaticCombatView}.
     *
     * @param metaController the application-level controller used for navigation.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public AutomaticCombatController(MetaController metaController, Player player)
            throws IOException {
        super(metaController, new AutomaticCombatView(), player);
    }

    /** Starts a complete automatic combat session and drives it to completion. */
    @Override
    public void startCombat() {
        this.playerTrainer = new AutoTrainer(player.getActiveTeam());
        this.opponentTrainer = createRandomOpponent(this.playerTrainer.getTeamSize());
        this.combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(this.playerTrainer, this.opponentTrainer, this.combat);
        this.view.refresh();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            TurnResult turnResult = combat.turn();
            timeline.pause();

            playTurnAnimations(turnResult, this.playerTrainer, () -> {
                combat.getWinner().ifPresent(winner -> {
                    timeline.stop();
                    handleCombatResult(winner, this.playerTrainer);
                });

                if (!combat.isFinished()) {
                    view.refresh();
                    timeline.play();
                }
            });
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }
}
