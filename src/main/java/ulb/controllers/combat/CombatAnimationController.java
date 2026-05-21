package ulb.controllers.combat;

import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.views.combat.CombatView;

/**
 * * Encapsulates all animation logic for combat so the main combat controller stays focused on game logic.
 */
public class CombatAnimationController {
    private final CombatView view;

    public CombatAnimationController(CombatView view) {
        this.view = view;
    }

    /**
     * Plays the animation corresponding to {@code step}, then invokes {@code onFinished}. Steps without a visual
     * animation (item use, switch) invoke {@code onFinished} immediately.
     *
     * @param step
     *            the step to animate.
     * @param playerTeam
     *            the player's team, used to determine animation direction (player side vs opponent side).
     * @param onFinished
     *            callback executed after the animation completes.
     */
    public void playStepAnimation(TurnStep step, CombatTeam playerTeam, Runnable onFinished) {
        switch (step) {
            case AttackStep a -> {
                boolean fromPlayer = a.attacker() == playerTeam.getActive();
                this.playAttackAnimation(fromPlayer, onFinished);
            }

            case KoStep(CombatBugemon koBugemon) -> {
                boolean isPlayerSide = koBugemon == playerTeam.getActive();
                this.playDeathAnimation(isPlayerSide, onFinished);
            }

            case SwitchStep s -> onFinished.run();

            default -> onFinished.run();
        }
    }

    public void makeBugemonReappear(boolean forPlayer) {
        if (forPlayer) {
            this.view.makePlayerBugemonReappear();
        } else {
            this.view.makeOpponentBugemonReappear();
        }
    }

    private void playAttackAnimation(boolean trainerAttacks, Runnable onFinished) {
        if (trainerAttacks) {
            this.view.playPlayerAttackAnimation(onFinished);
        } else {
            this.view.playOpponentAttackAnimation(onFinished);
        }
    }

    private void playDeathAnimation(boolean forPlayer, Runnable onFinished) {
        if (forPlayer) {
            this.view.playDeathAnimationForPlayer(onFinished);
        } else {
            this.view.playDeathAnimationForOpponent(onFinished);
        }
    }
}
