package ulb.models.combat.effect;

import ulb.common.EffectDuration;
import ulb.models.effect.EffectStat;
import ulb.models.effect.Ticker;

public class StatusEffect {
    private final EffectStat stat;
    private final int modifier;
    private Ticker ticker; // NOTE: A permanent effect is represented by a null ticker

    public StatusEffect(EffectStat stat, int modifier, EffectDuration duration) {
        this.stat = stat;
        this.modifier = modifier;
        this.ticker = (duration == EffectDuration.PERMANENT) ? null : new Ticker(1);
    }

    public EffectStat getStat() {
        return this.stat;
    }

    public int getModifier() {
        return this.modifier;
    }

    public boolean isExpired() {
        return this.ticker.isExpired();
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
