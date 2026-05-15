package ulb.models.effect;

import ulb.common.EffectTarget;

public class HealEffect extends Effect {
    private final int amount;

    public HealEffect(EffectTarget target, int amount) {
        super(target);
        this.amount = amount;
    }
}
