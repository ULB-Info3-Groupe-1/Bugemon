package ulb.models.bugemon.effect;

public class StatModifierEffect extends Effect {
    private final EffectStat stat;
    private final int modifier;
    private final EffectDuration duration;

    public StatModifierEffect(EffectTarget target, EffectStat stat, int modifier, EffectDuration duration) {
        super(target);
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
    }
}
