package ulb.controllers.combat;

import java.util.Map;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.views.combat.CombatView;

public abstract class CombatController<View extends CombatView> extends Controller<View> {
    /**
     * Enum representing the efficiency of an attack based on the types of the attack and the
     * defending bugemon.
     */
    public enum AttackEfficiency { EFFICIENT, INFERIOR, NEUTRAL }

    /**
     * Map representing the type advantages in the combat system. Each key is a bugemon type and its
     * corresponding value is the type it is strong against.
     */
    private static final Map<BType, BType> STRONG_AGAINST =
            Map.of(BType.FLORA, BType.AQUA, BType.AQUA, BType.PYRO, BType.PYRO, BType.LITHO,
                   BType.LITHO, BType.FLORA);

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
        if (winner == player) {
            this.metaController.switchTo(Window.COMBAT_VICTORY);
        } else {
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
        }
    }

    /**
     * Update the combat view with the current state of the player's and opponent's bugemon
     * @param player the player trainer whose bugemon is being updated in the view
     * @param opponent the opponent trainer whose bugemon is being updated in the view
     */
    public void updateCombatView(Trainer player, AutoTrainer opponent) {
        this.view.updateTrainerBugemon(player.getCurrentBugemon());
        this.view.updateOpponentBugemon(opponent.getCurrentBugemon());
    }

    /**
     * Determine the efficiency of the trainer attack against the opponent's bugemon based on their
     * types
     * @param attackType the type of the attack selected by the player
     * @param defenseBugemonType the type of the opponent's bugemon
     */
    public AttackEfficiency isAttackEfficient(BType attackType, BType defenseBugemonType) {
        if (STRONG_AGAINST.get(attackType).equals(defenseBugemonType)) {
            return AttackEfficiency.EFFICIENT;
        } else if (STRONG_AGAINST.get(defenseBugemonType).equals(attackType)) {
            return AttackEfficiency.INFERIOR;
        }
        return AttackEfficiency.NEUTRAL;
    }
}
