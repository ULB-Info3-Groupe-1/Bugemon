/**
 * Immutable value types that capture combat state for read-only consumption by strategy algorithms.
 *
 * <p>
 * Snapshots decouple AI decision-making from mutable model objects, allowing strategies such as
 * {@link ulb.models.combat.strategy.minimax.MiniMax} to simulate future game states without side effects. The three
 * records form a hierarchy:
 * <ul>
 * <li>{@link ulb.models.combat.snapshot.CombatBugemonSnapshot} — one Bugemon's stats</li>
 * <li>{@link ulb.models.combat.snapshot.TeamSnapshot} — a full team roster plus active Bugemon</li>
 * <li>{@link ulb.models.combat.snapshot.CombatSnapshot} — both teams and their inventories</li>
 * </ul>
 */
package ulb.models.combat.snapshot;
