package ulb.models.bugemon.effect;

import ulb.models.bugemon.Bugemon;

public record EffectHeal(EffectTarget target, int amount) implements Effect {
    @Override
    public void applyTo(Bugemon bugemon) {
        bugemon.apply(this);
    }
}
