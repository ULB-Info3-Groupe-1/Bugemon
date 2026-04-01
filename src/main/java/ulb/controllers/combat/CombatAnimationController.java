package ulb.controllers.combat;

import ulb.models.combat.TurnResult;
import ulb.models.trainer.Trainer;
import ulb.views.combat.CombatView;

/**
 * Class that manages attack animations for combat sequences.
 *
 * <p>
 * This controller encapsulates all animation logic related to bugemon attacks, keeping the main
 * combat controller clean and focused on game logic. It handles playing animations sequentially for
 * turn results containing one or two attacks.
 * </p>
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * CombatAnimationController animController = new CombatAnimationController(view);
 * animController.playTurnAnimations(turnResult, playerTrainer, () -> {
 *     // Continue with game logic after animations complete
 * });
 * </pre>
 * </p>
 */
public class CombatAnimationController {
    // Attributes

    private final CombatView view;

    // Constructor

    /**
     * Creates an animation controller for the given combat view.
     *
     * @param view
     *            the {@link CombatView} where animations will be displayed.
     */
    public CombatAnimationController(CombatView view) {
        this.view = view;
    }

    // Methods

    /**
     * Plays all attack animations from a turn result sequentially.
     *
     * <p>
     * If the turn has no attacks, the callback is executed immediately. If there are one or two
     * attacks, they are animated in order (attacking sprite lunges toward its opponent), and
     * {@code onFinished} is called after all animations complete.
     * </p>
     *
     * @param result
     *            the turn result containing the attack(s) to animate.
     * @param playerTrainer
     *            the player trainer, used to determine which sprite should lunge (forward = player
     *            attacks, backward = opponent attacks).
     * @param onFinished
     *            callback executed once all animations are complete; must not be {@code null}.
     */
    public void playTurnAnimations(TurnResult result, Trainer playerTrainer, Runnable onFinished) {
        if (result == null) {
            onFinished.run();
            return;
        }

        Runnable afterAttackAnimations = () -> {
            Boolean koOnTrainerSide = this.findKoDefenderSide(result, playerTrainer);
            if (koOnTrainerSide == null) {
                onFinished.run();
                return;
            }
            this.playDeathAnimation(koOnTrainerSide, onFinished);
        };

        if (result.first().wasAttack()) {
            boolean firstFromPlayer = result.first().attacker() == playerTrainer;
            this.playAttackAnimation(firstFromPlayer, () -> this.playSecondAttackIfPresent(result,
                    playerTrainer, afterAttackAnimations));
            return;
        }

        this.playSecondAttackIfPresent(result, playerTrainer, afterAttackAnimations);
    }

    /**
     * Plays the second attack animation if a second attack is present in the turn result, then
     * executes the callback.
     *
     * @param result
     *            the turn result containing the attack to check for a second attack.
     * @param playerTrainer
     *            the player trainer, used to determine which sprite should lunge if a second attack
     *            is present.
     * @param onFinished
     *            callback executed once the second attack animation completes or immediately if no
     *            second attack is present; must not be {@code null}.
     */
    private void playSecondAttackIfPresent(TurnResult result, Trainer playerTrainer,
            Runnable onFinished) {
        if (result.second().isPresent() && result.second().orElseThrow().wasAttack()) {
            boolean secondFromPlayer = result.second().orElseThrow().attacker() == playerTrainer;
            this.playAttackAnimation(secondFromPlayer, onFinished);
            return;
        }
        onFinished.run();
    }

    /**
     * Determines if a KO occurred in the turn result and on which side (player or opponent).
     *
     * @param result
     *            the turn result to check for KO occurrences.
     * @param playerTrainer
     *            the player trainer.
     * @return {@code true} if the player's Bugemon was knocked out, {@code false} if the opponent's
     *         Bugemon was knocked out, or {@code null} if no KO occurred.
     */
    private Boolean findKoDefenderSide(TurnResult result, Trainer playerTrainer) {
        if (result.second().isPresent() && result.second().orElseThrow().wasAttack()
                && result.second().orElseThrow().defender().getCurrentBugemon().getHp() <= 0) {
            return result.second().orElseThrow().defender() == playerTrainer;
        }

        if (result.first().wasAttack()
                && result.first().defender().getCurrentBugemon().getHp() <= 0) {
            return result.first().defender() == playerTrainer;
        }

        return null;
    }

    /**
     * Plays a single attack animation.
     *
     * <p>
     * Determines which sprite should animate based on who is attacking, then plays the
     * corresponding lunge animation.
     * </p>
     *
     * @param trainerAttacks
     *            {@code true} to animate the trainer's sprite, {@code false} to animate the
     *            opponent's sprite.
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            this.playTrainerAttackAnimation(onFinished);
        } else {
            this.playOpponentAttackAnimation(onFinished);
        }
    }

    /**
     * Plays a lunge animation on the player's sprite (slide toward the opponent then return).
     *
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playTrainerAttackAnimation(Runnable onFinished) {
        this.view.playTrainerAttackAnimation(onFinished);
    }

    /**
     * Plays a lunge animation on the opponent's sprite (slide toward the player then return).
     *
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playOpponentAttackAnimation(Runnable onFinished) {
        this.view.playOpponentAttackAnimation(onFinished);
    }

    /**
     * Plays the death animation for the trainer's active Bugemon if {@code forTrainer} is
     * {@code true}, or for the opponent's active Bugemon if {@code forTrainer} is {@code false}.
     *
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playDeathAnimationForTrainer(Runnable onFinished) {
        this.view.playDeathAnimationForTrainer(onFinished);
    }

    /**
     * Plays the death animation for the opponent's active Bugemon.
     *
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playDeathAnimationForOpponent(Runnable onFinished) {
        this.view.playDeathAnimationForOpponent(onFinished);
    }

    /**
     * Plays the death animation for the trainer's active Bugemon if {@code forTrainer} is
     * {@code true}, or for the opponent's active Bugemon if {@code forTrainer} is {@code false}.
     *
     * @param forTrainer
     *            {@code true} to play the trainer's Bugemon death animation, {@code false} to play
     *            the opponent's Bugemon death animation.
     * @param onFinished
     *            callback executed once the animation completes.
     */
    private void playDeathAnimation(boolean forTrainer, Runnable onFinished) {
        if (forTrainer) {
            this.playDeathAnimationForTrainer(onFinished);
        } else {
            this.playDeathAnimationForOpponent(onFinished);
        }
    }

    /**
     * Makes the trainer's active Bugemon reappear if {@code forTrainer} is {@code true}, or the
     * opponent's active Bugemon reappear if {@code forTrainer} is {@code false}.
     *
     * @param forTrainer
     *            {@code true} to make the trainer's Bugemon reappear, {@code false} to make the
     *            opponent's Bugemon reappear.
     */
    public void makeBugemonReappear(boolean forTrainer) {
        if (forTrainer) {
            this.view.makeTrainerBugemonReappear();
        } else {
            this.view.makeOpponentBugemonReappear();
        }
    }
}
