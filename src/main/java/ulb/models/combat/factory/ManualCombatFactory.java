package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.strategy.MiniMaxStrategy;
import ulb.models.combat.strategy.PlayerStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.player.PlayerInputHandler;

/**
 * Produces a {@link Combat} where the player acts through a {@link PlayerInputHandler}.
 */
public class ManualCombatFactory extends CombatFactory {

    private final PlayerInputHandler handler;
    private final Inventory defaultInventory;

    public ManualCombatFactory(Inventory defaultInventory, DamageCalculator damageCalculator,
            EffectProcessor effectProcessor, Random random, PlayerInputHandler handler, int floor, boolean bossMode) {
        super(damageCalculator, effectProcessor, floor, bossMode);
        this.defaultInventory = defaultInventory;
        this.handler = handler;
    }

    @Override
    protected CombatStrategy buildPlayerStrategy() {
        return new PlayerStrategy(this.handler);
    }

    @Override
    protected CombatStrategy buildOpponentStrategy() {
        return new MiniMaxStrategy();
    }

    @Override
    protected Inventory buildOpponentInventory() {
        return this.defaultInventory;
    }
}
