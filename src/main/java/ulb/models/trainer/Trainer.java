package ulb.models.trainer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Abstract base for any combat participant. Holds the {@link BugemonTeam} and tracks {@code currentBugemon}. Subclasses
 * implement {@link #getAction()} (decision strategy) and {@link #reactToKo()} (switch logic after a KO). All resolution
 * lives in {@link ulb.models.combat.Combat}.
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
     * @return a {@link TurnAction}
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

    /**
     * Applies the passive action of a {@link TurnAction.SwitchAction}.
     *
     * @param action
     *            must be a {@link TurnAction.SwitchAction}
     */
    public void applyPassiveAction(TurnAction action) {
        if (action instanceof TurnAction.SwitchAction(Bugemon target)) {
            this.setCurrentBugemon(target);
        }
    }

    /**
     * Checks if the entire team is defeated.
     *
     * @return true if the entire team is defeated
     */
    public boolean isDefeated() {
        return this.team.stream().allMatch(b -> !b.isAlive());
    }

    /**
     * Checks if the current Bugemon is alive.
     *
     * @return true if the current Bugemon is alive
     */
    public boolean isCurrentBugemonAlive() {
        return this.currentBugemon.isAlive();
    }

    /**
     * Checks if the current Bugemon has the given attack
     *
     * @param attack
     *            the attack
     * @return true if the current Bugemon has the given attack
     */
    public boolean checkCurrentBugemonHasAttack(Attack attack) {
        List<Attack> attackList = this.currentBugemon.getAttackList();
        return attackList.contains(attack);
    }

    /**
     * Returns the current Bugemon's initiative
     *
     * @return the current Bugemon's initiative
     */
    public int getCurrentBugemonInitiative() {
        return this.currentBugemon.getInitiative();
    }

    /**
     * Returns the current Bugemon
     *
     * @return the current Bugemon
     */
    public Bugemon getCurrentBugemon() {
        return this.currentBugemon;
    }

    /**
     * Returns the current Bugemon's attack list
     *
     * @return the current Bugemon's attack list
     */
    public List<Attack> getCurrentBugemonAttackList() {
        return this.currentBugemon.getAttackList();
    }

    /**
     * Returns the current Bugemon's name
     *
     * @return the current Bugemon's name
     */
    public String getCurrentBugemonName() {
        return this.currentBugemon.getName();
    }

    /**
     * Returns the current Bugemon's health
     *
     * @return the current Bugemon's health
     */
    public int getCurrentBugemonHp() {
        return this.currentBugemon.getHp();
    }

    /**
     * Returns the current Bugemon's type
     *
     * @return the current Bugemon's type
     */
    public BugemonType getCurrentBugemonType() {
        return this.currentBugemon.getType();
    }

    /**
     * Returns the team
     *
     * @return the team
     */
    public BugemonTeam getTeam() {
        return this.team;
    }

    /**
     * Returns the size of the team
     *
     * @return the size of the team
     */
    public int getTeamSize() {
        return this.team.size();
    }

    /**
     * Decrease the health of the active Bugemon.
     *
     * @param damage
     *            the amount of damage
     * @throws IllegalArgumentException
     *             if damage is negative
     */
    public void takeDamage(int damage) throws IllegalArgumentException {
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
     * Records the current Bugemon as having participated — used to distribute XP only to Bugemons that actually fought.
     */
    public void markCurrentBugemonParticipation() {
        this.participatedBugemons.add(this.currentBugemon);
    }

    /**
     * Returns the Bugemons that have participated
     *
     * @return the Bugemons that have participated
     */
    public Set<Bugemon> getParticipatingBugemons() {
        return this.participatedBugemons;
    }

    /**
     * Returns the Bugemons in the team
     *
     * @return the Bugemons in the team
     */
    public List<Bugemon> getBugemons() {
        return this.team.getAll();
    }

    /**
     * Restores the health of all the Bugemons in the team
     */
    public void restoreTeamHp() {
        this.team.restoreHp();
    }

    /**
     * Applies the given effect to all the Bugemons in the team
     *
     * @param effect
     *            the effect
     */
    public void applyEffectToCurrentTeam(Effect effect) {
        this.team.forEach(b -> b.apply(effect));
    }
}
