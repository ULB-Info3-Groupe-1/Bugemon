package ulb.models.combat.factory;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatBuilder;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.run.RunTeam;
import ulb.models.skills.SkillContext;
import ulb.models.team.factory.TeamFactory;

/**
 * Template method: {@link #create} defines the assembly algorithm; subclasses supply the variable parts via the
 * abstract factory methods {@link #buildPlayerStrategy()} and {@link #buildOpponentStrategy()}.
 */
public abstract class CombatFactory {

    protected final TeamFactory opponentFactory;

    protected final DamageCalculator damageCalculator;
    protected final EffectProcessor effectProcessor;
    protected final Random random;

    protected final int floor;
    protected final boolean bossMode;

    protected CombatFactory(TeamFactory opponentFactory, DamageCalculator damageCalculator,
            EffectProcessor effectProcessor, Random random, int floor, boolean bossMode) {
        this.opponentFactory = opponentFactory;
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
        this.floor = floor;
        this.bossMode = bossMode;
    }

    public Combat create(RunTeam playerRunTeam, Inventory playerInventory, SkillContext playerSkillContext,
            List<Bugemon> availableBugemons) {
        CombatTeam playerCombatTeam = CombatTeam.fromRunTeam(playerRunTeam);
        CombatTeam opponentTeam = this.buildOpponentTeam(playerRunTeam.size(), availableBugemons);

        return new CombatBuilder().playerTeam(playerCombatTeam).opponentTeam(opponentTeam).floor(this.floor)
                .bossMode(this.bossMode).playerInventory(playerInventory)
                .opponentInventory(this.buildOpponentInventory()).playerStrategy(this.buildPlayerStrategy())
                .opponentStrategy(this.buildOpponentStrategy()).damageCalculator(this.damageCalculator)
                .effectProcessor(this.effectProcessor).playerSkillContext(playerSkillContext).build();
    }

    protected abstract CombatStrategy buildPlayerStrategy();

    protected abstract CombatStrategy buildOpponentStrategy();

    protected CombatTeam buildOpponentTeam(int playerTeamSize, List<Bugemon> bugemons) {
        return CombatTeam.fromRunTeam(RunTeam.fromTeam(this.opponentFactory.create(playerTeamSize, bugemons)));
    }

    protected Inventory buildOpponentInventory() {
        return new Inventory();
    }
}
