package ulb.models.effect;

import ulb.common.EffectTarget;

public class ResetMalusEffect extends Effect {
    public ResetMalusEffect(EffectTarget target) {
        super(target);
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
