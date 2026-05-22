package ulb.models.effect;

/**
 * Visitor interface for processing {@link Effect} instances without downcasting.
 *
 * <p>
 * Implement this interface to handle each concrete effect type. Call {@link Effect#accept(EffectVisitor)} to dispatch
 * to the correct overload.
 */
public interface EffectVisitor {

    void visit(HealEffect healEffect);

    void visit(ResetMalusEffect resetMalusEffect);

    void visit(StatModifierEffect statModifierEffect);

}
