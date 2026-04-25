package ulb.models.trainer;

import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;

public class AITrainer extends Trainer {
    private Optional<TurnAction> pendingAction = Optional.empty();
    private Optional<Bugemon> bugemonTargetForSwitch = Optional.empty();
    private Inventory inventory;
    private MiniMax miniMax;

    public AITrainer(BugemonTeam team, Inventory inventory, int miniMaxDepth) {
        super(team);
        this.inventory = inventory;
        this.miniMax = new MiniMax(miniMaxDepth);
    }

    @Override
    public void reactToKo() {
    }

    /**
     * @throws IllegalStateException
     *                               if no action has been queued
     */

    @Override
    public TurnAction getAction() {
        return this.pendingAction.map(a -> {
            this.pendingAction = Optional.empty();
            return a;
        }).orElseThrow(() -> new IllegalStateException("No action has been selected for this turn."));
    }

}
