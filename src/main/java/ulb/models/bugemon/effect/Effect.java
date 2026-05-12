package ulb.models.bugemon.effect;

public abstract class Effect {
    private final EffectTarget target;

    public Effect(EffectTarget target) {
        this.target = target;
    }

    public EffectTarget getTarget() {
        return this.target;
    }
}
