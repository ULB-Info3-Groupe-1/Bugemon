package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;

/**
 * Produces a {@link Combat} where both sides are controlled by {@link AutoStrategy}.
 */
public class AutoCombatFactory extends CombatFactory {
    private final Random random;

    public AutoCombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            int floor, boolean bossMode) {
        super(damageCalculator, effectProcessor, floor, bossMode);
        this.random = random;
    }

    @Override
    protected CombatStrategy buildPlayerStrategy() {
        return new AutoStrategy(this.random);
    }

    @Override
    protected CombatStrategy buildOpponentStrategy() {
        return new AutoStrategy(this.random);
    }
}
