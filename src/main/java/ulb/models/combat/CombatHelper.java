/**
 * Utility class providing helper methods for combat calculations.
 *
 * <p>Handles attack priority resolution, damage computation, and type
 * effectiveness based on a fixed cycle defined by the {@link ulb.models.bugemon.Bugemon.BType}
 * enum order.</p>
 *
 * @author  Matteo Morbée
 * @author Lucas Verbeiren
 * @author Martin Gouverneur
 * @version 1.0
 * @date    05 mar. 2026
 */

package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon.Stats;
import ulb.models.trainer.Trainer;

/**
 * Utility class providing helper methods for combat calculations.
 * Handles attack priority, damage computation, and type effectiveness.
 */
public class CombatHelper {

    /**
     * Represents the effectiveness of an attack type against a defender's type.
     */
    public enum Efficiency {
        /** Normal effectiveness — no bonus or penalty. */
        NEUTRAL,
        /** Reduced effectiveness — deals less damage. */
        LOW,
        /** Super effectiveness — deals more damage. */
        HIGH,
    }

    /**
     * Determines which trainer's Bugemon attacks first based on initiative.
     * In case of a tie, the winner is chosen randomly.
     *
     * @param trainer1 the first trainer
     * @param trainer2 the second trainer
     * @return the trainer whose Bugemon attacks first
     */
    public static Trainer attackPriority(
        final Trainer trainer1,
        final Trainer trainer2
    ) {
        final int initiativeTrainer1 = trainer1.getCurrentBugemonInitiative();
        final int initiativeTrainer2 = trainer2.getCurrentBugemonInitiative();

        if (initiativeTrainer1 < initiativeTrainer2) {
            return trainer2;
        } else if (initiativeTrainer1 > initiativeTrainer2) {
            return trainer1;
        } else {
            return Math.random() <= 0.5 ? trainer1 : trainer2;
        }
    }

    /**
     * Calculates the damage dealt by an attack, factoring in the striker's
     * attack stat, the defender's defense stat, type effectiveness, and a
     * random critical hit chance (10% chance of 1.5x damage).
     *
     * @param attack        the attack being used
     * @param strikerStats  the stats of the attacking Bugemon
     * @param defenderBugemon the defending Bugemon, used to access its stats and type
     * @return the computed damage as a double
     */
    public static double calculateDamage(
        final Attack attack,
        final Stats strikerStats,
        final Bugemon defenderBugemon
    ) {
        BType defenderType = defenderBugemon.getType();
        Stats defenderStats = defenderBugemon.getStats();

        final int power = attack.getPower();
        final double attackFactor = (100.0 + strikerStats.getAttack()) / 100.0;
        final double reductionFactor =
            100.0 / (defenderStats.getDefense() + 100.0);
        final double typeFactor = getEfficiencyFactor(attack, defenderType);
        final double criticFactor = Math.random() <= 0.1 ? 1.5 : 1.0;

        final double result =
            power * attackFactor * reductionFactor * typeFactor * criticFactor;

        return result;
    }

    /**
     * Returns the damage multiplier corresponding to the effectiveness of an
     * attack's type against the defender's type.
     *
     * @param attack       the attack being used
     * @param defenderType the type of the defending Bugemon
     * @return {@code 0.75} for LOW, {@code 1.50} for HIGH, or {@code 1.00} for NEUTRAL
     */
    public static double getEfficiencyFactor(
        final Attack attack,
        final BType defenderType
    ) {
        final Efficiency efficiency = compareBType(
            attack.getType(),
            defenderType
        );

        if (efficiency.equals(Efficiency.LOW)) {
            return 0.75;
        } else if (efficiency.equals(Efficiency.HIGH)) {
            return 1.50;
        } else {
            return 1.00;
        }
    }

    /**
     * Determines the type effectiveness of a striker's type against a defender's type.
     * The types follow a fixed cycle defined by the {@link BType} enum order, where each
     * type is strong against the one before it and weak against the one after it.
     *
     * @param offensiveType  the type of the offensive Bugemon
     * @param defensiveType the type of the defensive Bugemon
     * @return {@link Efficiency#HIGH} if the striker's type is strong against the defender's,
     *         {@link Efficiency#LOW} if it is weak, or {@link Efficiency#NEUTRAL} otherwise
     */

    public static Efficiency compareBType(
        final BType offensiveType,
        final BType defensiveType
    ) {
        // Use the BType enum declaration order as the type cycle
        final List<BType> typeCycle = new ArrayList<BType>(
            List.of(BType.values())
        );

        final int offensiveIdx = typeCycle.indexOf(offensiveType);
        final int defensiveIdx = typeCycle.indexOf(defensiveType);

        // floorMod keeps the difference positive, wrapping around the cycle
        final int difference = Math.floorMod(
            offensiveIdx - defensiveIdx,
            typeCycle.size()
        );

        if (difference == 1) {
            return Efficiency.LOW; // offender is one step ahead of defender
        } else if (difference == typeCycle.size() - 1) {
            return Efficiency.HIGH; // offender is one step behind of defender
        } else {
            return Efficiency.NEUTRAL;
        }
    }
}
