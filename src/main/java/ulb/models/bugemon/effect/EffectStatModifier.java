package ulb.models.bugemon.effect;

import ulb.models.bugemon.Bugemon;

public record EffectStatModifier(EffectTarget target, EffectStat stat, int modifier,
        EffectDuration duration) implements Effect {
    @Override
    public void applyTo(Bugemon bugemon) {
        bugemon.apply(this);
    }
}
