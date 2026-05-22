/**
 * Factory classes that assemble {@link ulb.models.combat.Combat} instances.
 *
 * <p>
 * {@link ulb.models.combat.factory.CombatFactory} defines the Template Method that wires together teams, strategies,
 * inventories, and calculators. Concrete subclasses supply the strategy variants:
 * <ul>
 * <li>{@link ulb.models.combat.factory.ManualCombatFactory} — human player vs. MiniMax AI</li>
 * <li>{@link ulb.models.combat.factory.AutoCombatFactory} — fully automated simulation</li>
 * </ul>
 */
package ulb.models.combat.factory;
