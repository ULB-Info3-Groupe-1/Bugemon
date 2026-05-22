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

    public StatModifierEffect(EffectTarget target, StatType stat, int modifier, EffectDuration duration) {
        super(target);
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
    }

    public StatType getStat() {
        return this.stat;
    }

    public int getModifier() {
        return this.modifier;
    }

    public EffectDuration getDuration() {
        return this.duration;
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
