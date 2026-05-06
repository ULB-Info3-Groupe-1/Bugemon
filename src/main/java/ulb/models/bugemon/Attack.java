package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import ulb.models.bugemon.effect.Effect;

/**
 * Immutable record representing an attack a {@link Bugemon} can use in battle. Equality is based on {@link #id}.
 *
 * @param id
 *            The unique identifier of the attack
 * @param name
 *            The display name of the attack
 * @param type
 *            The {@link BugemonType} elemental type of the attack
 * @param description
 *            A brief description of the attack's behavior
 * @param power
 *            The base power or damage of the attack
 * @param effects
 *            The list of {@link Effect}s applied by this attack
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
     * Checks if this attack contains the given effect
     *
     * @param effect
     *            the effect
     * @return true if this attack contains the given effect and false otherwise
     */
    public boolean containsEffect(Effect effect) {
        return this.effects.contains(effect);
    }

    /** Compact representation for logging: {@code Explosion Ardente (PYRO, 70pw)}. */
    @Override
    public String toString() {
        return this.name + " (" + this.type + ", " + this.power + "pw)";
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
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Returns the efficiency of this attack against the given opponent type
     *
     * @param opponentType
     *            the type of the opponent
     * @return the efficiency
     */
    public Efficiency getEfficiencyAgainst(BugemonType opponentType) {
        return this.type.getEfficiencyAgainst(opponentType);
    }

    /**
     * Returns the efficiency of this attack against the given opponent
     *
     * @param opponent
     *            the opponent
     * @return the efficiency
     */
    public Efficiency getEfficiencyAgainst(Bugemon opponent) {
        return this.getEfficiencyAgainst(opponent.getType());
    }
}
