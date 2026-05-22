/**
 * Alpha-beta MiniMax engine and supporting types used by {@link ulb.models.combat.strategy.MiniMaxStrategy}.
 *
 * <p>
 * {@link ulb.models.combat.strategy.minimax.MiniMax} performs the tree search on immutable
 * {@link ulb.models.combat.snapshot.CombatSnapshot} objects. Actions are represented as
 * {@link ulb.models.combat.strategy.minimax.SimAction} records whose category is described by
 * {@link ulb.models.combat.strategy.minimax.SimActionKind}.
 */
package ulb.models.combat.strategy.minimax;
