package ulb.models.combat.factory;

import java.util.Map;

import ulb.models.bugemon.ElementType;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.run.RunTeam;
import ulb.models.team.Team;
import ulb.models.team.factory.TeamFactory;

/**
 * Abstract creator — declares the factory method {@link #create} that each concrete subclass overrides to produce a
 * specific {@link Combat} variant (manual, automatic, boss, …).
 */
public abstract class CombatFactory {

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;

    private final int floor;
    private final boolean bossMode;

    protected CombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, int floor,
            boolean bossMode) {
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;

        this.floor = floor;
        this.bossMode = bossMode;
    }

    /** Factory method: builds and returns a fully initialised {@link Combat}. */
    public Combat create(RunTeam playerRunTeam, Inventory playerInventory, TeamFactory opponentFactory,
            int playerCritBonus, Map<ElementType, Double> playerTypeMultipliers) {
        CombatTeam playerCombatTeam = CombatTeam.fromRunTeam(playerRunTeam);
        CombatTeam opponentTeam = this.buildOpponentTeam(playerRunTeam.size(), opponentFactory);

        CombatStrategy playerStrategy = this.buildPlayerStrategy();
        CombatStrategy opponentStrategy = this.buildOpponentStrategy();

        return new Combat(playerCombatTeam, opponentTeam, this.floor, this.bossMode, playerInventory,
                this.buildOpponentInventory(), playerStrategy, opponentStrategy, this.damageCalculator,
                this.effectProcessor);
    }

    protected abstract CombatStrategy buildPlayerStrategy();

    protected abstract CombatStrategy buildOpponentStrategy();

    protected Inventory buildOpponentInventory() {
        return new Inventory();
    }

    /**
     * Converts a {@link TeamFactory} into a ready-to-use {@link CombatTeam} for the opponent slot.
     */
    protected CombatTeam buildOpponentTeam(int size, TeamFactory factory) {
        Team opponentRaw = factory.create(size);
        return CombatTeam.fromRunTeam(RunTeam.fromTeam(opponentRaw));
    }
}
