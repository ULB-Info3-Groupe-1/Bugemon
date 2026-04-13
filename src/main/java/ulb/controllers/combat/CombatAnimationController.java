package ulb.controllers.combat;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
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
            case TurnStep.AttackStep(Trainer attacker, Attack attack, Efficiency efficiency) -> {
                boolean fromPlayer = attacker == playerTrainer;
                this.playAttackAnimation(fromPlayer, onFinished);
            }

            case TurnStep.BugemonKoStep(Trainer trainer) -> {
                boolean isPlayerSide = trainer == playerTrainer;
                this.playDeathAnimation(isPlayerSide, onFinished);
            }

            case TurnStep.TrainerKoStep(Trainer trainerKo) -> {
                boolean isPlayerSide = trainerKo == playerTrainer;
                this.playDeathAnimation(isPlayerSide, onFinished);
            }

            case TurnStep.ForfeitStep(Trainer trainer) -> {
                boolean isPlayerForfeiting = trainer == playerTrainer;
                this.playDeathAnimation(isPlayerForfeiting, onFinished);
            }

            case TurnStep.SwitchStep(Trainer trainer, Bugemon bugemon) -> onFinished.run();

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
