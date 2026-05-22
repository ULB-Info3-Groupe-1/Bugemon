package ulb.models.effect;

import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;

/**
 * An {@link Effect} that applies a numerical modifier to a specific {@link ulb.common.StatType} of the target.
 *
 * <p>
 * The modifier may be permanent or limited to one turn, depending on the {@link ulb.common.EffectDuration}. Negative
 * values act as debuffs; positive values act as buffs.
 */
public class StatModifierEffect extends Effect {
    private final StatType stat;
    private final int modifier;
    private final EffectDuration duration;

    /**
     * @param target
     *            which combatant the modifier is applied to
     * @param stat
     *            the stat being modified (e.g. attack, defense)
     * @param modifier
     *            the amount to add; negative for a debuff, positive for a buff
     * @param duration
     *            whether the modifier lasts one turn or permanently
     */
    public StatModifierEffect(EffectTarget target, StatType stat, int modifier, EffectDuration duration) {
        super(target);
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
    }

    /** Returns the stat this effect modifies. */
    public StatType getStat() {
        return this.stat;
    }

    /** Returns the modifier value; negative means a debuff. */
    public int getModifier() {
        return this.modifier;
    }

    /** Returns whether this modifier lasts one turn or is permanent. */
    public EffectDuration getDuration() {
        return this.duration;
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
