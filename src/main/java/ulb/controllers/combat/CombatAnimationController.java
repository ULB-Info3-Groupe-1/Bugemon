package ulb.controllers.combat;

import ulb.models.combat.TurnStep;
import ulb.models.trainer.Trainer;
import ulb.views.combat.CombatView;

/** Encapsulates all animation logic for combat so the main combat controller stays focused on game logic. */
public class CombatAnimationController {
    private final CombatView view;

    public CombatAnimationController(CombatView view) {
        this.view = view;
    }

    /**
     * Plays the animation corresponding to {@code step}, then invokes {@code onFinished}. Steps without a visual
     * animation (item use, forfeit) invoke {@code onFinished} immediately.
     *
     * @param step
     *            the step to animate.
     * @param playerTrainer
     *            used to determine animation direction (player side vs opponent side).
     * @param onFinished
     *            callback executed after the animation completes.
     */
    public void playStepAnimation(TurnStep step, Trainer playerTrainer, Runnable onFinished) {
        switch (step) {
            case TurnStep.AttackStep s -> {
                boolean fromPlayer = s.attacker() == playerTrainer;
                this.playAttackAnimation(fromPlayer, onFinished);
            }
            case TurnStep.BugemonKoStep s -> {
                boolean isPlayerSide = s.trainer() == playerTrainer;
                this.playDeathAnimation(isPlayerSide, onFinished);
            }
            case TurnStep.TrainerKoStep s -> {
                boolean isPlayerSide = s.trainerKo() == playerTrainer;
                this.playDeathAnimation(isPlayerSide, onFinished);
            }
            case TurnStep.SwitchStep s -> {
                boolean forPlayer = s.trainer() == playerTrainer;
                this.makeBugemonReappear(forPlayer);
                onFinished.run();
            }
            default -> onFinished.run();
        }
    }

    public void makeBugemonReappear(boolean forTrainer) {
        if (forTrainer) {
            this.view.makeTrainerBugemonReappear();
        } else {
            this.view.makeOpponentBugemonReappear();
        }
    }

    private void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            this.view.playTrainerAttackAnimation(onFinished);
        } else {
            this.view.playOpponentAttackAnimation(onFinished);
        }
    }

    private void playDeathAnimation(boolean forTrainer, Runnable onFinished) {
        if (forTrainer) {
            this.view.playDeathAnimationForTrainer(onFinished);
        } else {
            this.view.playDeathAnimationForOpponent(onFinished);
        }
    }
}
