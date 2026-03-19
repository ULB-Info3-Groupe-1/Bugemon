package ulb.models.bugemon.effect;

public sealed interface Effect permits EffectStatModifier, EffectHeal, EffectResetMalus {
    // Attributes
    EffectTarget target();
}
