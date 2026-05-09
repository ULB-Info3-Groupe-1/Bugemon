package ulb.models.trainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.skills.Skill;

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
    private final Inventory inventory;
    private final List<Skill> unlockedStatBonusSkills = new ArrayList<>();

    private boolean forcedSwitch = false;
    private boolean switchedThisTurn = false;

    public ManualTrainer(BugemonTeam team, Inventory inventory) {
        super(team);
        this.inventory = inventory;
    }

    public ManualTrainer(BugemonTeam team, Inventory inventory, List<Skill> unlockedStatBonusSkills) {
        super(team);
        this.inventory = inventory;
        this.unlockedStatBonusSkills.addAll(unlockedStatBonusSkills);
    }

    /**
     * @throws IllegalStateException
     *             if no action has been queued
     */
    @Override
    public TurnAction getAction() {
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
     */
    public void registerAction(TurnAction action) {
        this.pendingAction = Optional.of(action);
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

    public void registerForfeit() {
        this.registerAction(new TurnAction.ForfeitAction());
    }

    /** Pre-registers a KO switch target to be applied by {@link #reactToKo}. */
    public void registerSwitchAfterKO(Bugemon target) {
        this.bugemonTargetForSwitch = Optional.of(target);
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

    public boolean hasPendingAction() {
        return this.pendingAction.isPresent();
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

    public Map<Item, Integer> getInventoryMap() {
        return Collections.unmodifiableMap(this.inventory.getMap());
    }

    // Skill management

    public void setUnlockedSkills(List<Skill> unlockedSkills) {
        this.unlockedStatBonusSkills.clear();
        this.unlockedStatBonusSkills.addAll(unlockedSkills);
    }
}
