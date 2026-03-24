/**
 * Utility class providing helper methods for combat calculations.
 *
 * <p>Handles attack priority resolution, damage computation, and type
 * effectiveness based on a fixed cycle defined by the {@link
 * ulb.models.bugemon.Bugemon.BugemonType} enum order.</p>
 *
 * @author  Matteo Morbée
 * @author Lucas Verbeiren
 * @author Martin Gouverneur
 * @version 1.0
 * @date    05 mar. 2026
 */

package ulb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.Trainer;

/**
 * Utility class providing helper methods for combat calculations.
 *
 * <p>Handles attack priority resolution, damage computation, and type
 * effectiveness based on a fixed cycle defined by the {@link BugemonType} enum order.</p>
 *
 * <p>This class is not meant to be instantiated; all methods are static.</p>
 */
public class CombatService {

    private static final String BOSS_ID = "finalboss";


    private CombatService() {
        // Private constructor to prevent instantiation
    }

    /**
     * Determines which trainer's Bugemon attacks first based on initiative.
     * In case of a tie, the winner is chosen randomly.
     *
     * @param trainer1 the first trainer
     * @param trainer2 the second trainer
     * @return the trainer whose Bugemon attacks first
     */
    public static Trainer attackPriority(final Trainer trainer1, final Trainer trainer2) {
        final int initiative1 = trainer1.getCurrentBugemonInitiative();
        final int initiative2 = trainer2.getCurrentBugemonInitiative();

        if (initiative1 < initiative2) {
            return trainer2;
        } else if (initiative1 > initiative2) {
            return trainer1;
        } else {
            return Math.random() <= 0.5 ? trainer1 : trainer2;
        }
    }

    /**
     * Calculates the damage dealt by an attack using an explicit critical hit factor,
     * factoring in the offender's attack stat, the defender's defense stat, and
     * type effectiveness.
     *
     * <p>The damage formula is:
     * {@code power * ((100 + offenderAttack) / 100) * (100 / (defenderDefense + 100)) * typeFactor
     * * criticFactor}</p>
     *
     * @param attack          the attack being used
     * @param offenderBugemon the attacking Bugemon, used to access its attack stat
     * @param defenderBugemon the defending Bugemon, used to access its defense stat and type
     * @param criticFactor    the critical hit multiplier to apply (e.g. {@code 1.0} for normal,
     *                        {@code 1.5} for a critical hit)
     * @return the computed damage as a double
     */
    public static int calculateDamage(final Attack attack, final Bugemon offenderBugemon,
                                      final Bugemon defenderBugemon, final double criticFactor) {
        BugemonType defType = defenderBugemon.getType();

        final int basePower = attack.power();
        final double atkFactor = (100.0 + offenderBugemon.getAttack()) / 100.0;
        final double defFactor = 100.0 / (defenderBugemon.getDefense() + 100.0);
        final double typeMultiplier = getEfficiencyFactor(attack, defType);
        final double damage = basePower * atkFactor * defFactor * typeMultiplier * criticFactor;

        return (int)Math.ceil(damage);
    }

    /**
     * Calculates the damage dealt by an attack, factoring in the striker's
     * attack stat, the defender's defense stat, type effectiveness, and a
     * random critical hit chance (10% chance of 1.5x damage).
     *
     * <p>This is a convenience overload of
     * {@link #calculateDamage(Attack, Bugemon, Bugemon, double)} that automatically
     * determines whether a critical hit occurs.</p>
     *
     * @param attack          the attack being used
     * @param offenderBugemon the attacking Bugemon, used to access its attack stat
     * @param defenderBugemon the defending Bugemon, used to access its defense stat and type
     * @return the computed damage as a double
     */
    public static int calculateDamage(final Attack attack, final Bugemon offenderBugemon,
                                      final Bugemon defenderBugemon) {
        final double critMultiplier = Math.random() <= 0.1 ? 1.5 : 1.0;
        return calculateDamage(attack, offenderBugemon, defenderBugemon, critMultiplier);
    }

    /**
     * Returns the damage multiplier corresponding to the effectiveness of an
     * attack's type against the defender's type.
     *
     * <p>The multiplier is derived from {@link #compareBugemonType(BugemonType, BugemonType)} using
     * the attack's type and the defender's type.</p>
     *
     * @param attack       the attack being used
     * @param defenderType the type of the defending Bugemon
     * @return {@code 0.75} for {@link Efficiency#LOW}, {@code 1.50} for {@link Efficiency#HIGH},
     *         or {@code 1.00} for {@link Efficiency#NEUTRAL}
     */
    public static double getEfficiencyFactor(final Attack attack, final BugemonType defenderType) {
        final Efficiency matchup = compareBugemonType(attack.type(), defenderType);

        if (matchup.equals(Efficiency.LOW)) {
            return 0.75;
        } else if (matchup.equals(Efficiency.HIGH)) {
            return 1.50;
        } else {
            return 1.00;
        }
    }

