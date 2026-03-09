/**
 * File name : Effect.java
 * Description : Data class representing an effect of an attack.
 *
 * @author Liefferinckx Romain
 * @date 24 feb. 2026
 * @version 1.0
 */

package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a stat-altering or healing effect that can be applied to a
 * {@link Bugemon} when an {@link Attack} is used during battle.
 *
 * <p>
 * Each {@code Effect} defines:
 * <ul>
 *   <li>a {@link EffectType type} — whether it modifies a stat or heals;</li>
 *   <li>a {@link EffectTarget target} — which Bugemon(s) are affected;</li>
 *   <li>a {@link EffectStat stat} — which combat statistic is modified;</li>
 *   <li>a {@code modifier} — the signed integer delta applied to the stat;</li>
 *   <li>a {@code duration} — how long the effect lasts, encoded as a string
 *       in the format {@code "<n>_<unit>"} (e.g., {@code "2_turns"}).</li>
 * </ul>
 * </p>
 *
 * <p>
 * Effects are parsed from JSON and attached to {@link Attack} objects. At
 * runtime they are wrapped in {@link ActiveEffect} instances by the
 * {@link ulb.models.combat.EffectManager}, which tracks their remaining
 * duration and reverses the modification once they expire.
 * </p>
 *
 * @see EffectType
 * @see EffectTarget
 * @see EffectStat
 * @see ActiveEffect
 * @see ulb.models.combat.EffectManager
 */
public class Effect {
    // Attributes

    private EffectType type;

    @SerializedName("cible") private EffectTarget target;

    private EffectStat stat;

    @SerializedName("modificateur") private int modifier;

    private String duration;

    // Constructor

    /**
     * Constructs an {@code Effect} with all its attributes.
     *
     * @param type     the {@link EffectType} category of this effect (stat
     *                 modifier or healing).
     * @param target   the {@link EffectTarget} indicating which Bugemon(s) will
     *                 be affected.
     * @param stat     the {@link EffectStat} that will be modified by this effect.
     * @param modifier the signed integer value added to the targeted stat;
     *                 positive values buff, negative values debuff.
     * @param duration the duration string encoded as {@code "<n>_<unit>"}
     *                 (e.g., {@code "2_turns"}); parsed by
     *                 {@link #extractDuration()}.
     */
    public Effect(EffectType type, EffectTarget target, EffectStat stat, int modifier,
                  String duration) {
        this.type = type;
        this.target = target;
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
    }

    // Getters and Setters

    /**
     * Get the type of the effect.
     *
     * @return (EffectType) the type of the effect
     */
    public EffectType getTypeEffect() {
        return type;
    }

    /**
     * Set the type of the effect to a new value.
     *
     * @param type (EffectType) the new type to set for the effect
     */
    public void setTypeEffect(EffectType type) {
        this.type = type;
    }

    /**
     * Get the target of the effect.
     *
     * @return (EffectTarget) the target of the effect
     */
    public EffectTarget getTarget() {
        return target;
    }

    /**
     * Set the target of the effect to a new value.
     *
     * @param target (EffectTarget) the new target to set for the effect
     */
    public void setTarget(EffectTarget target) {
        this.target = target;
    }

    /**
     * Get the stat affected by the effect.
     *
     * @return (EffectStat) the stat affected by the effect
     */
    public EffectStat getStat() {
        return stat;
    }

    /**
     * Set the stat affected by the effect to a new value.
     *
     * @param stat (EffectStat) the new stat to set for the effect
     */
    public void setStat(EffectStat stat) {
        this.stat = stat;
    }

    /**
     * Get the modifier value of the effect.
     *
     * @return (int) the modifier value of the effect
     */
    public int getModifier() {
        return modifier;
    }

    /**
     * Set the modifier value of the effect to a new value.
     *
     * @param modifier (int) the new modifier value to set for the effect
     */
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    /**
     * Get the duration of the effect.
     *
     * @return (String) the duration of the effect
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Parses and returns the numeric part of the duration string.
     *
     * <p>
     * The duration is expected to be encoded as {@code "<n>_<unit>"}
     * (e.g., {@code "2_turns"}). This method splits the string on the first
     * {@code '_'} character and parses the leading segment as an integer.
     * </p>
     *
     * @return the number of turns (or other unit) this effect lasts.
     * @throws IllegalArgumentException if the duration string is {@code null}
     *         or does not contain an {@code '_'} separator.
     * @throws NumberFormatException if the part before {@code '_'} cannot be
     *         parsed as an integer.
     */
    public int extractDuration() {
        if (this.duration == null || !this.duration.contains("_")) {
            throw new IllegalArgumentException("Format invalide");
        }

        String numberPart = this.duration.split("_")[0];
        return Integer.parseInt(numberPart);
    }

    /**
     * Set the duration of the effect to a new value.
     *
     * @param duration (String) the new duration to set for the effect
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }
}
