package ulb.models.combat.damage;

import ulb.models.bugemon.ElementType;

/**
 * Represents the type-effectiveness category of an attack, derived from the element multiplier between attacker and
 * defender types.
 *
 * <p>
 * The three tiers map to the following multipliers:
 * <ul>
 * <li>{@link #NOT_VERY_EFFICIENT} — multiplier {@code < 1.0} (0.75×)</li>
 * <li>{@link #NORMAL} — multiplier {@code == 1.0} (1.0×)</li>
 * <li>{@link #SUPER_EFFICIENT} — multiplier {@code > 1.0} (1.5×)</li>
 * </ul>
 */
public enum Efficiency {
    /** The attack type is resisted by the defender's type (×0.75). */
    NOT_VERY_EFFICIENT(0.75),
    /** The attack deals normal damage against the defender's type (×1.0). */
    NORMAL(1.0),
    /** The attack type is super-effective against the defender's type (×1.5). */
    SUPER_EFFICIENT(1.5);

    private final double multiplier;

    Efficiency(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    /**
     * Maps a raw type multiplier to the corresponding {@code Efficiency} tier.
     *
     * @param mult
     *            the type multiplier to classify
     * @return {@link #SUPER_EFFICIENT} if {@code mult > 1.0}, {@link #NOT_VERY_EFFICIENT} if {@code mult < 1.0}, or
     *         {@link #NORMAL} otherwise
     */
    public static Efficiency fromMultiplier(double mult) {
        if (mult > 1.0) {
            return SUPER_EFFICIENT;
        }
        if (mult < 1.0) {
            return NOT_VERY_EFFICIENT;
        }
        return NORMAL;
    }

    /**
     * Convenience helper returning the efficiency of an attack of {@code type1} against a defender of {@code type2}
     * based on element multipliers.
     */
    public static Efficiency preview(ElementType type1, ElementType type2) {
        return fromMultiplier(type1.getMultiplierAgainst(type2));
    }

    /**
     * Returns {@code true} if this efficiency is {@link #NORMAL} or {@link #SUPER_EFFICIENT}.
     *
     * @return {@code true} for non-resisted matchups
     */
    public boolean isAtLeastNormal() {
        return this.compareTo(NORMAL) >= 0;
    }
}