    /**
     * Determines the type effectiveness of an offensive type against a defensive type.
     *
     * <p>The types follow a fixed cycle defined by the {@link BugemonType} enum declaration order.
     * In this cycle, each type is strong against the type immediately before it (wrapping
     * around) and weak against the type immediately after it (wrapping around).</p>
     *
     * <p>Specifically, given the cycle index difference
     * {@code (offensiveIdx - defensiveIdx) mod cycleSize}:</p>
     * <ul>
     *   <li>A difference of {@code 1} means the offensive type is one step ahead of the
     *       defensive type in the cycle → {@link Efficiency#LOW} (offensive is weak).</li>
     *   <li>A difference of {@code cycleSize - 1} means the offensive type is one step
     *       behind the defensive type in the cycle → {@link Efficiency#HIGH} (offensive is
     * strong).</li> <li>Any other difference → {@link Efficiency#NEUTRAL}.</li>
     * </ul>
     *
     * @param offensiveType the type of the attacking Bugemon or attack
     * @param defensiveType the type of the defending Bugemon
     * @return {@link Efficiency#HIGH} if the offensive type is strong against the defensive type,
     *         {@link Efficiency#LOW} if it is weak, or {@link Efficiency#NEUTRAL} otherwise
     */
    public static Efficiency compareBugemonType(final BugemonType offensiveType,
                                                final BugemonType defensiveType) {
        // Use the BugemonType enum declaration order as the type cycle
        final List<BugemonType> cycle = new ArrayList<>(List.of(BugemonType.values()));

        final int atkIdx = cycle.indexOf(offensiveType);
        final int defIdx = cycle.indexOf(defensiveType);

        // floorMod keeps the difference positive, wrapping around the cycle
        final int delta = Math.floorMod(atkIdx - defIdx, cycle.size());

        if (delta == 1) {
            return Efficiency.LOW; // offender is one step ahead of defender
        } else if (delta == cycle.size() - 1) {
            return Efficiency.HIGH; // offender is one step behind of defender
        } else {
            return Efficiency.NEUTRAL;
        }
    }

    /**
     * Generates a random {@link BugemonTeam} of the specified size by sampling
     * without replacement from the given pool of available {@link Bugemon}s.
     *
     * <p>
     * Each selected Bugemon is {@link Bugemon#clone() cloned} before being added
     * to the team so that the originals in {@code bugemonList} are not modified
     * during combat.
     * </p>
     *
     * @param bugemonList the pool of {@link Bugemon}s to sample from; must not
     *                    be {@code null} and must contain at least {@code teamSize}
     *                    distinct entries.
     * @param teamSize    the number of {@link Bugemon}s the resulting team should
     *                    contain; must be between {@code 1} and
     *                    {@code bugemonList.size()} inclusive.
     * @return a new {@link BugemonTeam} containing {@code teamSize} randomly
     *         chosen, cloned {@link Bugemon}s.
     * @throws RuntimeException if cloning a selected {@link Bugemon} fails.
     */
    public static BugemonTeam createRandomTeam(final List<Bugemon> bugemonList,
                                               final int teamSize) {
        List<Bugemon> pool = new ArrayList<>(bugemonList);
        Collections.shuffle(pool);
        BugemonTeam team = new BugemonTeam();
        for (int i = 0; i < teamSize; i++) {
            team.add(pool.get(i).clone());
        }
        return team;
    }

    /**
     * Creates a boss {@link BugemonTeam} containing the unique boss Bugemon defined by {@code BOSS_ID}.
     * 
     * @param bugemonList the list of available {@link Bugemon}s to search for the boss; must not be {@code null}
     * @return a new {@link BugemonTeam} containing the boss Bugemon.
     */
    public static BugemonTeam createBossTeam(List<Bugemon> bugemonList) {
        final Optional<Bugemon> bossBugemon =
                bugemonList.stream().filter(obj -> obj.getId().equals(BOSS_ID)).findFirst();
        BugemonTeam bossTeam = new BugemonTeam();

        bossTeam.add(bossBugemon.orElseThrow(
                ()
                        -> new RuntimeException("Boss Bugemon with ID '" + BOSS_ID
                                                + "' not found in the list.")));

        return bossTeam;
    }

}
