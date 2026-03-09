/**
 * File name : Trainer.java
 * Description : Class representing a trainer.
 *
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Represents a trainer who owns a {@link BugemonTeam} and participates in
 * combat by fielding one active {@link Bugemon} at a time.
 *
 * <p>
 * A {@code Trainer} holds a reference to its team and tracks the
 * {@code currentBugemon} — the member currently on the field. It exposes
 * methods to query combat state (HP, initiative, alive/defeated) and to
 * delegate damage to the active Bugemon.
 * </p>
 *
 * <p>
 * {@link AutoTrainer} and {@link ManualTrainer} extend this class to add
 * automatic and player-driven action-selection strategies respectively.
 * </p>
 *
 * @see AutoTrainer
 * @see ManualTrainer
 * @see BugemonTeam
 * @see Bugemon
 */
public class Trainer {
    // Attributes

    protected BugemonTeam team;
    protected Bugemon currentBugemon;

    // Constructor

    /**
     * Constructor for the Trainer class, initializing the team of the trainer.
     *
     * @param team (BugemonTeam) the team of the trainer, which is a list of
     *             bugemon.
     */
    public Trainer(BugemonTeam team) {
        this.team = team;
        this.currentBugemon = team.getFirst();
    }

    // Methods

    /**
     * Returns true if the trainer is defeated (all bugemon in the team are
     * defeated), false otherwise.
     *
     * @return (boolean) true if the trainer is defeated, false otherwise.
     */
    public boolean isDefeated() {
        return this.team.stream().allMatch(b -> !b.isAlive());
    }

    /**
     * Returns true if the current bugemon in the team of the trainer is alive,
     * false otherwise.
     *
     * @return (boolean) true if the current bugemon is alive, false otherwise.
     */
    public boolean isCurrentBugemonAlive() {
        return this.currentBugemon.isAlive();
    }

    /**
     * Returns {@code true} if the current {@link Bugemon}'s attack list contains
     * the specified {@link Attack}.
     *
     * @param attack the {@link Attack} to look for; must not be {@code null}.
     * @return {@code true} if the current Bugemon knows the given attack,
     *         {@code false} otherwise.
     */
    public boolean currentBugemonContainsAttack(Attack attack) {
        List<Attack> attackList = this.currentBugemon.getAttackList();
        return attackList.contains(attack);
    }

    /**
     * Return the initiative of the current bugemon.
     *
     * @return (int) the initiative of the bugemon.
     */
    public int getCurrentBugemonInitiative() {
        return this.currentBugemon.getInitiative();
    }

    /**
     * Return the current bugemon in the team of the trainer.
     *
     * @return (Bugemon) the current bugemon in the team of the trainer.
     */
    public Bugemon getCurrentBugemon() {
        return this.currentBugemon;
    }

    /**
     * Return the list of attacks of the current bugemon in the team of the trainer.
     *
     * @return (AttackList) the list of attacks of the current bugemon.
     */
    public List<Attack> getCurrentBugemonAttackList() {
        return this.currentBugemon.getAttackList();
    }

    /**
     * Get the HP of the current bugemon in the team of the trainer.
     *
     * @return (int) the HP of the current bugemon in the team of the trainer.
     */
    public int getCurrentBugemonHp() {
        return this.currentBugemon.getHp();
    }

    /**
     * Takes damage to the current bugemon in the team of the trainer, reducing its
     * HP.
     *
     * @param damage (int) the amount of damage to be taken by the current bugemon
     *               in the team of the trainer, reducing its HP.
     */
    public void takeDamage(int damage) {
        this.currentBugemon.takeDamage(damage);
    }

    // Getters and setters

    /**
     * Sets the current bugemon in the team of the trainer to the specified bugemon.
     *
     * @param bugemon (Bugemon) the new current bugemon in the team of the trainer.
     */
    public void setCurrentBugemon(Bugemon bugemon) {
        this.currentBugemon = bugemon;
    }

    /**
     * Returns the team of the trainer.
     *
     * @return (BugemonTeam) the team of the trainer.
     */
    public BugemonTeam getTeam() {
        return this.team;
    }

    /**
     * Returns the size of the team of the trainer.
     *
     * @return (int) the size of the team of the trainer.
     */
    public int getTeamSize() {
        return team.size();
    }

    /**
     * Returns the bugemon with the specified ID from the team of the trainer.
     */
    public Bugemon getBugemonById(String bugemonId) {
        // TODO: is this correct usage of optional ?
        return this.team.getBugemon(bugemonId).get();
    }

    /**
     * Returns the type of the current bugemon in the team of the trainer.
     * @return (BType) the type of the current bugemon in the team of the trainer.
     */
    public BType getCurrentBugemonType() {
        return this.currentBugemon.getType();
    }
}
