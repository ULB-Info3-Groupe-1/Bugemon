/**
 * Run-scoped wrappers around persistent player data used during an active combat or tower run.
 *
 * <p>
 * {@link ulb.models.run.RunBugemon} wraps a {@link ulb.models.player.PlayerBugemon} and tracks mutable current HP,
 * while {@link ulb.models.run.RunTeam} groups the active Bugemons for the duration of the run. Neither class persists
 * state — changes are propagated back through the underlying {@code PlayerBugemon}.
 */
package ulb.models.run;
