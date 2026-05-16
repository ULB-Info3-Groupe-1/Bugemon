package ulb.models.combat.effect;

import ulb.common.EffectDuration;
import ulb.common.StatType;
import ulb.models.effect.Ticker;

public class StatusEffect {
    private final StatType stat;
    private final int modifier;
    private Ticker ticker; // NOTE: A permanent effect is represented by a null ticker

    public StatusEffect(StatType stat, int modifier, EffectDuration duration) {
        this.stat = stat;
        this.modifier = modifier;
        this.ticker = (duration == EffectDuration.PERMANENT) ? null : new Ticker(1);
    }

    public StatType getStat() {
        return this.stat;
    }

    public int getModifier() {
        return this.modifier;
    }

    public boolean isExpired() {
        return this.ticker != null && this.ticker.isExpired();
    }

    public void tick() {
        if (this.ticker != null) {
            this.ticker.tick();
        }
    }

    public boolean isNegative() {
        return this.modifier < 0;
    }
}
