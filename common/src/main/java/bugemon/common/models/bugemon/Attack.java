package bugemon.common.models.bugemon;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import bugemon.common.models.effect.Effect;

/**
 * Immutable record representing an attack a {@link Bugemon} can use in battle.
 *
 * <p>
 * Attacks are loaded from JSON via Gson ({@code nom}, {@code puissance}, {@code effets} are French-named fields in the
 * data file). Equality is based solely on {@code id}.
 *
 * @param id
 *            unique identifier of the attack
 * @param name
 *            display name (mapped from JSON field {@code nom})
 * @param description
 *            human-readable description
 * @param power
 *            base damage power (mapped from JSON field {@code puissance})
 * @param type
 *            elemental type of the attack
 * @param effects
 *            list of additional {@link Effect}s applied on use (mapped from JSON field {@code effets})
 */
public record Attack(

        String id,

        @SerializedName("nom") String name,

        String description,

        @SerializedName("puissance") int power,

        ElementType type,

        @SerializedName("effets") List<Effect> effects

) implements Serializable {
    /**
     * Returns {@code true} if this attack applies at least one {@link Effect} on use.
     */
    public boolean hasEffects() {
        return !this.effects.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Attack other = (Attack) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
