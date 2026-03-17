package ulb.views.combat;

import java.io.IOException;

import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;

/**
 * View for the automatic combat screen.
 *
 * <p>
 * Holds references to the player {@link AutoTrainer}, the opponent
 * {@link AutoTrainer}, and the {@link Combat} model. In {@link #refresh()} it
 * reads their current state and updates the Bugemon panels and dialog zone.
 * No controller reference is held.
 * </p>
 */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;
    private Combat combat;

    public AutomaticCombatView() throws IOException {
        super();
        this.initCombatMode();
    }

    /** Gives the view the model objects it needs to read from in {@link #refresh()}. */
    public void setModel(AutoTrainer player, AutoTrainer opponent, Combat combat) {
        this.player = player;
        this.opponent = opponent;
        this.combat = combat;
    }

    @Override
    protected void initCombatMode() {
        this.actionMenuView.setVisible(false);
        this.actionMenuView.setManaged(false);
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }

    @Override
    public void refresh() {
        if (player == null)
            return;
        updateTrainerBugemon(player.getCurrentBugemon());
        updateOpponentBugemon(opponent.getCurrentBugemon());

        TurnResult last = combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            showCombatDialog(last.first(), last.second());
        } else {
            hideDialog();
        }
    }
}
