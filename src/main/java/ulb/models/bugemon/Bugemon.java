/**
 * File name : Bugemon.java
 * Description : Class representing a bugemon.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUp;

/**
 * This class represents a bugemon, which has an ID, name, type, and stats.
 * <p>
 * A {@code Bugemon} is the central entity of the game. It holds identifying
 * information (ID, name, type, sprite), combat statistics (HP, attack, defense,
 * initiative), a list of available {@link Attack}s, and a flag indicating
 * whether the bugemon is a starter.
 * </p>
 * <p>
 * Instances must be created via the {@link BugemonBuilder} class. The class
 * implements {@link Cloneable} to support deep-copying of bugemon instances,
 * and {@link BugemonDTO} to expose a common data-transfer interface.
 * </p>
 *
 * @see BugemonBuilder
 * @see BugemonType
 * @see Attack
 */
public class Bugemon implements BugemonDTO, Cloneable {
    /** Unique identifier of this bugemon. */
    String id;

    /** Display name of this bugemon. Serialised as {@code "nom"}. */
    @SerializedName("nom") String name;

    /** Elemental type of this bugemon. */
    BugemonType type;

    /** Path or URL to the sprite image of this bugemon. */
    String sprite;

    /**
     * The baseline stats of this bugemon, set once at construction and used to
     * reset the bugemon to its original condition.
     */
    BugemonState initialState;

    /**
     * The current (mutable) combat stats of this bugemon. These values change
     * during battles (e.g. when damage is taken).
     */
    BugemonState state;

    /**
     * Whether this bugemon is available as a starter choice. Serialised as
     * {@code "starter"}.
     */
    @SerializedName("starter") boolean isStarter;

    /**
     * The list of attacks available to this bugemon. Serialised as
     * {@code "attaques"}.
     */
    @SerializedName("attaques") List<Attack> attackList;

    /**
     * Private no-arg constructor used exclusively by the {@link BugemonBuilder}.
     * <p>
     * Direct instantiation is not supported; use {@link BugemonBuilder} instead.
     * </p>
     */
    Bugemon() {}

    /**
     * Creates and returns a deep copy of this Bugemon instance.
     * <p>
     * The cloned Bugemon will have independent copies of both the current
     * {@code state} and the {@code initialState}, ensuring that modifications
     * to the clone's state do not affect the original, and vice versa.
     * All other fields are shallow-copied via {@link Object#clone()}.
     * </p>
     *
     * @return a new {@code Bugemon} instance that is a deep copy of this object.
     * @throws CloneNotSupportedException if the object's class does not support
     *                                    the {@link Cloneable} interface.
     */
    @Override
    public Bugemon clone() throws CloneNotSupportedException {
        Bugemon cloned = (Bugemon)super.clone();
        cloned.state = new BugemonState(this.state);
        cloned.initialState = new BugemonState(this.initialState);

        return cloned;
    }

    // Methods

    /**
     * Apply damage to the bugemon, reducing its HP by the specified amount.
     *
     * @param damage the amount of damage to apply.
     */
    public void takeDamage(double damage) {
        this.state.hp -= damage;
    }

    /**
     * Check if the bugemon is alive, which is determined by whether its HP is
     * greater than 0.
     *
     * @return {@code true} if the bugemon's current HP is greater than 0;
     *         {@code false} otherwise.
     */
    public boolean isAlive() {
        return this.state.hp > 0;
    }

    /**
     * Override the equals method to compare bugemons based on their unique ID.
     *
     * @param obj the object to compare with this bugemon.
     * @return {@code true} if {@code obj} is a {@code Bugemon} with the same
     *         ID as this instance; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Bugemon other = (Bugemon)obj;
        return this.id.equals(other.id);
    }

    /**
     * Override the hashCode method to generate a hash code based on the unique ID
     * of the bugemon.
     *
     * @return an {@code int} hash code derived from the bugemon's unique ID.
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    // Getters and Setters

    /**
     * Get the unique identifier of the bugemon.
     *
     * @return (String) the unique identifier of the bugemon.
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Get the name of the bugemon.
     *
     * @return (String) the name of the bugemon.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the type of the bugemon.
     *
     * @return (Type) the type of the bugemon.
     */
    public BugemonType getType() {
        return this.type;
    }

    /**
     * Get the image link of the bugemon.
     *
     * @return (String) the sprite link of the bugemon.
     */
    public String getSpriteURL() {
        return this.sprite;
    }

