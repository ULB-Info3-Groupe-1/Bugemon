package ulb.models.effect;

import ulb.common.EffectTarget;

/**
 * Abstract base class for all effects that can be applied to a {@link ulb.common.EffectTarget}.
 *
 * <p>
 * Concrete subclasses implement {@link #accept(EffectVisitor)} to dispatch to the appropriate {@link EffectVisitor}
 * overload, enabling type-safe processing without downcasting.
 */
public abstract class Effect {
    private final EffectTarget target;

    protected Effect(EffectTarget target) {
        this.target = target;
    }

    public EffectTarget getTarget() {
        return this.target;
    }

    /**
     * Dispatches this effect to the appropriate {@link EffectVisitor} overload.
     *
     * @param visitor
     *            the visitor to accept
     */
    public abstract void accept(EffectVisitor visitor);
}
