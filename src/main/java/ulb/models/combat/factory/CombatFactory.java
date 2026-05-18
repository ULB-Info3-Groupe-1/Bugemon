package ulb.models.combat.factory;

import java.util.Random;

import ulb.models.combat.Combat;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
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

    protected final DamageCalculator damageCalculator;
    protected final EffectProcessor effectProcessor;
    protected final Random random;

    protected CombatFactory(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random) {
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
    }

    /** Factory method: builds and returns a fully initialised {@link Combat}. */
    public abstract Combat create(RunTeam playerRunTeam, Inventory playerInventory, TeamFactory opponentFactory);

    /**
     * Converts a {@link TeamFactory} into a ready-to-use {@link CombatTeam} for the opponent slot.
     */
    protected CombatTeam buildOpponentTeam(int size, TeamFactory factory) {
        Team opponentRaw = factory.create(size);
        return CombatTeam.fromRunTeam(RunTeam.fromTeam(opponentRaw));
    }
}
