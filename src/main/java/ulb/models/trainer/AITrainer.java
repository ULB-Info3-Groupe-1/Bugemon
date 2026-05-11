package ulb.models.trainer;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.InventoryService;

public class AITrainer extends Trainer {
    private Optional<TurnAction> pendingAction = Optional.empty();
    private Optional<Bugemon> bugemonTargetForSwitch = Optional.empty();
    private final InventoryService inventoryService;
    private MiniMax miniMax;
    private Bugemon opponentActiveBugemon;
    private Trainer opponentTrainer;

    public AITrainer(BugemonTeam team, InventoryService inventoryService, int miniMaxDepth) {
        super(team);
        this.inventoryService = inventoryService;
        this.miniMax = new MiniMax(miniMaxDepth);
    }

    @Override
    public void reactToKo() {
        if (this.isDefeated()) {
            return;
        }

        if (this.bugemonTargetForSwitch.isPresent() && this.bugemonTargetForSwitch.get().isAlive()) {
            this.currentBugemon = this.bugemonTargetForSwitch.get();
            this.bugemonTargetForSwitch = Optional.empty();
            return;
        }

        Bugemon bestSwitch = null;
        if (this.opponentTrainer != null) {
            bestSwitch = this.miniMax.chooseBestSwitchAfterKo(this, this.opponentTrainer);
        }

        if (bestSwitch != null && bestSwitch.isAlive()) {
            this.currentBugemon = bestSwitch;
            return;
        }

        this.currentBugemon = this.team.aliveStream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No alive bugemon available after KO."));
    }

    /**
     * @throws IllegalStateException
     *             if no action has been queued
     */
    @Override
    public TurnAction getAction() {
        if (this.pendingAction.isEmpty()) {
            this.pendingAction = Optional.of(this.chooseActionWithMiniMax());
        }

        return this.pendingAction.map(a -> {
            this.pendingAction = Optional.empty();
            return a;
        }).orElseThrow(() -> new IllegalStateException("No action has been selected for this turn."));
    }

    @Override
    public void applyPassiveAction(TurnAction action) {
        super.applyPassiveAction(action);
        if (action instanceof TurnAction.UseItemAction(Item item)) {
            this.useItem(item);
        }
    }

    public void useItem(Item item) {
        this.inventoryService.useItem(item);
        this.currentBugemon.apply(item.effect());
    }

    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventoryService.getInventoryMap());
    }

    public void setOpponentTrainer(Trainer opponentTrainer) {
        this.opponentTrainer = opponentTrainer;
        if (opponentTrainer != null) {
            this.opponentActiveBugemon = opponentTrainer.getCurrentBugemon();
        }
    }

    public void setOpponentActiveBugemon(Bugemon opponentActiveBugemon) {
        this.opponentActiveBugemon = opponentActiveBugemon;
    }

    private TurnAction chooseActionWithMiniMax() {
        if (this.opponentTrainer == null) {
            if (this.opponentActiveBugemon == null) {
                throw new IllegalStateException("AITrainer cannot decide without an opponent state.");
            }
            if (this.currentBugemon.getAttackList().isEmpty()) {
                throw new IllegalStateException("AITrainer current bugemon has no available attack.");
            }
            return new TurnAction.AttackAction(this.currentBugemon.getAttackList().get(0));
        }

        return this.miniMax.chooseBestAction(this, this.opponentTrainer);
    }
}
