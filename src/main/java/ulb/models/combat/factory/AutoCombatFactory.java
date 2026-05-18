package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.Combat;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.run.RunTeam;
import ulb.models.team.factory.TeamFactory;

/**
 * Produces a {@link Combat} where both sides are controlled by {@link AutoStrategy}.
 */
public class AutoCombatFactory extends CombatFactory {

    private final int floor;
    private final boolean bossMode;

    public AutoCombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            int floor, boolean bossMode) {
        super(damageCalculator, effectProcessor, random);
        this.floor = floor;
        this.bossMode = bossMode;
    }

    @Override
    public Combat create(RunTeam playerRunTeam, Inventory playerInventory, TeamFactory opponentFactory) {
        CombatTeam playerCombatTeam = CombatTeam.fromRunTeam(playerRunTeam);
        CombatTeam opponentTeam = this.buildOpponentTeam(playerRunTeam.size(), opponentFactory);
        CombatStrategy playerStrategy = new AutoStrategy(this.random);
        CombatStrategy opponentStrategy = new AutoStrategy(this.random);
        return new Combat(playerCombatTeam, opponentTeam, this.floor, this.bossMode, playerInventory, new Inventory(),
                playerStrategy, opponentStrategy, this.damageCalculator, this.effectProcessor);
    }
}
