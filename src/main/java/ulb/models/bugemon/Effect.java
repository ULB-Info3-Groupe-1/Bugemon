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
 * Class representing an effect that can be applied to a bugemon during battle.
 */
public class Effect {
    // Attributes

    private EffectType type;
    private String target;

    private String stat;

    @SerializedName("modificateur")
    private int modifier;

    private String duration;

    // Constructor

    /**
     * Constructor for the Effect class, initializing all attributes.
     * 
     * @param type     (EffectType) the type of the effect.
     * @param target   (String) the target of the effect.
     * @param stat     (String) the stat affected by the effect.
     * @param modifier (int) the modifier value of the effect.
     * @param duration (String) the duration of the effect.
     */
    public Effect(EffectType type, String target, String stat, int modifier, String duration) {
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
     * @return (String) the target of the effect
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set the target of the effect to a new value.
     * 
     * @param target (String) the new target to set for the effect
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Get the stat affected by the effect.
     * 
     * @return (String) the stat affected by the effect
     */
    public String getStat() {
        return stat;
    }

    /**
     * Set the stat affected by the effect to a new value.
     * 
     * @param stat (String) the new stat to set for the effect
     */
    public void setStat(String stat) {
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
     * Get the duration of the effect in integer format.
     * 
     * @return (int) the value of the duration.
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
