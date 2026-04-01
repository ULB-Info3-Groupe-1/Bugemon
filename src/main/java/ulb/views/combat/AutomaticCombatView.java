package ulb.views.combat;

import java.io.IOException;

import ulb.models.combat.Combat;
import ulb.models.combat.TurnResult;
import ulb.models.trainer.AutoTrainer;

/**
 * View for the automatic combat screen.
 *
 * <p>
 * Holds references to the player {@link AutoTrainer}, the opponent {@link AutoTrainer}, and the
 * {@link Combat} model. In {@link #refresh()} it reads their current state and updates the Bugemon
 * panels and dialog zone. No controller reference is held.
 * </p>
 */
public class AutomaticCombatView extends CombatView {
    private AutoTrainer player;
    private AutoTrainer opponent;
    private Combat combat;

    /**
     * Loads the shared combat FXML layout and configures it for automatic mode (hides the action
     * menu and team pane).
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
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
    }

    @Override
    public void refresh() {
        if (this.player == null) {
            return;
        }
        updateTrainerBugemon(this.player.getCurrentBugemon());
        updateOpponentBugemon(this.opponent.getCurrentBugemon());

        TurnResult last = this.combat.getLastTurnResult();
        if (last != null && last.first().wasAttack()) {
            showCombatDialog(last.first(), last.second());
        } else {
            hideDialog();
        }
    }
}
