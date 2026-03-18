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
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Abstract base for any participant in a combat session.
 *
 * <p>
 * A {@code Trainer} holds a reference to its {@link BugemonTeam} and tracks the
 * {@code currentBugemon} — the member currently on the field. It exposes
 * methods to query combat state (HP, initiative, alive/defeated) and to
 * delegate damage to the active Bugemon.
 * </p>
 *
 * <p>
 * Subclasses must implement {@link #getAction()} to supply the
 * {@link TurnAction} this trainer wants to take on the current turn, and
 * {@link #reactToKo()} to define how the trainer responds when its active
 * Bugemon faints mid-turn. All resolution logic lives in
 * {@link ulb.models.combat.Combat}; the trainer only <em>decides</em>.
 * </p>
 *
 * @see AutoTrainer
 * @see ManualTrainer
 * @see BugemonTeam
 * @see Bugemon
 */
public abstract class Trainer {
    protected final BugemonTeam team;
    protected Bugemon currentBugemon;

    /**
     * Constructs a {@code Trainer} with the given team, setting the first member
     * as the initial active Bugemon.
     *
     * @param team the {@link BugemonTeam} owned by this trainer; must not be
     *             {@code null} and must contain at least one Bugemon.
     */
    public Trainer(BugemonTeam team) {
        this.team = team;
        currentBugemon = team.getFirst();
    }

    // ── strategy contract ────────────────────────────────────────────────────

    /**
     * Returns the {@link TurnAction} this trainer has decided to take for the
     * current turn.
     *
     * <p>
     * Called exactly once per turn by {@link ulb.models.combat.Combat#turn()}.
     * {@link AutoTrainer} implements this by picking a random attack;
     * {@link ManualTrainer} returns the action previously queued by the
     * controller via one of the {@code queue*()} methods.
     * </p>
     *
     * @return the chosen {@link TurnAction}; never {@code null}.
     * @throws IllegalStateException if {@link ManualTrainer} has no pending
     *                               action queued.
     */
    public abstract TurnAction getAction();

    /**
     * Called by {@link ulb.models.combat.Combat} immediately after the active
     * Bugemon faints and the trainer is not yet fully defeated, so that the
     * trainer can bring in a replacement.
     *
     * <p>
     * {@link AutoTrainer} switches to a randomly chosen alive Bugemon.
     * {@link ManualTrainer} switches to the Bugemon previously set via
     * {@link ManualTrainer#registerSwitchAfterKO(Bugemon)}, or does nothing if
     * none has been set yet (the controller is then responsible for calling
     * {@link ManualTrainer#switchAfterKO(Bugemon)} directly).
     * </p>
     */
    public abstract void reactToKo();

    /**
     * Instantly defeats the entire team by reducing every Bugemon's HP to zero.
     *
     * <p>
     * Used by {@link ulb.models.combat.Combat} to resolve a
     * {@link TurnAction.ForfeitAction}: the forfeiting trainer's team is killed
     * so that {@link #isDefeated()} returns {@code true} and
     * {@link ulb.models.combat.Combat#getWinner()} can identify the winner.
     * </p>
     */
    public void killTeam() {
        team.getAll().forEach(b -> b.takeDamage(b.getHp()));
    }

    // ── shared state queries ─────────────────────────────────────────────────

    /**
     * Returns {@code true} if the trainer is defeated, i.e. every Bugemon in
     * the team has fainted (HP &le; 0).
     *
     * @return {@code true} if all Bugemons are dead, {@code false} otherwise.
     */
    public boolean isDefeated() {
        return team.stream().allMatch(b -> !b.isAlive());
    }

    /**
     * Returns {@code true} if the currently active Bugemon is still alive.
     *
     * @return {@code true} if the active Bugemon's HP is above zero.
     */
    public boolean isCurrentBugemonAlive() {
        return currentBugemon.isAlive();
    }

    /**
     * Returns {@code true} if the current {@link Bugemon}'s attack list contains
     * the specified {@link Attack}.
     *
     * @param attack the {@link Attack} to look for; must not be {@code null}.
     * @return {@code true} if the current Bugemon knows the given attack,
     *         {@code false} otherwise.
     */
    public boolean checkCurrentBugemonHasAttack(Attack attack) {
        List<Attack> attackList = currentBugemon.getAttackList();
        return attackList.contains(attack);
    }

    /**
     * Returns the initiative stat of the currently active Bugemon, used by
     * {@link ulb.services.CombatService#attackPriority(ulb.models.trainer.Trainer,
     * ulb.models.trainer.Trainer)} to determine which side strikes first.
     *
     * @return the initiative value of the active Bugemon.
     */
    public int getCurrentBugemonInitiative() {
        return currentBugemon.getInitiative();
    }

    /**
     * Returns the currently active {@link Bugemon}.
     *
     * @return the active {@link Bugemon}; never {@code null}.
     */
    public Bugemon getCurrentBugemon() {
        return currentBugemon;
    }

    /**
     * Returns the list of attacks known by the currently active {@link Bugemon}.
     *
     * @return a {@link List} of {@link Attack}s; never {@code null}.
     */
    public List<Attack> getCurrentBugemonAttackList() {
        return currentBugemon.getAttackList();
    }

    public String getCurrentBugemonName() {
        return currentBugemon.getName();
    }

    /**
     * Returns the current HP of the active Bugemon.
     *
     * @return the HP value as an {@code int}.
     */
    public int getCurrentBugemonHp() {
        return currentBugemon.getHp();
    }

    /**
     * Returns the elemental type of the currently active Bugemon.
     *
     * @return the {@link BugemonType} of the active Bugemon; never {@code null}.
     */
    public BugemonType getCurrentBugemonType() {
        return currentBugemon.getType();
    }

    /**
     * Returns the team owned by this trainer.
     *
     * @return the {@link BugemonTeam}; never {@code null}.
     */
    public BugemonTeam getTeam() {
        return team;
    }

    /**
     * Returns the number of Bugemons in this trainer's team.
     *
     * @return the team size as an {@code int}.
     */
    public int getTeamSize() {
        return team.size();
    }

    // ── mutators ─────────────────────────────────────────────────────────────

    /**
     * Applies damage to the currently active Bugemon, reducing its HP.
     *
     * @param damage the amount of damage to apply; must be &ge; 0.
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("damage must be positive");
        }
        currentBugemon.takeDamage(damage);
    }

    /**
     * Replaces the currently active Bugemon with the specified one.
     *
     * <p>
     * No validation is performed here; callers are responsible for ensuring the
     * target Bugemon belongs to this trainer's team and is alive when required.
     * </p>
     *
     * @param bugemon the {@link Bugemon} to set as the new active member;
     *                must not be {@code null}.
     */
    public void setCurrentBugemon(Bugemon bugemon) {
        currentBugemon = bugemon;
    }

    /**
     * Marks the currently active Bugemon as having participated in the current
     * combat, which is used later by
     * {@link ulb.services.LevelUpService#distributeXp(ulb.models.trainer.Trainer,
     * ulb.models.trainer.Trainer)} to distribute experience points only to Bugemons that actually
     * fought.
     */
    public void addBugemonParticipation() {
        currentBugemon.setParticipation(true);
    }
}
