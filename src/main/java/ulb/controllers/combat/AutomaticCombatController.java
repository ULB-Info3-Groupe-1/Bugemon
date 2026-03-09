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
import ulb.views.combat.AutomaticCombatView;

public class AutomaticCombatController extends CombatController<AutomaticCombatView> {
    
    /**
     * Constructor for the AutomaticCombatController class.
     * @param metaController the MetaController instance to manage the overall application state
     * @throws IOException if an I/O error occurs during view initialization
     */
    public AutomaticCombatController(MetaController metaController) throws IOException {
        super(metaController, new AutomaticCombatView());
    }
    
    /**
     * Run an auto combat
     * @param playerTeam the team of the player
     */
    public void runAutoCombat(final AutoTrainer player) {
        AutoTrainer opponent = new AutoTrainer(TeamFactory.createRandomTeam(metaController.getAllBugemonsAvailable(), player.getTeamSize()));
        AutomaticCombat combat = new AutomaticCombat(player, opponent);

        updateCombatView(player, opponent);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            combat.turn();
            Trainer winner = combat.getWinner();
            combat.incrementTurn();
            updateCombatView(player, opponent);

            if (winner != null) {
                timeline.stop();
                handleCombatResult(winner, player);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * Update the combat view with the current state of the player's and opponent's bugemon
     * @param player the player trainer whose bugemon is being updated in the view
     * @param opponent the opponent trainer whose bugemon is being updated in the view
     */
    public void updateCombatView(AutoTrainer player, AutoTrainer opponent) {
        this.view.updateTrainerBugemon(player.getCurrentBugemon());
        this.view.updateOpponentBugemon(opponent.getCurrentBugemon());
    }
}
