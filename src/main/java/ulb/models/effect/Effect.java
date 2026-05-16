package ulb.models.effect;

import ulb.common.EffectTarget;

public abstract class Effect {
    private final EffectTarget target;

    protected Effect(EffectTarget target) {
        this.target = target;
    }

    public EffectTarget getTarget() {
        return this.target;
    }

    public abstract void accept(EffectVisitor visitor);
}
