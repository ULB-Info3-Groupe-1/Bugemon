package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.team.factory.TeamFactory;

/**
 * {@link CombatFactory} variant that assigns an {@link AutoStrategy} to both the player and the opponent, producing a
 * fully automated combat useful for simulations and testing.
 */
public class AutoCombatFactory extends CombatFactory {

    /**
     * Creates an {@code AutoCombatFactory} with the given combat dependencies.
     *
     * @param damageCalculator
     *            calculator used to resolve attack damage
     * @param effectProcessor
     *            processor that applies per-turn status effects
     * @param random
     *            source of randomness shared by both auto-strategies
     * @param opponentFactory
     *            factory that generates the opponent's team
     * @param floor
     *            current tower floor number, used for opponent scaling
     * @param bossMode
     *            {@code true} if the encounter is a boss fight
     */
    public AutoCombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            TeamFactory opponentFactory, int floor, boolean bossMode) {
        super(opponentFactory, damageCalculator, effectProcessor, random, floor, bossMode);
    }

    /** {@inheritDoc} */
    @Override
    protected CombatStrategy buildPlayerStrategy() {
        return new AutoStrategy(this.random);
    }

    /** {@inheritDoc} */
    @Override
    protected CombatStrategy buildOpponentStrategy() {
        return new AutoStrategy(this.random);
    }
}
