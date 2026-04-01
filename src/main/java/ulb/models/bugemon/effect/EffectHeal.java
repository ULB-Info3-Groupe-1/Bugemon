package ulb.models.bugemon.effect;

public record EffectHeal(EffectTarget target, int amount) implements Effect {
}
