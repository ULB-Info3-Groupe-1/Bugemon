/**
 * File name : EffectType.java
 * Description : Enum representing the types of effects that can be applied to a Bugemon.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

/**
 * Enumerates the categories of {@link Effect}s that can be applied to a
 * {@link Bugemon} during battle.
 *
 * <p>
 * The type of an effect determines how it is processed by the
 * {@link ulb.models.combat.EffectManager}: a {@link #STAT_MODIFIER} adjusts a
 * numeric combat stat for a fixed number of turns, while a {@link #SOIN}
 * restores hit points immediately.
 * </p>
 *
 * @see Effect
 * @see EffectStat
 * @see ulb.models.combat.EffectManager
 */
public enum EffectType {
    /**
     * A stat-modifier effect temporarily changes one of a {@link Bugemon}'s
     * combat statistics (HP, attack, defense, or initiative) by a given
     * modifier value for a specified duration.
     *
     * <p>
     * When the effect expires, the modification is reversed so the stat returns
     * to its pre-effect value.
     * </p>
     *
     * @see EffectStat
     * @see ActiveEffect
     */
    STAT_MODIFIER,

    /**
     * A healing effect ({@code "soin"} in French) that restores a number of
     * hit points to the targeted {@link Bugemon}.
     *
     * <p>
     * Unlike {@link #STAT_MODIFIER}, a healing effect is applied immediately
     * and does not persist over multiple turns.
     * </p>
     */
    SOIN,
}
