package ulb.controllers.combat;

import java.io.IOException;
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
    private Timeline turnTimeline;
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

        // Stop any existing timeline from a previous combat
        if (this.turnTimeline != null) {
            this.turnTimeline.stop();
        }

        AutoTrainer playerTrainer = new AutoTrainer(this.playerService.getActiveTeam());
        AutoTrainer opponentTrainer = createRandomOpponent(playerTrainer.getTeamSize());
        Combat combat = new Combat(playerTrainer, opponentTrainer);

        this.view.setModel(playerTrainer, opponentTrainer, combat);
        this.view.refresh();

        scheduleTurn(combat, playerTrainer, Duration.seconds(1));
    }

    /**
     * Schedules the next combat turn to happen after the specified delay.
     * Creates a fresh Timeline for each turn to avoid timing drift issues.
     *
     * @param combat the combat model
     * @param playerTrainer the player trainer
     * @param delay the delay before executing the turn
     */
    private void scheduleTurn(Combat combat, AutoTrainer playerTrainer, Duration delay) {
        this.turnTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(delay, event -> {
            TurnResult turnResult = combat.turn();

            playTurnAnimations(turnResult, playerTrainer, () -> {
                this.view.refresh();

                if (combat.getWinner().isPresent()) {
                    this.turnTimeline.stop();
                    handleCombatResult(combat.getWinner().orElseThrow(), playerTrainer);
                    return;
                }

                // Schedule the next turn after 3 seconds
                scheduleTurn(combat, playerTrainer, Duration.seconds(3));
            });
        });

        this.turnTimeline.getKeyFrames().add(keyFrame);
        this.turnTimeline.play();
    }
}
