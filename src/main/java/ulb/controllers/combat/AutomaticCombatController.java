package ulb.controllers.combat;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.trainer.AutoTrainer;
import ulb.services.PlayerService;
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
    /**
     * Constructs an {@code AutomaticCombatController} and initialises its
     * {@link AutomaticCombatView}.
     *
     * @param metaController the application-level controller used for navigation.
     * @throws IOException if the view fails to load its FXML resource.
     */
    public AutomaticCombatController(MetaController metaController, PlayerService playerService)
            throws IOException {
        super(metaController, new AutomaticCombatView(), playerService);
    }

    /** Starts a complete automatic combat session and drives it to completion. */
    @Override
    public void startCombat() {
        AutoTrainer playerTrainer = new AutoTrainer(playerService.getActiveTeam());
        AutoTrainer opponentTrainer = createRandomOpponent(playerTrainer.getTeamSize());
        Combat combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(playerTrainer, opponentTrainer, combat);
        this.view.refresh();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            combat.turn();

            combat.getWinner().ifPresent(winner -> {
                timeline.stop();
                handleCombatResult(winner, playerTrainer);
            });

            if (!combat.isFinished()) {
                view.refresh();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }
}
