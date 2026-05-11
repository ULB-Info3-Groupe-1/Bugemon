package ulb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.CritBonusEffect;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.models.skills.SkillEffect.TypeMultiplierEffect;
import ulb.models.trainer.Trainer;

/**
 * Stateless utility for combat calculations: attack priority, damage formula.
 */
public class CombatService {

    public final BugemonService bugemonService;
    private final SkillService skillService;

    public CombatService(BugemonService bugemonService, SkillService skillService) {
        this.bugemonService = bugemonService;
        this.skillService = skillService;
    }

    /**
     * Creates a combat.
     *
     * At the end of the combat, HPs are restored and XP is distributed.
     */
    public Combat createUniqueCombat(Trainer playerTrainer, Trainer opponentTrainer) {
        EndOfCombatAction endOfCombatCb = EndOfCombatAction.RESTORE_HP;
        this.applyStatBonusSkills(playerTrainer);
        return new Combat(playerTrainer, opponentTrainer, endOfCombatCb);
    }

    /**
     * Skill-aware damage calculation. When {@code isPlayerAttacking} is true, every unlocked
     * {@link TypeMultiplierEffect} matching the attack's type is applied to the damage, and every unlocked
     * {@link CritBonusEffect} adds to the base 10% crit chance.
     */
    public int calculateDamage(final Attack attack, final Bugemon offenderBugemon, final Bugemon defenderBugemon,
            final boolean isPlayerAttacking) {
        final double critFactor = this.computeCritFactor(isPlayerAttacking);
        final double skillTypeMultiplier = isPlayerAttacking ? this.computeTypeMultiplier(attack) : 1.0;
        return (int) Math
                .ceil(calculateDamage(attack, offenderBugemon, defenderBugemon, critFactor) * skillTypeMultiplier);
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

    private double computeCritFactor(boolean isPlayerAttacking) {
        double critChance = Configuration.Game.BASE_CRIT_CHANCE;
        if (isPlayerAttacking) {
            for (Skill skill : this.skillService.getSkills(CritBonusEffect.class)) {
                critChance += ((CritBonusEffect) skill.getEffect()).extraChance();
            }
        }
        return Math.random() <= critChance ? Configuration.Game.CRIT_DAMAGE_FACTOR : 1.0;
    }

    private double computeTypeMultiplier(Attack attack) {
        double mult = 1.0;
        for (Skill skill : this.skillService.getSkills(TypeMultiplierEffect.class)) {
            TypeMultiplierEffect effect = (TypeMultiplierEffect) skill.getEffect();
            if (effect.type() == attack.type()) {
                mult *= effect.mult();
            }
        }
        return mult;
    }

    /**
     * Applies every unlocked {@link StatBonusEffect} skill as a permanent {@link EffectStatModifier} on each Bugemon of
     * the player's team — these bonuses are re-applied at the start of every combat.
     */
    private void applyStatBonusSkills(Trainer playerTrainer) {
        for (Skill skill : this.skillService.getSkills(StatBonusEffect.class)) {
            StatBonusEffect bonus = (StatBonusEffect) skill.getEffect();
            EffectStatModifier modifier = new EffectStatModifier(EffectTarget.TEAM, bonus.stat(), bonus.bonus(),
                    EffectDuration.PERMANENT);
            playerTrainer.applyEffectToCurrentTeam(modifier);
        }
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
     * Creates a random team of Bugemons from a list of Bugemons and a team size.
     *
     * @param bugemonList
     *            all Bugemons that can be in the team
     * @param teamSize
     *            the size of the team
     * @return the created team
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

    /**
     * Creates a random team with one boss.
     *
     * @param bugemonList
     *            all Bugemons that can be in the team
     * @param teamSize
     *            the size of the team
     * @return the created team
     */
    public static BugemonTeam createRandomBossTeam(final List<Bugemon> bugemonList, final int teamSize)
            throws RuntimeException {
        final Bugemon bossBugemon = bugemonList.stream()
                .filter(obj -> obj.getName().equals(Configuration.Game.BOSS_NAME)).findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Boss Bugemon with name '" + Configuration.Game.BOSS_NAME + "' not found in the list."));
        List<Bugemon> listWithoutBoss = new ArrayList<>(bugemonList);
        listWithoutBoss.remove(bossBugemon);

        BugemonTeam bossTeam = createRandomTeam(listWithoutBoss, teamSize - 1);
        bossTeam.add(bossBugemon);
        bossTeam.shuffle();

        return bossTeam;
    }
}
