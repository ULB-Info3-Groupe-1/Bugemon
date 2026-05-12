package ulb.models.bugemon;

public record AttackEffectData(AttackEffectType effectType, EffectTarget target, StatType stat, int modifier,
        EffectDuration duration) {
    @Override
    public String toString() {
        return String.format("Effect[%s %s %s %+d %s]", this.effectType, this.target, this.stat, this.modifier,
                this.duration);
    }
}
