package ulb.controllers.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.factory.TeamFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.LevelUpService;
import ulb.utils.Parser;
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

    public CombatController(MetaController metaController, V view) {
        super(metaController, view);
    }

    public void setOnVictory(Consumer<List<LevelUp>> onVictory) {
        this.onVictory = onVictory;
    }

    public abstract void startCombat();

    /** Creates a random opponent team sized to match the given player's team. */
    protected AutoTrainer createRandomOpponent(int playerTeamSize) {
        return new AutoTrainer(
                TeamFactory.createRandomTeam(Parser.getInstance().getBugemons(), playerTeamSize));
    }

    /**
     * Resolves the end of a combat session by distributing XP on victory and
     * navigating to the appropriate outcome screen.
     */
    protected void handleCombatResult(Trainer winner, Trainer playerTrainer) {
        List<LevelUp> levelUps = new ArrayList<>();
        if (winner == playerTrainer) {
            LevelUpService.distributeXp(winner, playerTrainer);
            List<Bugemon> participatingBugemons =
                    winner.getTeam().stream().filter(b -> b.getParticipation()).toList();
            levelUps = LevelUpService.levelUp(participatingBugemons);
            this.onVictory.accept(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }
}
