package ulb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.trainer.Trainer;

/**
 * Stateless utility for combat calculations: attack priority, damage formula.
 */
public class CombatService {

    public final BugemonService bugemonService;

    public CombatService(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    /**
     * Creates a combat.
     *
     * At the end of the combat, HPs are restored and XP is distributed.
     */
    public Combat createUniqueCombat(Trainer playerTrainer, Trainer opponentTrainer) {
        EndOfCombatAction endOfCombatCb = EndOfCombatAction.RESTORE_HP;
        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, endOfCombatCb);
    }

    /**
     * Determines which trainer's Bugemon attacks first based on initiative. In case of a tie, the winner is chosen
     * randomly.
     *
     * @param trainer1
     *            the first trainer
     * @param trainer2
     *            the second trainer
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
     * Calculates the damage dealt by an attack using an explicit critical hit factor, factoring in the offender's
     * attack stat, the defender's defense stat, and type effectiveness. Formula:
     * {@code power * ((100 + offenderAttack) / 100) * (100 / (defenderDefense + 100)) * typeFactor * criticFactor}
     *
     * @param attack
     *            the attack being used
     * @param offenderBugemon
     *            the attacking Bugemon, used to access its attack stat
     * @param defenderBugemon
     *            the defending Bugemon, used to access its defense stat and type
     * @param criticFactor
     *            the critical hit multiplier to apply (e.g. {@code 1.0} for normal, {@code 1.5} for a critical hit)
     * @return the computed damage as a double
     */
    public static int calculateDamage(final Attack attack, final Bugemon offenderBugemon, final Bugemon defenderBugemon,
            final double criticFactor) {

        final int basePower = attack.power();
        final double atkFactor = (100.0 + offenderBugemon.getAttack()) / 100.0;
        final double defFactor = 100.0 / (defenderBugemon.getDefense() + 100.0);
        final double typeMultiplier = getEfficiencyFactor(attack, defenderBugemon);
        final double damage = basePower * atkFactor * defFactor * typeMultiplier * criticFactor;

        return (int) Math.ceil(damage);
    }

    /**
     * Overload of {@link #calculateDamage(Attack, Bugemon, Bugemon, double)} with a random crit factor (10% chance of
     * 1.5×).
     */
    public static int calculateDamage(final Attack attack, final Bugemon offenderBugemon,
            final Bugemon defenderBugemon) {
        final double critMultiplier = Math.random() <= 0.1 ? 1.5 : 1.0;
        return calculateDamage(attack, offenderBugemon, defenderBugemon, critMultiplier);
    }

    /**
     * Returns the damage multiplier corresponding to the effectiveness of an attack's type against the defender's type.
     *
     * @param attack
     *            the attack being used
     * @param defender
     *            the defending Bugemon
     * @return {@code 0.75} for {@link Efficiency#LOW}, {@code 1.50} for {@link Efficiency#HIGH}, or {@code 1.00} for
     *         {@link Efficiency#NEUTRAL}
     */
    public static double getEfficiencyFactor(final Attack attack, final Bugemon defender) {
        final Efficiency matchup = attack.getEfficiencyAgainst(defender);

        if (matchup.equals(Efficiency.LOW)) {
            return 0.75;
        } else if (matchup.equals(Efficiency.HIGH)) {
            return 1.50;
        } else {
            return 1.00;
        }
    }

    /**
     * Determines the type effectiveness of an offensive type against a defensive type. Types follow a fixed cycle
     * defined by the {@link BugemonType} enum declaration order: each type is strong against the type immediately
     * before it (wrapping around) and weak against the type immediately after it.
     *
     * Given {@code delta = (offensiveIdx - defensiveIdx) mod cycleSize}: delta 1 → {@link Efficiency#LOW} (weak); delta
     * {@code cycleSize - 1} → {@link Efficiency#HIGH} (strong); any other → {@link Efficiency#NEUTRAL}.
     *
     * @param offensiveType
     *            the type of the attacking Bugemon or attack
     * @param defensiveType
     *            the type of the defending Bugemon
     * @return {@link Efficiency#HIGH} if the offensive type is strong against the defensive type,
     *         {@link Efficiency#LOW} if it is weak, or {@link Efficiency#NEUTRAL} otherwise
     */

    public static BugemonTeam createRandomTeam(final List<Bugemon> bugemonList, final int teamSize) {
        List<Bugemon> pool = new ArrayList<>(bugemonList);
        Collections.shuffle(pool);
        BugemonTeam team = new BugemonTeam();
        for (int i = 0; i < teamSize; i++) {
            team.add(new Bugemon(pool.get(i))); // Clone the Bugemon to avoid modifying the original
        }
        return team;
    }

    public static BugemonTeam createBossTeam(List<Bugemon> bugemonList) {
        final Optional<Bugemon> bossBugemon = bugemonList.stream()
                .filter(obj -> obj.getName().equals(Configuration.Game.BOSS_NAME)).findFirst();
        BugemonTeam bossTeam = new BugemonTeam();

        bossTeam.add(bossBugemon.orElseThrow(() -> new RuntimeException(
                "Boss Bugemon with name '" + Configuration.Game.BOSS_NAME + "' not found in the list.")));

        return bossTeam;
    }
}
