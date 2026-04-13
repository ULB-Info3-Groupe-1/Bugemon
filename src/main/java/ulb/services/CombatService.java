package ulb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
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

    /** Creates a combat that restores HP and distributes XP when it ends. */
    public Combat createUniqueCombat(Trainer playerTrainer, Trainer opponentTrainer) {
        EndOfCombatAction endOfCombatCb = EndOfCombatAction.RESTORE_HP;
        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, endOfCombatCb);
    }

    /**
     * Returns the trainer whose Bugemon attacks first based on initiative; ties are broken randomly.
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
     * Computes damage using an explicit crit factor.
     * Formula: {@code power * ((100 + offenderAttack) / 100) * (100 / (defenderDefense + 100)) * typeFactor * criticFactor}
     *
     * @param criticFactor multiplier to apply (e.g. {@code 1.0} normal, {@code 1.5} critical)
     * @return damage value, always >= 1
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
     * Returns the type-effectiveness damage multiplier for an attack against a defender.
     *
     * @return {@code 0.75} for {@link Efficiency#LOW}, {@code 1.50} for {@link Efficiency#HIGH}, {@code 1.00} otherwise
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
