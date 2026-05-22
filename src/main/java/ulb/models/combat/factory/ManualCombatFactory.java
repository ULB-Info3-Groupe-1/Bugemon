package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.strategy.MiniMaxStrategy;
import ulb.models.combat.strategy.PlayerStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.player.PlayerInputHandler;
import ulb.models.team.factory.TeamFactory;

/**
 * {@link CombatFactory} variant for a human-vs-AI encounter: the player is driven by a {@link PlayerStrategy} backed by
 * live UI input, while the opponent uses {@link MiniMaxStrategy}.
 */
public class ManualCombatFactory extends CombatFactory {

    private final PlayerInputHandler handler;
    private final Inventory defaultInventory;

    /**
     * Creates a {@code ManualCombatFactory}.
     *
     * @param defaultInventory
     *            inventory given to the opponent at the start of combat
     * @param damageCalculator
     *            calculator used to resolve attack damage
     * @param effectProcessor
     *            processor that applies per-turn status effects
     * @param random
     *            source of randomness (forwarded to the base factory)
     * @param handler
     *            UI input handler that the {@link PlayerStrategy} will delegate to
     * @param opponentFactory
     *            factory that generates the opponent's team
     * @param floor
     *            current tower floor number
     * @param bossMode
     *            {@code true} if the encounter is a boss fight
     */
    public ManualCombatFactory(Inventory defaultInventory, DamageCalculator damageCalculator,
            EffectProcessor effectProcessor, Random random, PlayerInputHandler handler, TeamFactory opponentFactory,
            int floor, boolean bossMode) {
        super(opponentFactory, damageCalculator, effectProcessor, random, floor, bossMode);
        this.defaultInventory = defaultInventory;
        this.handler = handler;
    }

    /** {@inheritDoc} */
    @Override
    protected CombatStrategy buildPlayerStrategy() {
        return new PlayerStrategy(this.handler);
    }

    /** {@inheritDoc} */
    @Override
    protected CombatStrategy buildOpponentStrategy() {
        return new MiniMaxStrategy();
    }

    /**
     * Returns {@link #defaultInventory} so the opponent starts with a predefined item set.
     *
     * @return the default opponent inventory
     */
    @Override
    protected Inventory buildOpponentInventory() {
        return this.defaultInventory;
    }
}
