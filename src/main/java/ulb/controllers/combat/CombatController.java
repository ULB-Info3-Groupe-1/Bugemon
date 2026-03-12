package ulb.controllers.combat;

import java.util.ArrayList;
import java.util.List;

import ulb.common.Efficiency;
import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.services.LevelUpService;
import ulb.views.combat.CombatView;

public abstract class CombatController<View extends CombatView> extends Controller<View> {
    public CombatController(MetaController metaController, View view) {
        super(metaController, view);
    }

    /**
     * Resolves the end of a combat session by navigating to the appropriate
     * outcome screen based on whether the given {@code winner} is the player.
     *
     * <p>
     * The navigation rules are:
     * <ul>
     *   <li>If {@code winner == player}, the player won: navigate to
     *       {@link Window#COMBAT_VICTORY}.</li>
     *   <li>Otherwise the player lost: navigate to
     *       {@link Window#COMBAT_DEFEAT}.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This method should be called by a subclass as soon as
     * {@link ulb.models.combat.Combat#isFinished()} returns {@code true} and
     * {@link ulb.models.combat.Combat#getWinner()} returns a non-{@code null}
     * value.
     * </p>
     *
     * @param winner the {@link Trainer} that won the combat; must not be
     *               {@code null}.
     * @param player the {@link Trainer} representing the local player, used to
     *               determine whether the outcome is a victory or a defeat; must
     *               not be {@code null}.
     */
    protected void handleCombatResult(Trainer winner, Trainer player) {
        List<LevelUp> levelUps = new ArrayList<>();
        if (winner == player) {
            // TODO: Make sure to show xp gained after combat.
            // TODO: Do not forget to set manually floor and multiplier based on NO combat.
            int xp = LevelUpService.distributeXp(winner, player);

            List<Bugemon> participatingBugemons =
                    winner.getTeam().stream().filter(b -> b.getParticipation()).toList();

            levelUps = LevelUpService.levelUp(participatingBugemons);
            this.metaController.setLevelUp(levelUps);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }

    /**
     * Update the combat view with the current state of the player's and opponent's bugemon
     * @param player the player trainer whose bugemon is being updated in the view
     * @param opponent the opponent trainer whose bugemon is being updated in the view
     */
    public void updateCombatView(Trainer player, AutoTrainer opponent, Attack attack) {
        this.view.hideDialog();
        this.view.updateTrainerBugemon(player.getCurrentBugemon());
        this.view.updateOpponentBugemon(opponent.getCurrentBugemon());

        if (attack != null) {
            if (CombatService.compareBugemonType(attack.getType(), opponent.getCurrentBugemonType())
                        .equals(Efficiency.HIGH)) {
                this.view.showDialog("ATTAQUE EFFICACE: félicitation", null);
            } else if (CombatService
                               .compareBugemonType(attack.getType(),
                                                   opponent.getCurrentBugemonType())
                               .equals(Efficiency.LOW)) {
                this.view.showDialog("Peu d'effet ...", null);
            } else {
                this.view.showDialog("Dégats standards", null);
            }
        }
    }
}
