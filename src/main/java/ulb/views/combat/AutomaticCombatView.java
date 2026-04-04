package ulb.views.combat;

import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;

/**
 * View for the automatic combat screen.
 *
 * Dialog steps are driven explicitly by the controller via {@link #showFirstAttackResult} and
 * {@link #showSecondAttackResult}; all user interaction is dispatched through the {@link Listener} interface.
 */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;

    private Listener listener;

    public AutomaticCombatView() {
        super();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Gives the view the trainer references it needs to read from in {@link #refresh()}. */
    public void setModel(AutoTrainer newPlayer, AutoTrainer newOpponent) {
        this.player = newPlayer;
        this.opponent = newOpponent;
    }

    @Override
    protected void initCombatMode() {
        this.hideActionMenu();
        this.setDialogNextCallback(() -> {
            if (this.listener != null) {
                this.listener.onNext();
            }
        });
    }

    /** Displays the first attack result in the dialog zone and shows the Next button. */
    public void showFirstAttackResult(TurnResult.AttackResult first) {
        this.showCombatDialog(first, java.util.Optional.empty());
    }

    /** Displays the second attack result in the dialog zone and shows the Next button. */
    public void showSecondAttackResult(TurnResult.AttackResult second) {
        this.showCombatDialog(second, java.util.Optional.empty());
    }

    @Override
    public void refresh() {
        if (this.player == null) {
            return;
        }
        this.updateTrainerBugemon(this.player.getCurrentBugemon());
        this.updateOpponentBugemon(this.opponent.getCurrentBugemon());
    }

    /** Callback interface dispatched when the player clicks the dialog's Next button. */
    public interface Listener {
        /** Called each time the player advances the combat dialog. */
        void onNext();
    }
}
