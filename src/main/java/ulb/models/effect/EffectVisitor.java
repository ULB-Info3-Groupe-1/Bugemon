package ulb.models.effect;

/**
 * Visitor interface for processing {@link Effect} instances without downcasting.
 *
 * <p>
 * Implement this interface to handle each concrete effect type. Call {@link Effect#accept(EffectVisitor)} to dispatch
 * to the correct overload.
 */
public interface EffectVisitor {

    /**
     * Handles a {@link HealEffect}.
     *
     * @param healEffect
     *            the heal effect to process
     */
    void visit(HealEffect healEffect);

    /**
     * Handles a {@link ResetMalusEffect}.
     *
     * @param resetMalusEffect
     *            the reset-malus effect to process
     */
    void visit(ResetMalusEffect resetMalusEffect);

    /**
     * Handles a {@link StatModifierEffect}.
     *
     * @param statModifierEffect
     *            the stat-modifier effect to process
     */
    void visit(StatModifierEffect statModifierEffect);

}
