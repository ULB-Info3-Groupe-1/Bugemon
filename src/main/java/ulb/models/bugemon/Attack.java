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
 * Each {@code Attack} has a unique identifier, a display name, an elemental {@link BugemonType
 * type}, a textual description, a base power value, and a list of {@link Effect}s that may be
 * applied to one or more targets when the attack is used.
 * </p>
 *
 * @see Effect
 * @see BugemonType
 */
public record Attack(

        String id,

        @SerializedName("nom") String name,

        BugemonType type,

        String description,

        @SerializedName("puissance") int power,

        @SerializedName("effets") List<Effect> effects

) {
    public Attack {
        // Defensive copy of the effects list to ensure immutability of the record
        effects = (effects == null) ? List.of() : List.copyOf(effects);
    }

    /**
     * Returns {@code true} if this attack's effect list contains the specified {@link Effect}.
     *
     * @param effect
     *            the {@link Effect} to search for; must not be {@code null}.
     * @return {@code true} if the effect is present in this attack's effect list, {@code false}
     *         otherwise.
     */
    public boolean containsEffect(Effect effect) {
        return this.effects.contains(effect);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attack)) {
            return false;
        }
        Attack attack = (Attack) o;
        return Objects.equals(this.id, attack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
