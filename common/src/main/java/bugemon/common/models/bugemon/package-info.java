/**
 * Core game entities: {@link bugemon.common.models.bugemon.Bugemon} species definitions, their {@link bugemon.common.models.bugemon.Attack}s,
 * and the {@link bugemon.common.models.bugemon.ElementType} matchup system.
 *
 * <p>
 * A {@link bugemon.common.models.bugemon.Bugemon} is an immutable record describing a species template (base stats, move-set,
 * sprite). Runtime battle state is held by {@code bugemon.common.models.run.RunBugemon}; ownership and progression are held by
 * {@code bugemon.common.models.player.PlayerBugemon}.
 *
 * <p>
 * The {@code exceptions} sub-package contains unchecked exceptions thrown when Bugemon construction invariants are
 * violated.
 */
package bugemon.common.models.bugemon;
