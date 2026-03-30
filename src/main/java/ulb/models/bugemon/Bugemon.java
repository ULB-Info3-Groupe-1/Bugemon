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
import ulb.models.bugemon.components.AttackComponent;
import ulb.models.bugemon.components.DefenseComponent;
import ulb.models.bugemon.components.HealthComponent;
import ulb.models.bugemon.components.InitiativeComponent;
import ulb.models.bugemon.components.LevelComponent;
import ulb.models.bugemon.components.modifier.Modifier;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.level_up.Upgrade;

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
public class Bugemon implements BugemonDTO {
    /** Unique identifier of this bugemon. */
    String id;

    /** Display name of this bugemon. Serialised as {@code "nom"}. */
    @SerializedName("nom") String name;

    /** Elemental type of this bugemon. */
    BugemonType type;

    /** Path or URL to the sprite image of this bugemon. */
    String sprite;

    HealthComponent healthComponent;

    AttackComponent attackComponent;

    DefenseComponent defenseComponent;

    InitiativeComponent initiativeComponent;

    LevelComponent levelComponent;

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
    public Bugemon clone() {
        return new BugemonBuilder()
                .id(this.id)
                .name(this.name)
                .type(this.type)
                .sprite(this.sprite)
                .hp(this.healthComponent.getMaxHp())
                .attack(this.attackComponent.getAttack())
                .defense(this.defenseComponent.getDefense())
                .initiative(this.initiativeComponent.getInitiative())
                .xp(this.levelComponent.getXp())
                .level(this.levelComponent.getLevel())
                .attackList(List.copyOf(this.attackList)) // TODO: is this safe ?
                .isStarter(this.isStarter)
                .build();
    }

    // Methods

    /**
     * Apply damage to the bugemon, reducing its HP by the specified amount.
     *
     * @param damage the amount of damage to apply.
     */
    public void takeDamage(int damage) {
        this.healthComponent.decreaseHp(damage);
    }

    /**
     * Check if the bugemon is alive, which is determined by whether its HP is
     * greater than 0.
     *
     * @return {@code true} if the bugemon's current HP is greater than 0;
     *         {@code false} otherwise.
     */
    public boolean isAlive() {
        return this.healthComponent.getHp() > 0;
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
        return this.healthComponent.getHp();
    }

    /**
     * Get the maximum hit points of the bugemon (at the start of a battle).
     * @return (int) the maximum hit points of the bugemon.
     */
    public int getMaxHp() {
        return this.healthComponent.getMaxHp();
    }

    /**
     * Get the attack power of the bugemon.
     *
     * @return (int) the current attack power of the bugemon.
     */
    public int getAttack() {
        return this.attackComponent.getAttack();
    }

    /**
     * Get the defense rating of the bugemon.
     *
     * @return (int) the current defense rating of the bugemon.
     */
    public int getDefense() {
        return this.defenseComponent.getDefense();
    }

    /**
     * Get the initiative value of the bugemon, which determines turn order in
     * combat.
     *
     * @return (int) the initiative value of the bugemon.
     */
    public int getInitiative() {
        return this.initiativeComponent.getInitiative();
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
    public void resetModifiers() {
        this.healthComponent.clearModifiers();
        this.attackComponent.clearModifiers();
        this.defenseComponent.clearModifiers();
        this.initiativeComponent.clearModifiers();
    }

    @Override
    public int getLevel() {
        return this.levelComponent.getLevel();
    }

    /**
     * Restore the bugemon's HP to its initial value, without affecting other stats.
     */
    public void restoreHp() {
        this.healthComponent.restoreHp();
    }

    /**
     * Get the experience points (XP) of the bugemon.
     *
     * @return (int) the current experience points of the bugemon.
     */
    public int getXp() {
        return this.levelComponent.getXp();
    }

    /**
     * Adds xp, and returns the number of levels that have just been crossed
     *
     * @param xp the amount of experience points to add
     * @return the number of levels that have just been crossed
     */
    public int gainXp(int xp) {
        return this.levelComponent.addXp(xp);
    }

    /**
     * Applies a level-up choice to the bugemon, adding the choice's stat bonuses.
     *
     * @param choice the {@link Upgrade} to apply, containing stat bonuses
     */
    // TODO: this should be removed, a choice should know how to apply itself on a
    // bugemon instead.
    public void applyChoice(Upgrade choice) {
        this.healthComponent.increaseMaxHp(choice.hp());
        this.attackComponent.increaseAttack(choice.attack());
        this.defenseComponent.increaseDefense(choice.defense());
        this.initiativeComponent.increaseInitiative(choice.initiative());
    }

    public void addEffect(Effect effect) {
        switch (effect) {
            case EffectStatModifier e
                    -> {
                // TODO: I feel like this part should probably be done elsewhere
                EffectDuration duration = e.duration();
                Modifier modifier =
                        (duration == EffectDuration.ONE_TURN) ? new Modifier(e.modifier(), 1):
                new Modifier(e.modifier());

                switch (e.stat()) {
                    case EffectStat.HP:
                        this.healthComponent.addModifier(modifier);
                        break;
                    case EffectStat.ATTACK:
                        this.attackComponent.addModifier(modifier);
                        break;
                    case EffectStat.DEFENSE:
                        this.defenseComponent.addModifier(modifier);
                        break;
                    case EffectStat.INITIATIVE:
                        this.initiativeComponent.addModifier(modifier);
                        break;
                }
        }

            case EffectHeal e -> {
                this.healthComponent.increaseHp(e.amount());
                break;
            }
            case EffectResetMalus e -> {
                this.resetModifiers();
                break;
            }
            default -> {
                throw new RuntimeException("unknown effect");
            }
        }
    }

    public void kill() {
        this.takeDamage(this.getHp());
    }

    /**
     * Get the list of attack IDs that the bugemon can have.
     * @return (List<String>) the list of attack IDs that the bugemon can have.
     */
    public List<String> getListAttacksId() {
        return this.attackList.stream().map(Attack::id).toList();
    }
}
