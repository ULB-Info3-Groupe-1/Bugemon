package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

/**
 * Enumerates the possible targets of an {@link Effect} when it is applied
 * during battle.
 *
 * <p>
 * The target determines which {@link Bugemon} (or group of Bugemons) is
 * affected when an {@link Attack} carrying the effect is used. The
 * {@link ulb.models.combat.EffectManager} uses this value to dispatch the
 * effect to the correct recipient(s).
 * </p>
 *
 * <p>
 * The JSON serialization names (via {@code @SerializedName}) match the French
 * field values used in the game's data files.
 * </p>
 *
 * @see Effect
 * @see ulb.models.combat.EffectManager
 */
public enum EffectTarget {
    /**
     * The effect targets the opposing {@link Bugemon} (i.e., the defender).
     *
     * <p>
     * Typically used for debuffs or damage-over-time effects aimed at weakening
     * the enemy during combat.
     * </p>
     *
     * <p>JSON value: {@code "adversaire"}</p>
     */
    @SerializedName("adversaire") ADVERSARY,

    /**
     * The effect targets the {@link Bugemon} that launched the attack (i.e.,
     * the attacker itself).
     *
     * <p>
     * Typically used for self-buffs or self-healing effects that benefit the
     * attacking Bugemon.
     * </p>
     *
     * <p>JSON value: {@code "lanceur"}</p>
     */
    @SerializedName("lanceur") THROWER,

    /**
     * The effect targets all {@link Bugemon}s in the attacker's team.
     *
     * <p>
     * Used for team-wide buffs or healing effects that benefit every member of
     * the attacking trainer's team simultaneously.
     * </p>
     *
     * <p>JSON value: {@code "equipe"}</p>
     */
    @SerializedName("equipe") TEAM,

    /**
     * The effect has no specific target.
     *
     * <p>
     * Used as a neutral or placeholder value when an {@link Effect} does not
     * need to be dispatched to any Bugemon (e.g., environmental effects or
     * effects that have already been fully resolved inline).
     * </p>
     */
    NONE,
}
