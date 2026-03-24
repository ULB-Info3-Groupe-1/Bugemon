package ulb.controllers.combat;

import java.util.List;
import java.util.function.Consumer;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.combat.TurnResult;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.services.LevelUpService;
import ulb.services.PlayerService;
import ulb.views.combat.CombatView;

/**
 * Abstract base controller for all combat screens.
 *
 * <p>
 * Provides the shared {@link #handleCombatResult(Trainer, Trainer)} method that
 * distributes XP and navigates to the correct outcome screen. Concrete
 * subclasses drive the combat loop and call {@code view.refresh()} after each
 * model mutation; they never push data into the view directly.
 * </p>
 *
 * @param <V> the concrete {@link CombatView} subtype managed by this
 *            controller.
 */
public abstract class CombatController<V extends CombatView> extends Controller<V> {
    private Consumer<List<LevelUp>> onVictory;
    protected final AttackAnimationController animationController;
    protected final PlayerService playerService;

    protected CombatController(MetaController metaController, PlayerService playerService, V view) {
        super(metaController, view);
        this.animationController = new AttackAnimationController(view);
        this.playerService = playerService;
    }

    public void setOnVictory(Consumer<List<LevelUp>> onVictory) {
        this.onVictory = onVictory;
    }

    public abstract void startCombat();

    /**
     * Plays the attack animations contained in a turn result, then invokes
     * {@code onFinished}. If the turn has no attacks, the callback is executed
     * immediately.
     *
     * @param result        the turn result containing the attacks to animate.
     * @param playerTrainer the player's trainer, used to determine animation
     *                      direction.
     * @param onFinished    the callback to execute after all animations have
     *                      played.
     */
    protected void playTurnAnimations(TurnResult result, Trainer playerTrainer,
                                      Runnable onFinished) {
        this.animationController.playTurnAnimations(result, playerTrainer, onFinished);
    }

    /**
     * Creates a random opponent team sized to match the given player's team.
     *
     * @param playerTeamSize the size of the player's team, used to size the
     *                       opponent's team.
     *
     * @return an {@link AutoTrainer} with a randomly generated team.
     */
    protected AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(CombatService.createRandomTeam(
                this.playerService.getAllDefaultBugemons(), playerTeamSize));
    }

    /**
     * Resolves the end of a combat session by distributing XP on victory and
     * navigating to the appropriate outcome screen.
     *
     * @param winner        the winning trainer, used to determine if the player won
     *                      or lost.
     * @param playerTrainer the player's trainer, used to determine if the player
     *                      won
     *                      or lost and to distribute XP on victory.
     */
    protected void handleCombatResult(Trainer winner, Trainer playerTrainer) {
        if (winner == playerTrainer) {
            List<LevelUp> levelUps =
                    LevelUpService.distributeXpAndGetLevelUps(winner, playerTrainer);

            this.onVictory.accept(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }
}
