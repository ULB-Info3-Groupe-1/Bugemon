package ulb.models.effect;

import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;

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

    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
