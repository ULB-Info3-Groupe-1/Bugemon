package ulb.models.effect;

import ulb.common.EffectTarget;

/**
 * An {@link Effect} that clears all active negative stat modifiers (maluses) from the target.
 */
public class ResetMalusEffect extends Effect {
    public ResetMalusEffect(EffectTarget target) {
        super(target);
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
