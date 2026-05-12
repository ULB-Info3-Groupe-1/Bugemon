package ulb.models.combat.effect;

import ulb.models.bugemon.EffectDuration;
import ulb.models.bugemon.components.modifier.Ticker;
import ulb.models.bugemon.effect.EffectStat;

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

    private boolean isPermanent() {
        return this.ticker == null;
    }

    public void tick() {
        if (this.ticker != null) {
            this.ticker.tick();
        }
    }
}
