package ulb.models.trainer;

import java.util.Map;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon_team.Team;

public class AITrainer extends Trainer {
    private Optional<TurnAction> pendingAction = Optional.empty();
    private Optional<Bugemon> bugemonTargetForSwitch = Optional.empty();
    private Inventory inventory;
    private MiniMax miniMax;
    private boolean forcedSwitch = false;
    private boolean switchedThisTurn = false;
    private Bugemon opponentActiveBugemon;
    private Trainer opponentTrainer;

    public AITrainer(Team team, Inventory inventory, int miniMaxDepth) {
        super(team);
        this.inventory = inventory;
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
     * Overwrites any previously queued action. Prefer typed convenience methods.
     */
    public void registerAction(TurnAction action) {
        this.pendingAction = Optional.of(action);
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

    /**
     * @throws IllegalArgumentException
     *             if the attack is not in the active Bugemon's move-set
     */
    public void registerAttack(Attack attack) {
        if (!checkCurrentBugemonHasAttack(attack)) {
            throw new IllegalArgumentException("The selected attack is not in the current bugemon's attack list.");
        }
        this.registerAction(new TurnAction.AttackAction(attack));
    }

    /** Pre-registers a KO switch target to be applied by {@link #reactToKo}. */
    public void registerSwitchAfterKO(Bugemon target) {
        this.bugemonTargetForSwitch = Optional.of(target);
    }

    /**
     * Queues a voluntary switch (consumes the turn; opponent still attacks).
     *
     * @throws IllegalArgumentException
     *             if target is not alive
     */
    public void registerSwitch(Bugemon target) {
        if (!target.isAlive()) {
            throw new IllegalArgumentException("The target bugemon is not alive.");
        }
        this.registerAction(new TurnAction.SwitchAction(target));
    }

    /**
     * @throws IllegalArgumentException
     *             if the item is not in the inventory
     */
    public void registerUseItem(Item item) {
        if (this.inventory.hasItem(item)) {
            this.registerAction(new TurnAction.UseItemAction(item));
        } else {
            throw new IllegalArgumentException("The player does not have the specified item.");
        }
    }

    public void useItem(Item item) {
        this.inventory.useItem(item);
        this.currentBugemon.apply(item.effect());
    }

    public Map<Item, Integer> getInventoryMap() {
        return this.inventory.getMap();
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

    public BugemonType getOpponentActiveBugemonType() {
        return this.opponentActiveBugemon.getType();
    }

    public boolean hasPendingAction() {
        return this.pendingAction.isPresent();
    }

    public int getOpponentActiveBugemonHp() {
        return this.opponentActiveBugemon.getHp();
    }

    public int getMyActiveBugemonHp() {
        return this.currentBugemon.getHp();
    }

    public Efficiency getOpponentActiveBugemonAttackEfficiencyAgainstMine() {
        return this.opponentActiveBugemon.getType().getEfficiencyAgainst(this.currentBugemon.getType());
    }

    public Efficiency getMyActiveBugemonAttackEfficiencyAgainstOpponent() {
        return this.currentBugemon.getType().getEfficiencyAgainst(this.opponentActiveBugemon.getType());
    }

    public boolean isForcedToSwitch() {
        return this.forcedSwitch;
    }

    public void setForcedSwitch(boolean value) {
        this.forcedSwitch = value;
    }

    public boolean hasSwitchedThisTurn() {
        return this.switchedThisTurn;
    }

    public void setHasSwitchedThisTurn(boolean value) {
        this.switchedThisTurn = value;
    }

    public boolean canVoluntarilySwitch() {
        return !this.forcedSwitch && !this.switchedThisTurn;
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
