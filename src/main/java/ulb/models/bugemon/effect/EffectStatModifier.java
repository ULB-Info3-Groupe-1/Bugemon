package ulb.models.bugemon.effect;

public record EffectStatModifier(EffectTarget target, EffectStat stat, int modifier,
        EffectDuration duration) implements Effect {
}
