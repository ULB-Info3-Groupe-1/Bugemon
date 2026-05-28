package bugemon.common.models.effect;

import bugemon.common.EffectTarget;

/**
 * An {@link Effect} that restores a fixed number of hit points to the target.
 */
public class HealEffect extends Effect {
    private final int amount;

    /**
     * @param target
     *            which combatant receives the healing
     * @param amount
     *            number of hit points to restore (should be positive)
     */
    public HealEffect(EffectTarget target, int amount) {
        super(target);
        this.amount = amount;
    }

    /** Returns the number of hit points restored by this effect. */
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void accept(EffectVisitor visitor) {
        visitor.visit(this);
    }
}
