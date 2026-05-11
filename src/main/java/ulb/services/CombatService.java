package ulb.services;

import java.util.List;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.CritBonusEffect;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.models.skills.SkillEffect.TypeMultiplierEffect;
import ulb.models.trainer.Trainer;

/**
 * Stateless utility for combat calculations: attack priority, damage formula.
 */
public class CombatService {

    /**
     * Creates a combat.
     *
     * At the end of the combat, HPs are restored and XP is distributed.
     */
    public Combat createUniqueCombat(List<Skill> skills, Trainer playerTrainer, Trainer opponentTrainer) {
        this.applyStatBonusSkills(skills, playerTrainer);
        return new Combat(playerTrainer, opponentTrainer, skills);
    }

    /**
     * Skill-aware damage calculation. When {@code isPlayerAttacking} is true, every unlocked
     * {@link TypeMultiplierEffect} matching the attack's type is applied to the damage, and every unlocked
     * {@link CritBonusEffect} adds to the base 10% crit chance.
     */
    public int calculateDamage(List<Skill> skills, final Attack attack, final Bugemon offenderBugemon,
            final Bugemon defenderBugemon, final boolean isPlayerAttacking) {
        final double critFactor = this.computeCritFactor(skills, isPlayerAttacking);
        final double skillTypeMultiplier = isPlayerAttacking ? this.computeTypeMultiplier(skills, attack) : 1.0;
        return (int) Math
                .ceil(this.calculateDamage(attack, offenderBugemon, defenderBugemon, critFactor) * skillTypeMultiplier);
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
    public int calculateDamage(final Attack attack, final Bugemon offenderBugemon, final Bugemon defenderBugemon,
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
    public int calculateDamage(final Attack attack, final Bugemon offenderBugemon, final Bugemon defenderBugemon) {
        final double critMultiplier = Math.random() <= 0.1 ? 1.5 : 1.0;
        return this.calculateDamage(attack, offenderBugemon, defenderBugemon, critMultiplier);
    }

    private double computeCritFactor(List<Skill> skills, boolean isPlayerAttacking) {
        double critChance = Configuration.Game.BASE_CRIT_CHANCE;
        if (isPlayerAttacking) {
            for (Skill skill : skills) {
                if (skill.getEffect() instanceof CritBonusEffect(var extraChance)) {
                    critChance += extraChance;
                }
            }
        }
        return Math.random() <= critChance ? Configuration.Game.CRIT_DAMAGE_FACTOR : 1.0;
    }

    private double computeTypeMultiplier(List<Skill> skills, Attack attack) {
        double mult = 1.0;
        for (Skill skill : skills) {
            if (skill.getEffect() instanceof TypeMultiplierEffect(var type, var effectMult) && type == attack.type()) {
                mult *= effectMult;
            }
        }
        return mult;
    }

    /**
     * Applies every unlocked {@link StatBonusEffect} skill as a permanent {@link EffectStatModifier} on each Bugemon of
     * the player's team — these bonuses are re-applied at the start of every combat.
     */
    private void applyStatBonusSkills(List<Skill> skills, Trainer playerTrainer) {
        for (Skill skill : skills) {
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
}
