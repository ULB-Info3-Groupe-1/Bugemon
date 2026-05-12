package ulb.models.bugemon;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/** Immutable record representing an attack a {@link Bugemon} can use in battle. Equality is based on {@link #id}. */
public record Attack(

        String id,

        @SerializedName("nom") String name,

        String description,

        @SerializedName("puissance") int power,

        BugemonType type,

        @SerializedName("effets") List<AttackEffectData> effects

) {
    public boolean hasEffects() {
        return !this.effects.isEmpty();
    }
}
