package ulb.controllers.combat;

import java.util.List;
import java.util.function.Consumer;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon;
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
 * @param <V> the concrete {@link CombatView} subtype managed by this controller.
 */
public abstract class CombatController<V extends CombatView> extends Controller<V> {
    private Consumer<List<LevelUp>> onVictory;
    protected final PlayerService playerService;

    protected CombatController(MetaController metaController, V view, PlayerService playerService) {
        super(metaController, view);
        this.playerService = playerService;
    }

    public void setOnVictory(Consumer<List<LevelUp>> onVictory) {
        this.onVictory = onVictory;
    }

    public abstract void startCombat();

    /** Creates a random opponent team sized to match the given player's team. */
    protected AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(CombatService.createRandomTeam(
                this.playerService.getAllDefaultBugemons(), playerTeamSize));
    }

    /**
     * Resolves the end of a combat session by distributing XP on victory and
     * navigating to the appropriate outcome screen.
     */
    protected void handleCombatResult(Trainer winner, Trainer playerTrainer) {
        if (winner == playerTrainer) {
            LevelUpService.distributeXp(winner, playerTrainer);
            List<Bugemon> participatingBugemons =
                    winner.getTeam().stream().filter(Bugemon::getParticipation).toList();
            List<LevelUp> levelUps = LevelUpService.levelUp(participatingBugemons);
            this.onVictory.accept(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }
}
