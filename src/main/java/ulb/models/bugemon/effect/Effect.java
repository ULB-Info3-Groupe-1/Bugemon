package ulb.models.bugemon.effect;

import ulb.models.bugemon.ActiveEffect;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;


public sealed interface Effect permits EffectStatModifier, EffectHeal, EffectResetMalus {
    // Attributes
    EffectTarget target();
}
