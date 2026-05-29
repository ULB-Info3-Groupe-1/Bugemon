package bugemon.common.models.effect;

import java.io.Serializable;

import bugemon.common.EffectTarget;

/**
 * Abstract base class for all effects that can be applied to a {@link bugemon.common.EffectTarget}.
 *
 * <p>
 * Concrete subclasses implement {@link #accept(EffectVisitor)} to dispatch to the appropriate {@link EffectVisitor}
 * overload, enabling type-safe processing without downcasting.
 */
public abstract class Effect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final EffectTarget target;

    /**
     * @param target
     *            which combatant this effect applies to (e.g. self or opponent)
     */
    protected Effect(EffectTarget target) {
        this.target = target;
    }

    /** Returns which combatant this effect targets. */
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
