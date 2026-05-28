/**
 * Factory classes that assemble {@link bugemon.common.models.combat.Combat} instances.
 *
 * <p>
 * {@link bugemon.common.models.combat.factory.CombatFactory} defines the Template Method that wires together teams, strategies,
 * inventories, and calculators. Concrete subclasses supply the strategy variants:
 * <ul>
 * <li>{@link bugemon.common.models.combat.factory.ManualCombatFactory} — human player vs. MiniMax AI</li>
 * <li>{@link bugemon.common.models.combat.factory.AutoCombatFactory} — fully automated simulation</li>
 * </ul>
 */
package bugemon.common.models.combat.factory;
