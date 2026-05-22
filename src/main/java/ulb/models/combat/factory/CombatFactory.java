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
 * Abstract factory that assembles a fully configured {@link Combat} instance.
 *
 * <p>
 * Follows the Template Method pattern: {@link #create} defines the fixed assembly algorithm while subclasses supply the
 * variable parts via {@link #buildPlayerStrategy()} and {@link #buildOpponentStrategy()}.
 */
public abstract class CombatFactory {

    protected final TeamFactory opponentFactory;

    protected final DamageCalculator damageCalculator;
    protected final EffectProcessor effectProcessor;
    protected final Random random;

    protected final int floor;
    protected final boolean bossMode;

    /**
     * Initialises the shared dependencies used by all concrete factories.
     *
     * @param opponentFactory
     *            factory that generates the opponent's team
     * @param damageCalculator
     *            calculator used to resolve attack damage
     * @param effectProcessor
     *            processor that applies per-turn status effects
     * @param random
     *            source of randomness for strategies that need it
     * @param floor
     *            current tower floor number, forwarded to the built {@link Combat}
     * @param bossMode
     *            {@code true} if the encounter is a boss fight
     */
    protected CombatFactory(TeamFactory opponentFactory, DamageCalculator damageCalculator,
            EffectProcessor effectProcessor, Random random, int floor, boolean bossMode) {
        this.opponentFactory = opponentFactory;
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
        this.floor = floor;
        this.bossMode = bossMode;
    }

    /**
     * Assembles and returns a new {@link Combat} for the given player state.
     *
     * @param playerRunTeam
     *            the player's current run team
     * @param playerInventory
     *            the player's item inventory
     * @param playerSkillContext
     *            the player's active skill context
     * @param availableBugemons
     *            pool of bugemons the opponent team may be drawn from
     * @return a fully configured, ready-to-start {@link Combat} instance
     */
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

    /**
     * Returns the {@link CombatStrategy} that will drive the player's decisions.
     *
     * @return player-side strategy instance
     */
    protected abstract CombatStrategy buildPlayerStrategy();

    /**
     * Returns the {@link CombatStrategy} that will drive the opponent's decisions.
     *
     * @return opponent-side strategy instance
     */
    protected abstract CombatStrategy buildOpponentStrategy();

    /**
     * Generates the opponent's {@link CombatTeam} via {@link #opponentFactory}.
     *
     * @param playerTeamSize
     *            number of bugemons in the player's team, used to size the opponent team equally
     * @param bugemons
     *            pool of available bugemons the factory may draw from
     * @return the opponent's combat team
     */
    protected CombatTeam buildOpponentTeam(int playerTeamSize, List<Bugemon> bugemons) {
        return CombatTeam.fromRunTeam(RunTeam.fromTeam(this.opponentFactory.create(playerTeamSize, bugemons)));
    }

    protected Inventory buildOpponentInventory() {
        return new Inventory();
    }
}
