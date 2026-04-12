package ulb.models.bugemon.effect;

import ulb.models.bugemon.Bugemon;

public sealed interface Effect permits EffectStatModifier, EffectHeal, EffectResetMalus {
    // Attributes
    EffectTarget target();

    /** Applies this effect to the given Bugemon. */
    void applyTo(Bugemon target);
}
