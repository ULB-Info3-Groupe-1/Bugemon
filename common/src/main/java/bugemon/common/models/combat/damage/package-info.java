/**
 * Damage calculation sub-system for the combat model.
 *
 * <p>
 * The central class is {@link bugemon.common.models.combat.damage.DamageCalculator}, which computes the damage dealt by one
 * {@link bugemon.common.models.combat.CombatBugemon} to another using pluggable
 * {@link bugemon.common.models.combat.damage.DamageCalculator.AttackFactorFormula} and
 * {@link bugemon.common.models.combat.damage.DamageCalculator.ReductionFactorFormula} strategies.
 *
 * <p>
 * {@link bugemon.common.models.combat.damage.Efficiency} classifies the outcome of a type-effectiveness check into
 * {@code NOT_VERY_EFFICIENT}, {@code NORMAL}, or {@code SUPER_EFFICIENT} for display in the combat UI.
 */
package bugemon.common.models.combat.damage;
