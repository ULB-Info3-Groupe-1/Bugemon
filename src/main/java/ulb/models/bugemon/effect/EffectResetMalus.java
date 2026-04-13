package ulb.models.bugemon.effect;

import ulb.models.bugemon.Bugemon;

public record EffectResetMalus(EffectTarget target) implements Effect {
    @Override
    public void applyTo(Bugemon bugemon) {
        bugemon.apply(this);
    }
}
