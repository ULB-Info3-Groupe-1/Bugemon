package bugemon.common.models.effect;

import bugemon.common.EffectTarget;

/**
 * An {@link Effect} that clears all active negative stat modifiers (maluses) from the target.
 */
public class ResetMalusEffect extends Effect {

    /**
     * @param target
     *            which combatant has its maluses cleared
     */
    public ResetMalusEffect(EffectTarget target) {
        super(target);
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
