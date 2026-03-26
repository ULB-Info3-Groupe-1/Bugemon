package ulb.controllers.combat;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import ulb.controllers.MetaController;
import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
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
        super(metaController, playerService, new AutomaticCombatView());
    }

    /** Starts a complete automatic combat session and drives it to completion. */
    @Override
    public void startCombat(boolean restoreHpAfterCombat) {
        this.restoreHpAfterCombat = restoreHpAfterCombat;

        AutoTrainer playerTrainer = new AutoTrainer(this.playerService.getActiveTeam());
        AutoTrainer opponentTrainer = createRandomOpponent(playerTrainer.getTeamSize());
        Combat combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(playerTrainer, opponentTrainer, combat);
        this.view.refresh();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            TurnResult turnResult = combat.turn();
            timeline.pause();

            if (combat.isFinished()) {
                timeline.stop();
                this.handleCombatResult(combat.getCombatResult(), playerTrainer);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }
}
