package ulb.models.trainer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Abstract base for any combat participant. Holds the {@link BugemonTeam} and tracks {@code currentBugemon}. Subclasses
 * implement {@link #getAction()} (decision strategy) and {@link #reactToKo()} (switch logic after a KO). All
 * resolution lives in {@link ulb.models.combat.Combat}.
 *
 * @see AutoTrainer
 * @see ManualTrainer
 */
public abstract class Trainer {
    protected final BugemonTeam team;
    protected Bugemon currentBugemon;
    Set<Bugemon> participatedBugemons = new HashSet<>();

    /**
     * Sets the first team member as the initial active Bugemon.
     *
     * @param team
     *            must not be {@code null} and must contain at least one Bugemon
     */
    protected Trainer(BugemonTeam team) {
        this.team = team;
        this.currentBugemon = team.getFirst();
    }

    /**
     * Returns the action chosen for this turn. Called once per turn by {@link ulb.models.combat.Combat#turn()}.
     *
     * @throws IllegalStateException
     *             if {@link ManualTrainer} has no action queued
     */
    public abstract TurnAction getAction();

    /**
     * Called immediately after the active Bugemon faints (and the trainer is not yet fully defeated), so the trainer
     * can bring in a replacement.
     */
    public abstract void reactToKo();

    /** Kills the entire team — used to resolve a {@link TurnAction.ForfeitAction}. */
    public void killTeam() {
        this.team.killAll();
    }

    public void applyPassiveAction(TurnAction action) {
        if (action instanceof TurnAction.SwitchAction(Bugemon target)) {
            this.setCurrentBugemon(target);
        }
    }

    public boolean isDefeated() {
        return this.team.stream().allMatch(b -> !b.isAlive());
    }

    public boolean isCurrentBugemonAlive() {
        return this.currentBugemon.isAlive();
    }

    public boolean checkCurrentBugemonHasAttack(Attack attack) {
        List<Attack> attackList = this.currentBugemon.getAttackList();
        return attackList.contains(attack);
    }

    public int getCurrentBugemonInitiative() {
        return this.currentBugemon.getInitiative();
    }

    public Bugemon getCurrentBugemon() {
        return this.currentBugemon;
    }

    public List<Attack> getCurrentBugemonAttackList() {
        return this.currentBugemon.getAttackList();
    }

    public String getCurrentBugemonName() {
        return this.currentBugemon.getName();
    }

    public int getCurrentBugemonHp() {
        return this.currentBugemon.getHp();
    }

    public BugemonType getCurrentBugemonType() {
        return this.currentBugemon.getType();
    }

    public BugemonTeam getTeam() {
        return this.team;
    }

    public int getTeamSize() {
        return this.team.size();
    }

    /**
     * @throws IllegalArgumentException
     *             if damage is negative
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("damage must be positive");
        }
        this.currentBugemon.takeDamage(damage);
    }

    /**
     * Switches the active Bugemon. No validation — caller ensures the target is alive and in the team.
     */
    public void setCurrentBugemon(Bugemon bugemon) {
        this.currentBugemon = bugemon;
    }

    /**
     * Records the current Bugemon as having participated — used by {@link ulb.services.LevelUpService} to distribute XP
     * only to Bugemons that actually fought.
     */
    public void markCurrentBugemonParticipation() {
        this.participatedBugemons.add(this.currentBugemon);
    }

    public Set<Bugemon> getParticipatingBugemons() {
        return this.participatedBugemons;
    }

    public List<Bugemon> getBugemons() {
        return this.team.getAll();
    }
}
