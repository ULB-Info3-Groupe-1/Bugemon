package ulb.controllers.combat;

import ulb.models.combat.TurnResult;
import ulb.models.trainer.Trainer;
import ulb.views.combat.CombatView;

/**
 * Manages attack animations for combat sequences.
 *
 * <p>
 * This controller encapsulates all animation logic related to bugemon attacks,
 * keeping the main combat controller clean and focused on game logic. It handles
 * playing animations sequentially for turn results containing one or two attacks.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 * AttackAnimationController animController = new AttackAnimationController(view);
 * animController.playTurnAnimations(turnResult, playerTrainer, () -> {
 *     // Continue with game logic after animations complete
 * });
 * </pre>
 * </p>
 */
public class AttackAnimationController {
    private final CombatView view;

    /**
     * Creates an animation controller for the given combat view.
     *
     * @param view the {@link CombatView} where animations will be displayed.
     */
    public AttackAnimationController(CombatView view) {
        this.view = view;
    }

    /**
     * Plays all attack animations from a turn result sequentially.
     *
     * <p>
     * If the turn has no attacks, the callback is executed immediately. If there
     * are one or two attacks, they are animated in order (attacking sprite lunges
     * toward its opponent), and {@code onFinished} is called after all animations
     * complete.
     * </p>
     *
     * @param result        the turn result containing the attack(s) to animate.
     * @param playerTrainer the player's trainer, used to determine which sprite
     *                      should lunge (forward = player attacks, backward =
     *                      opponent attacks).
     * @param onFinished    callback executed once all animations are complete;
     *                      must not be {@code null}.
     */
    public void playTurnAnimations(TurnResult result, Trainer playerTrainer, Runnable onFinished) {
        if (result == null || !result.first().wasAttack()) {
            onFinished.run();
            return;
        }

        boolean firstFromPlayer = result.first().attacker() == playerTrainer;
        playAttackAnimation(firstFromPlayer, () -> {
            if (result.second().isPresent() && result.second().orElseThrow().wasAttack()) {
                boolean secondFromPlayer =
                        result.second().orElseThrow().attacker() == playerTrainer;
                playAttackAnimation(secondFromPlayer, onFinished);
            } else {
                onFinished.run();
            }
        });
    }

    /**
     * Plays a single attack animation.
     *
     * <p>
     * Determines which sprite should animate based on who is attacking, then
     * plays the corresponding lunge animation.
     * </p>
     *
     * @param trainerAttacks {@code true} to animate the trainer's sprite,
     *                       {@code false} to animate the opponent's sprite.
     * @param onFinished     callback executed once the animation completes.
     */
    private void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            playTrainerAttackAnimation(onFinished);
        } else {
            playOpponentAttackAnimation(onFinished);
        }
    }

    /**
     * Plays a lunge animation on the player's sprite (slide toward the opponent
     * then return).
     *
     * @param onFinished callback executed once the animation completes.
     */
    private void playTrainerAttackAnimation(Runnable onFinished) {
        view.playTrainerAttackAnimation(onFinished);
    }

    /**
     * Plays a lunge animation on the opponent's sprite (slide toward the player
     * then return).
     *
     * @param onFinished callback executed once the animation completes.
     */
    private void playOpponentAttackAnimation(Runnable onFinished) {
        view.playOpponentAttackAnimation(onFinished);
    }
}
