package ulb.models.combat;

import java.util.Random;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.strategy.PlayerStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.player.PlayerInputHandler;
import ulb.models.run.RunTeam;
import ulb.models.team.TeamFactory;

/**
 * Produces a {@link Combat} where the player acts through a {@link PlayerInputHandler}.
 */
public class ManualCombatFactory extends CombatFactory {

    private final PlayerInputHandler handler;
    private final int floor;
    private final boolean bossMode;

    public ManualCombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            PlayerInputHandler handler, int floor, boolean bossMode) {
        super(damageCalculator, effectProcessor, random);
        this.handler = handler;
        this.floor = floor;
        this.bossMode = bossMode;
    }

    @Override
    public Combat create(RunTeam playerRunTeam, Inventory playerInventory, TeamFactory opponentFactory) {
        CombatTeam playerCombatTeam = CombatTeam.fromRunTeam(playerRunTeam);
        CombatTeam opponentTeam = this.buildOpponentTeam(playerRunTeam.size(), opponentFactory);
        CombatStrategy playerStrategy = new PlayerStrategy(this.handler);
        CombatStrategy opponentStrategy = new AutoStrategy(this.random);
        return new Combat(playerCombatTeam, opponentTeam, this.floor, this.bossMode, playerInventory, new Inventory(),
                playerStrategy, opponentStrategy, this.damageCalculator, this.effectProcessor);
    }
}
