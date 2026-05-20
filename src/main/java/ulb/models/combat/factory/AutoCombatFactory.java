package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.team.factory.TeamFactory;

public class AutoCombatFactory extends CombatFactory {

    public AutoCombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            TeamFactory opponentFactory, int floor, boolean bossMode) {
        super(opponentFactory, damageCalculator, effectProcessor, random, floor, bossMode);
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
