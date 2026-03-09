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

        updateCombatView(player, opponent, null);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
            this.view.hideDialog();
            Attack allyAttack = combat.turn();
            
            
            Trainer winner = combat.getWinner();
            combat.incrementTurn();
            updateCombatView(player, opponent, allyAttack);
            

            if (winner != null) {
                timeline.stop();
                handleCombatResult(winner, player);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setDelay(Duration.seconds(1)); // Wait 1 second before starting the combat to let the player see the initial state
        timeline.play();
    }
}
