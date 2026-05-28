package bugemon.common.models.combat.damage;

import bugemon.common.Configuration;
import bugemon.common.DamageResult;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.skills.SkillContext;

/**
 * Stateless service that computes damage dealt by one {@link bugemon.common.models.combat.CombatBugemon} to another.
 *
 * <p>
 * The damage formula is:
 *
 * <pre>{@code
 * damage = ceil(power × attackFactor(effectiveAttack) × reductionFactor(effectiveDefense) × typeMultiplier)
 * }</pre>
 *
 * where {@code attackFactor} and {@code reductionFactor} are pluggable {@link AttackFactorFormula} /
 * {@link ReductionFactorFormula} strategies loaded from {@link bugemon.common.Configuration.Game}.
 *
 * <p>
 * The {@link #previewEfficiency} helper maps a raw type multiplier to the human-readable {@link Efficiency} enum used
 * by the UI.
 */
public class DamageCalculator {
    AttackFactorFormula attackFactorFormula;
    ReductionFactorFormula reductionFactorFormula;

    public DamageCalculator() {
        this.attackFactorFormula = Configuration.Game.ATTACK_FACTOR_FORMULA;
        this.reductionFactorFormula = Configuration.Game.REDUCTION_FACTOR_FORMULA;
    }

    /**
     * Calculates the damage dealt by {@code attacker} using {@code attack} against {@code defender}, incorporating type
     * effectiveness and any skill-granted type multiplier from {@code attackerSkillContext}.
     *
     * @param attacker
     *            the attacking Bugemon
     * @param defender
     *            the defending Bugemon
     * @param attack
     *            the attack being used
     * @param attackerSkillContext
     *            the attacker's skill bonuses; use {@link bugemon.common.models.skills.SkillContext#NONE} when no skills apply
     * @return a {@link bugemon.common.DamageResult} containing the final damage and the {@link Efficiency} classification
     */
    public DamageResult calculateDamage(CombatBugemon attacker, CombatBugemon defender, Attack attack,
            SkillContext attackerSkillContext) {
        double power = attack.power();

        int attackStat = attacker.getEffectiveAttack();
        double attackFactor = this.attackFactorFormula.evaluate(attackStat);
        double reductionFactor = this.reductionFactorFormula.evaluate(defender.getEffectiveDefense());

        double typeMultiplier = attack.type().getMultiplierAgainst(defender.getType())
                * attackerSkillContext.getTypeMultiplier(attack.type());
        Efficiency efficiency = this.previewEfficiency(attack.type(), defender.getType());

        int totalDamage = (int) Math.ceil(power * attackFactor * reductionFactor * typeMultiplier);

        return new DamageResult(totalDamage, efficiency);
    }

    /**
     * Calculates raw damage without type effectiveness, using pre-computed stat values. Intended for UI preview and AI
     * heuristics where only base damage matters.
     *
     * @param attack
     *            the attack whose power and formula are used
     * @param attackerEffectiveAttack
     *            the attacker's effective attack stat
     * @param defenderEffectiveDefense
     *            the defender's effective defense stat
     * @return the raw damage value (type-neutral, rounded up)
     */
    public int calculateDamage(Attack attack, int attackerEffectiveAttack, int defenderEffectiveDefense) {
        double power = attack.power();
        double attackFactor = this.attackFactorFormula.evaluate(attackerEffectiveAttack);
        double reductionFactor = this.reductionFactorFormula.evaluate(defenderEffectiveDefense);
        double totalDamage = power * attackFactor * reductionFactor;
        return (int) Math.ceil(totalDamage);
    }

    /**
     * Computes a damage multiplier from the attacker's effective attack stat.
     */
    @FunctionalInterface
    public interface AttackFactorFormula {
        /**
         * Evaluates the attack factor for the given effective attack value.
         *
         * @param effectiveAttack
         *            the attacker's effective attack stat
         * @return a positive multiplier applied to base attack power
         */
        double evaluate(int effectiveAttack);
    }

    /**
     * Computes a damage reduction multiplier from the defender's effective defense stat.
     */
    @FunctionalInterface
    public interface ReductionFactorFormula {
        /**
         * Evaluates the reduction factor for the given effective defense value.
         *
         * @param effectiveDefense
         *            the defender's effective defense stat
         * @return a value in {@code (0, 1]} that scales down incoming damage
         */
        double evaluate(int effectiveDefense);
    }

    /**
     * Returns the {@link Efficiency} of an attack of {@code attackType} against a defender of {@code defenderType},
     * without computing actual damage.
     *
     * @param attackType
     *            the element type of the attack
     * @param defenderType
     *            the element type of the defending Bugemon
     * @return the corresponding {@link Efficiency} constant
     */
    public Efficiency previewEfficiency(ElementType attackType, ElementType defenderType) {
        return Efficiency.fromMultiplier(attackType.getMultiplierAgainst(defenderType));
    }
}
