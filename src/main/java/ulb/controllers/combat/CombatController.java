package ulb.controllers.combat;

import java.util.ArrayList;
import java.util.List;

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
    public CombatController(MetaController metaController, V view) {
        super(metaController, view);
    }

    /** Creates a random opponent team sized to match the given player's team. */
    protected AutoTrainer createRandomOpponent(Trainer player) {
        return new AutoTrainer(TeamFactory.createRandomTeam(Parser.getInstance().getBugemons(),
                                                            player.getTeamSize()));
    }

    /**
     * Resolves the end of a combat session by distributing XP on victory and
     * navigating to the appropriate outcome screen.
     */
    protected void handleCombatResult(Trainer winner, Trainer player) {
        List<LevelUp> levelUps = new ArrayList<>();
        if (winner == player) {
            LevelUpService.distributeXp(winner, player);
            List<Bugemon> participatingBugemons =
                    winner.getTeam().stream().filter(b -> b.getParticipation()).toList();
            levelUps = LevelUpService.levelUp(participatingBugemons);
            this.metaController.setLevelUp(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }
}