    /**
     * Adds (or subtracts) {@code value} to the combat statistic identified by
     * {@code stat}.
     *
     * <p>
     * Positive values buff the stat; negative values debuff it. The change is
     * applied to the <em>current</em> (mutable) state, not the initial state,
     * so it will be undone when {@link #reset()} is called.
     * </p>
     *
     * @param stat  the {@link EffectStat} identifying which statistic to modify
     *              ({@code HP}, {@code ATTACK}, {@code DEFENSE}, or
     *              {@code INITIATIVE}).
     * @param value the signed integer delta to add to the stat; positive values
     *              buff, negative values debuff.
     * @throws IllegalArgumentException if {@code stat} does not match any known
     *                      {@link EffectStat} constant (should not occur with a
     *                      well-formed enum value).
     */
    public void editStat(EffectStat stat, int value) {
        switch (stat) {
            case EffectStat.HP:
                this.state.hp = this.state.hp + value;
                break;
            case EffectStat.ATTACK:
                this.state.attack = this.state.attack + value;
                break;
            case EffectStat.DEFENSE:
                this.state.defense = this.state.defense + value;
                break;
            case EffectStat.INITIATIVE:
                this.state.initiative = this.state.initiative + value;
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid stat key when trying to edit stat value");
        }
    }

    /**
     * Get the list of attacks that the bugemon can have.
     *
     * @return (AttackList) the list of attacks that the bugemon can have.
     */
    public List<Attack> getAttackList() {
        return this.attackList;
    }

    /**
     * Get the current hit points of the bugemon.
     *
     * @return (int) the current hit points of the bugemon.
     */
    public int getHp() {
        return this.state.hp;
    }

    /**
     * Get the maximum hit points of the bugemon (at the start of a battle).
     * @return (int) the maximum hit points of the bugemon.
     */
    public int getMaxHp() {
        return this.state.maxHp;
    }

    /**
     * Get the attack power of the bugemon.
     *
     * @return (int) the current attack power of the bugemon.
     */
    public int getAttack() {
        return this.state.attack;
    }

    /**
     * Get the defense rating of the bugemon.
     *
     * @return (int) the current defense rating of the bugemon.
     */
    public int getDefense() {
        return this.state.defense;
    }

    /**
     * Get the initiative value of the bugemon, which determines turn order in
     * combat.
     *
     * @return (int) the initiative value of the bugemon.
     */
    public int getInitiative() {
        return this.state.initiative;
    }

    /**
     * Get whether the bugemon is a starter or not.
     *
     * @return (boolean) true if the bugemon is a starter, false otherwise.
     */
    public boolean isStarter() {
        return this.isStarter;
    }

    /**
     * Reset the bugemon's current state to its initial state, restoring its original stats.
     */
    public void reset() {
        this.state = new BugemonState(this.initialState);
    }

    @Override
    public int getLevel() {
        return this.state.level;
    }

    /**
     * Restore the bugemon's HP to its initial value, without affecting other stats.
     */
    public void restoreHp() {
        this.state.hp = this.initialState.hp;
    }

    /**
     * Get the experience points (XP) of the bugemon.
     *
     * @return (int) the current experience points of the bugemon.
     */
    public int getXp() {
        return this.state.xp;
    }

    /**
     * Adds experience points to the bugemon.
     *
     * @param xp the amount of experience points to add
     */
    public void addXp(int xp) {
        this.state.xp += xp;
    }

    /**
     * Level up the bugemon, increasing its level by 1 and restoring HP.
     *
     * @return a {@link LevelUp} object representing the level-up event
     */
    public LevelUp levelUp() {
        this.state.level += 1;
        this.restoreHp();
        return new LevelUp(this);
    }

    /**
     * Applies a level-up choice to the bugemon, adding the choice's stat bonuses.
     *
     * @param choice the {@link Choice} to apply, containing stat bonuses
     */
    public void applyChoice(Choice choice) {
        this.state.hp += choice.getBonusHP();
        this.state.attack += choice.getBonusAttack();
        this.state.defense += choice.getBonusDefense();
        this.state.initiative += choice.getBonusInitiative();
    }

    /**
     * Returns whether this bugemon participated in the last combat.
     *
     * <p>
     * This flag is set to {@code true} by
     * {@link ulb.models.trainer.Trainer#addBugemonParticipation()} at the start
     * of each turn the bugemon is active, and is used by
     * {@link ulb.services.LevelUpService#distributeXp(ulb.models.trainer.Trainer,
     * ulb.models.trainer.Trainer)} to distribute experience only to bugemons that actually fought.
     * </p>
     *
     * @return {@code true} if this bugemon participated in the last combat,
     *         {@code false} otherwise.
     */
    public boolean getParticipation() {
        return this.state.participatedLastFight;
    }

    /**
     * Sets whether this bugemon participated in the last combat.
     *
     * @param participated {@code true} to mark this bugemon as having
     *                     participated; {@code false} to clear the flag.
     * @see #getParticipation()
     */
    public void setParticipation(boolean participated) {
        this.state.participatedLastFight = participated;
    }
}
