package ulb.models.trainer;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.InventoryService;

/**
 * Human-controlled trainer. Before each {@link ulb.models.combat.Combat#turn()}, the controller enqueues exactly one
 * action via {@link #registerAttack}, {@link #registerSwitch}, {@link #registerForfeit}, or {@link #registerUseItem}.
 * The action is consumed by {@link #getAction()} and cleared; a new one must be queued every turn.
 *
 * Post-KO switches bypass the turn queue: use {@link #switchAfterKO} (immediate) or {@link #registerSwitchAfterKO}
 * (deferred, picked up by {@link #reactToKo}).
 */
public class ManualTrainer extends Trainer {
    private Optional<TurnAction> pendingAction = Optional.empty();
    private Optional<Bugemon> bugemonTargetForSwitch = Optional.empty();
    private final InventoryService inventoryService;

    private boolean forcedSwitch = false;
    private boolean switchedThisTurn = false;

    /**
     * Creates a new trainer.
     *
     * @param team
     *            the team of the trainer to control
     * @param inventoryService
     *            the inventory service to use
     */
    public ManualTrainer(BugemonTeam team, InventoryService inventoryService) {
        super(team);
        this.inventoryService = inventoryService;
    }

    /**
     * @throws IllegalStateException
     *             if no action has been queued
     */
    @Override
    public TurnAction getAction() throws IllegalStateException {
        return this.pendingAction.map(a -> {
            this.pendingAction = Optional.empty();
            return a;
        }).orElseThrow(() -> new IllegalStateException("No action has been selected for this turn."));
    }

    /**
     * Switches to the target pre-registered via {@link #registerSwitchAfterKO}. Does nothing if none was registered —
     * the controller must then call {@link #switchAfterKO} directly.
     */
    @Override
    public void reactToKo() {
        if (this.bugemonTargetForSwitch.isPresent()) {
            currentBugemon = this.bugemonTargetForSwitch.get();
            this.bugemonTargetForSwitch = Optional.empty();
        }
    }

    @Override
    public void applyPassiveAction(TurnAction action) {
        super.applyPassiveAction(action);
        if (action instanceof TurnAction.UseItemAction(Item item)) {
            this.useItem(item);
        }
    }

    /**
     * Immediately replaces the active Bugemon after a KO, bypassing the turn queue.
     *
     * @param target
     *            the bugemon to switch to
     *
     * @throws IllegalArgumentException
     *             if target is not alive
     */
    public void switchAfterKO(Bugemon target) {
        if (!target.isAlive()) {
            throw new IllegalArgumentException("The target bugemon is not alive.");
        }
        currentBugemon = target;
    }

    /**
     * Overwrites any previously queued action. Prefer typed convenience methods.
     *
     * @param action
     *            the action to queue
     */
    public void registerAction(TurnAction action) {
        this.pendingAction = Optional.of(action);
    }

    /**
     * @param attack
     *            the attack to queue
     *
     * @throws IllegalArgumentException
     *             if the attack is not in the active Bugemon's move-set
     */
    public void registerAttack(Attack attack) {
        if (!checkCurrentBugemonHasAttack(attack)) {
            throw new IllegalArgumentException("The selected attack is not in the current bugemon's attack list.");
        }
        this.registerAction(new TurnAction.AttackAction(attack));
    }

    /**
     * Queues a voluntary switch (consumes the turn; opponent still attacks).
     *
     * @param target
     *            the bugemon to switch to
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
     * Queues a forfeit (consumes the turn; opponent still attacks).
     */
    public void registerForfeit() {
        this.registerAction(new TurnAction.ForfeitAction());
    }

    /**
     * Pre-registers a KO switch target to be applied by {@link #reactToKo}.
     *
     * @param target
     *            the bugemon to switch to
     */
    public void registerSwitchAfterKO(Bugemon target) {
        this.bugemonTargetForSwitch = Optional.of(target);
    }

    /**
     * @param item
     *            the item to use
     *
     * @throws IllegalArgumentException
     *             if the item is not in the inventory
     */
    public void registerUseItem(Item item) {
        if (this.inventoryService.hasItem(item)) {
            this.registerAction(new TurnAction.UseItemAction(item));
        } else {
            throw new IllegalArgumentException("The player does not have the specified item.");
        }
    }

    /**
     * Consumes an item and applies its effect to the active Bugemon.
     *
     * @param item
     *            the item to use
     */
    public void useItem(Item item) {
        this.inventoryService.useItem(item);
        this.currentBugemon.apply(item.effect());
    }

    /**
     * @see InventoryService#getInventoryMap()
     * @return a copy of the player's inventory
     */
    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventoryService.getInventoryMap());
    }

    /**
     * Checks if an action has been queued.
     *
     * @return true if an action has been queued
     */
    public boolean hasPendingAction() {
        return this.pendingAction.isPresent();
    }

    /**
     * Checks if the trainer has been forced to switch this turn.
     *
     * @return true if the trainer has been forced to switch
     */
    public boolean isForcedToSwitch() {
        return this.forcedSwitch;
    }

    /**
     * Sets whether the trainer has been forced to switch this turn.
     *
     * @param value
     *            true if the trainer has been forced to switch
     */
    public void setForcedSwitch(boolean value) {
        this.forcedSwitch = value;
    }

    /**
     * Checks if the trainer has switched this turn.
     *
     * @return true if the trainer has switched
     */
    public boolean hasSwitchedThisTurn() {
        return this.switchedThisTurn;
    }

    /**
     * Sets whether the trainer has switched this turn.
     *
     * @param value
     *            true if the trainer has switched
     */
    public void setHasSwitchedThisTurn(boolean value) {
        this.switchedThisTurn = value;
    }

    /**
     * Checks if the trainer can voluntarily switch this turn.
     *
     * @return true if the trainer can voluntarily switch
     */
    public boolean canVoluntarilySwitch() {
        return !this.forcedSwitch && !this.switchedThisTurn;
    }
}
