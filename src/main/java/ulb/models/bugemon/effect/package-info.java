/**
 * Attack effects applied during combat. {@link ulb.models.bugemon.effect.Effect} is a sealed interface with three
 * permitted subtypes: {@code EffectStatModifier} (temporary stat delta), {@code EffectHeal} (HP restoration), and
 * {@code EffectResetMalus} (clears active negative modifiers). Each effect targets one or more Bugemons via
 * {@link ulb.models.bugemon.effect.EffectTarget}.
 */
package ulb.models.bugemon.effect;
