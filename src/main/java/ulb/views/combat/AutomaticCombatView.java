package ulb.views.combat;

import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;

/**
 * View for the automatic combat screen.
 *
 * <p>
 * Holds references to the player {@link AutoTrainer}, the opponent {@link AutoTrainer}, and the {@link Combat} model.
 * In {@link #refresh()} it reads their current state and updates the Bugemon panels and dialog zone. No controller
 * reference is held.
 * </p>
 */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;
    private Combat combat;

    public AutomaticCombatView() {
        super();
    }

    /** Gives the view the model objects it needs to read from in {@link #refresh()}. */
    public void setModel(AutoTrainer newPlayer, AutoTrainer newOpponent, Combat newCombat) {
        this.player = newPlayer;
        this.opponent = newOpponent;
        this.combat = newCombat;
    }

    @Override
    protected void initCombatMode() {
        this.hideActionMenu();
    }

    @Override
    public void refresh() {
        if (this.player == null) {
            return;
        }
        this.updateTrainerBugemon(this.player.getCurrentBugemon());
        this.updateOpponentBugemon(this.opponent.getCurrentBugemon());

        TurnResult last = this.combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            this.showCombatDialog(last.first(), last.second());
        } else {
            this.hideDialog();
        }
    }
}
