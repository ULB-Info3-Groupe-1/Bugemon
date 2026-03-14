/**
 * File name : Attack.java
 * Description : Data class representing an attack of a bugemon.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import ulb.models.bugemon.effect.Effect;

/**
 * Represents an attack that a {@link Bugemon} can perform during battle.
 *
 * <p>
 * Each {@code Attack} has a unique identifier, a display name, an elemental
 * {@link BugemonType type}, a textual description, a base power value, and a
 * list of {@link Effect}s that may be applied to one or more targets when the
 * attack is used.
 * </p>
 *
 * @see Effect
 * @see BugemonType
 */
public class Attack {
    // Attributes

    private String id;

    @SerializedName("nom") private String name;

    private BugemonType type;
    private String description;

    @SerializedName("puissance") private int power;

    @SerializedName("effets") private List<Effect> effects;

    // Constructor
    /**
     * Constructor for the Attack class, initializing all attributes.
     *
     * @param id          (String) the unique identifier for the attack.
     * @param name        (String) the name of the attack.
     * @param type        (Type) the type of the attack.
     * @param description (String) a description of the attack's effects and
     *                    mechanics.
     * @param power       (int) the power of the attack, determining its damage
     *                    output.
     * @param effects      (Effect) the effects of the attack, which can apply
     *                    status changes or stat modifications to the target.
     */
    public Attack(String id, String name, BugemonType type, String description, int power,
                  List<Effect> effects) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.power = power;
        this.effects = effects;
    }

    // Getters and Setters

    /**
     * Get the unique identifier of the attack.
     *
     * @return (String) the unique identifier of the attack.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the unique identifier of the attack to a new value.
     *
     * @param id (String) the new unique identifier to set for the attack.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of the attack.
     *
     * @return (String) the name of the attack.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the attack to a new value.
     *
     * @param name (String) the new name to set for the attack.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the attack.
     *
     * @return (Type) the type of the attack.
     */
    public BugemonType getType() {
        return type;
    }

    /**
     * Set the type of the attack to a new value.
     *
     * @param type (Type) the new type to set for the attack.
     */
    public void setType(BugemonType type) {
        this.type = type;
    }

    /**
     * Get the description of the attack.
     *
     * @return (String) the description of the attack.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the attack to a new value.
     *
     * @param description (String) the new description to set for the attack.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the power of the attack.
     *
     * @return (int) the power of the attack.
     */
    public int getPower() {
        return power;
    }

    /**
     * Set the power of the attack to a new value.
     *
     * @param power (int) the new power to set for the attack.
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Returns {@code true} if this attack's effect list contains the specified
     * {@link Effect}.
     *
     * @param effect the {@link Effect} to search for; must not be {@code null}.
     * @return {@code true} if the effect is present in this attack's effect list,
     *         {@code false} otherwise.
     */
    public boolean containsEffect(Effect effect) {
        return effects.contains(effect);
    }

    /**
     * Get the effects of the attack.
     *
     * @return (Effect) the effect of the attack.
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Add an effect to the attack.
     *
     * @param effect (Effect) the new effect to set for the attack.
     */
    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Attack))
            return false;
        Attack attack = (Attack)o;
        return Objects.equals(id, attack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
