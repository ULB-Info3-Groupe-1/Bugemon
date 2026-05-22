package ulb.models.effect;

import ulb.common.EffectTarget;

/**
 * An {@link Effect} that restores a fixed number of hit points to the target.
 */
public class HealEffect extends Effect {
    private final int amount;

    public HealEffect(EffectTarget target, int amount) {
        super(target);
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
