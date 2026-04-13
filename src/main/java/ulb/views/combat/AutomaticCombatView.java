package ulb.views.combat;

import ulb.models.trainer.AutoTrainer;

/** View for the automatic combat screen; the action menu is hidden since both trainers are AI-controlled. */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;

    public AutomaticCombatView() {
        super();
    }

    /** Gives the view the trainer references it needs to read from in {@link #refresh()}. */
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
