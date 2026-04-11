package ulb.models.bugemon.effect;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.components.modifier.Modifier;

public record EffectStatModifier(EffectTarget target, EffectStat stat, int modifier,
        EffectDuration duration) implements Effect {
    @Override
    public void applyTo(Bugemon bugemon) {
        Modifier m = (this.duration == EffectDuration.ONE_TURN) ? new Modifier(this.modifier, 1)
                : new Modifier(this.modifier);
        bugemon.apply(this);
    }
}
