package ulb.views.combat;

import ulb.models.trainer.AutoTrainer;

/**
 * View for the automatic combat screen.
 *
 * Dialog steps are driven step by step by the controller via {@link CombatView#showStepDialog}; all player interaction
 * is dispatched through the {@link ulb.views.combat.components.SwitchMenuView.Listener} interface.
 */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;

    /**
     * Default constructor.
     */
    public AutomaticCombatView() {
        super();
    }

    /**
     * Gives the view the trainer references it needs to read from in {@link #refresh()}.
     *
     * @param newPlayer
     *            the player trainer
     * @param newOpponent
     *            the opponent trainer
     */
    public void setModel(AutoTrainer newPlayer, AutoTrainer newOpponent) {
        this.player = newPlayer;
        this.opponent = newOpponent;
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
    }
}
