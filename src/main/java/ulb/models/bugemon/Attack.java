package ulb.models.bugemon;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import ulb.models.effect.Effect;

/**
 * Immutable record representing an attack a {@link Bugemon} can use in battle.
 */
public record Attack(

        String id,

        @SerializedName("nom") String name,

        String description,

        @SerializedName("puissance") int power,

        ElementType type,

        @SerializedName("effets") List<Effect> effects

) {
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
