/**
 * Core game entities: {@link ulb.models.bugemon.Bugemon} species definitions, their {@link ulb.models.bugemon.Attack}s,
 * and the {@link ulb.models.bugemon.ElementType} matchup system.
 *
 * <p>
 * A {@link ulb.models.bugemon.Bugemon} is an immutable record describing a species template (base stats, move-set,
 * sprite). Runtime battle state is held by {@code ulb.models.run.RunBugemon}; ownership and progression are held by
 * {@code ulb.models.player.PlayerBugemon}.
 *
 * <p>
 * The {@code exceptions} sub-package contains unchecked exceptions thrown when Bugemon construction invariants are
 * violated.
 */
package ulb.models.bugemon;
