package ulb.models.combat;

import java.util.List;
import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.CritBonusEffect;
import ulb.models.skills.SkillEffect.TypeMultiplierEffect;

public class CombatDamageCalculator {
    private List<Skill> skills;

    public CombatDamageCalculator(List<Skill> skills) {
        this.skills = skills;
    }

    /**
     * Skill-aware damage calculation. When {@code isPlayerAttacking} is true, every
     * unlocked
     * {@link TypeMultiplierEffect} matching the attack's type is applied to the
     * damage, and every unlocked
     * {@link CritBonusEffect} adds to the base 10% crit chance.
     */
    public int calculateDamage(final Attack attack, final Bugemon offenderBugemon,
            final Bugemon defenderBugemon, final boolean isPlayerAttacking) {
        final double critFactor = this.computeCritFactor(isPlayerAttacking);
        final double skillTypeMultiplier = isPlayerAttacking ? this.computeTypeMultiplier(attack) : 1.0;
        return (int) Math
                .ceil(this.calculateDamage(attack, offenderBugemon, defenderBugemon, critFactor) * skillTypeMultiplier);
    }

    /**
     * Calculates the damage dealt by an attack using an explicit critical hit
     * factor, factoring in the offender's
     * attack stat, the defender's defense stat, and type effectiveness. Formula:
     * {@code power * ((100 + offenderAttack) / 100) * (100 / (defenderDefense + 100)) * typeFactor * criticFactor}
     *
     * @param attack
     *                        the attack being used
     * @param offenderBugemon
     *                        the attacking Bugemon, used to access its attack stat
     * @param defenderBugemon
     *                        the defending Bugemon, used to access its defense stat
     *                        and type
     * @param criticFactor
     *                        the critical hit multiplier to apply (e.g. {@code 1.0}
     *                        for normal, {@code 1.5} for a critical hit)
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
     * Overload of {@link #calculateDamage(Attack, Bugemon, Bugemon, double)} with a
     * random crit factor (10% chance of
     * 1.5×).
     */
    public int calculateDamage(final Attack attack, final Bugemon offenderBugemon, final Bugemon defenderBugemon) {
        final double critMultiplier = Math.random() <= 0.1 ? 1.5 : 1.0;
        return this.calculateDamage(attack, offenderBugemon, defenderBugemon, critMultiplier);
    }

    private double computeCritFactor(boolean isPlayerAttacking) {
        double critChance = Configuration.Game.BASE_CRIT_CHANCE;
        if (isPlayerAttacking) {
            for (Skill skill : this.skills) {
                if (skill.getEffect() instanceof CritBonusEffect(var extraChance)) {
                    critChance += extraChance;
                }
            }
        }
        return Math.random() <= critChance ? Configuration.Game.CRIT_DAMAGE_FACTOR : 1.0;
    }

    private double computeTypeMultiplier(Attack attack) {
        double mult = 1.0;
        for (Skill skill : this.skills) {
            if (skill.getEffect() instanceof TypeMultiplierEffect(var type, var effectMult) && type == attack.type()) {
                mult *= effectMult;
            }
        }
        return mult;
    }

    /**
     * Returns the damage multiplier corresponding to the effectiveness of an
     * attack's type against the defender's type.
     *
     * @param attack
     *                 the attack being used
     * @param defender
     *                 the defending Bugemon
     * @return {@code 0.75} for {@link Efficiency#LOW}, {@code 1.50} for
     *         {@link Efficiency#HIGH}, or {@code 1.00} for
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
