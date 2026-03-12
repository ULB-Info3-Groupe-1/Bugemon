package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

import ulb.models.bugemon.Bugemon;

/**
 * Enumerates the {@link Bugemon} combat statistics that can be modified by an
 * {@link Effect} during battle.
 *
 * <p>
 * When an {@link Effect} of type {@link EffectType#STAT_MODIFIER} is applied,
 * its {@link Effect#getStat()} value determines which numeric field of the
 * targeted {@link Bugemon} is adjusted by the effect's modifier. The
 * {@link ulb.models.combat.EffectManager} reads this enum value and forwards
 * the modification to {@link Bugemon#editStat(EffectStat, int)}.
 * </p>
 *
 * <p>
 * The JSON serialization names (via {@code @SerializedName}) match the French
 * field values used in the game's data files.
 * </p>
 *
 * @see Effect
 * @see EffectType
 * @see Bugemon#editStat(EffectStat, int)
 * @see ulb.models.combat.EffectManager
 */
public enum EffectStat {
    /**
     * Represents the hit-points (HP) stat of a {@link Bugemon}.
     *
     * <p>
     * When targeted by a {@link EffectType#STAT_MODIFIER} effect, the Bugemon's
     * current HP is increased or decreased by the modifier value. This stat is
     * also targeted by {@link EffectType#SOIN} (healing) effects.
     * </p>
     *
     * <p>JSON value: {@code "pv"}</p>
     */
    @SerializedName("pv") HP,

    /**
     * Represents the attack stat of a {@link Bugemon}.
     *
     * <p>
     * When targeted by a {@link EffectType#STAT_MODIFIER} effect, the Bugemon's
     * attack value is increased or decreased by the modifier, affecting the
     * damage it deals with its attacks.
     * </p>
     *
     * <p>JSON value: {@code "attaque"}</p>
     */
    @SerializedName("attaque") ATTACK,

    /**
     * Represents the defense stat of a {@link Bugemon}.
     *
     * <p>
     * When targeted by a {@link EffectType#STAT_MODIFIER} effect, the Bugemon's
     * defense value is increased or decreased by the modifier, affecting the
     * amount of damage it absorbs from incoming attacks.
     * </p>
     *
     * <p>JSON value: {@code "defense"}</p>
     */
    @SerializedName("defense") DEFENSE,

    /**
     * Represents the initiative stat of a {@link Bugemon}.
     *
     * <p>
     * When targeted by a {@link EffectType#STAT_MODIFIER} effect, the Bugemon's
     * initiative value is increased or decreased by the modifier. Initiative
     * determines turn order: the Bugemon with the higher initiative attacks first
     * in a given turn, as resolved by
     * {@link ulb.models.combat.CombatHelper#attackPriority}.
     * </p>
     *
     * <p>JSON value: {@code "initiative"}</p>
     */
    @SerializedName("initiative") INITIATIVE,
}
