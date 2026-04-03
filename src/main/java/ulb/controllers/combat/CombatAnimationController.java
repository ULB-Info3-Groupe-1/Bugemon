package ulb.controllers.combat;

import ulb.models.combat.TurnResult;
import ulb.models.trainer.Trainer;
import ulb.views.combat.CombatView;

/** Encapsulates all animation logic for combat so the main combat controller stays focused on game logic. */
public class CombatAnimationController {
    // Attributes

    private final CombatView view;

    // Constructor

    public CombatAnimationController(CombatView view) {
        this.view = view;
    }

    // Methods

    /**
     * Plays attack animations sequentially for the turn. Executes {@code onFinished} immediately if {@code result} is
     * {@code null} or contains no attacks.
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
            this.playAttackAnimation(firstFromPlayer,
                    () -> this.playSecondAttackIfPresent(result, playerTrainer, afterAttackAnimations));
            return;
        }

        this.playSecondAttackIfPresent(result, playerTrainer, afterAttackAnimations);
    }

    private void playSecondAttackIfPresent(TurnResult result, Trainer playerTrainer, Runnable onFinished) {
        if (result.second().isPresent() && result.second().orElseThrow().wasAttack()) {
            boolean secondFromPlayer = result.second().orElseThrow().attacker() == playerTrainer;
            this.playAttackAnimation(secondFromPlayer, onFinished);
            return;
        }
        onFinished.run();
    }

    /** Returns {@code true} if the player's side was KO'd, {@code false} for opponent, {@code null} if no KO. */
    private Boolean findKoDefenderSide(TurnResult result, Trainer playerTrainer) {
        if (result.second().isPresent() && result.second().orElseThrow().wasAttack()
                && result.second().orElseThrow().defender().getCurrentBugemon().getHp() <= 0) {
            return result.second().orElseThrow().defender() == playerTrainer;
        }

        if (result.first().wasAttack() && result.first().defender().getCurrentBugemon().getHp() <= 0) {
            return result.first().defender() == playerTrainer;
        }

        return null;
    }

    private void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            this.playTrainerAttackAnimation(onFinished);
        } else {
            this.playOpponentAttackAnimation(onFinished);
        }
    }

    private void playTrainerAttackAnimation(Runnable onFinished) {
        this.view.playTrainerAttackAnimation(onFinished);
    }

    private void playOpponentAttackAnimation(Runnable onFinished) {
        this.view.playOpponentAttackAnimation(onFinished);
    }

    private void playDeathAnimationForTrainer(Runnable onFinished) {
        this.view.playDeathAnimationForTrainer(onFinished);
    }

    private void playDeathAnimationForOpponent(Runnable onFinished) {
        this.view.playDeathAnimationForOpponent(onFinished);
    }

    private void playDeathAnimation(boolean forTrainer, Runnable onFinished) {
        if (forTrainer) {
            this.playDeathAnimationForTrainer(onFinished);
        } else {
            this.playDeathAnimationForOpponent(onFinished);
        }
    }

    public void makeBugemonReappear(boolean forTrainer) {
        if (forTrainer) {
            this.view.makeTrainerBugemonReappear();
        } else {
            this.view.makeOpponentBugemonReappear();
        }
    }
}
