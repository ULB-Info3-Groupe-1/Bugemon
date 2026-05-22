/**
 * Turn-based combat system. {@link ulb.models.combat.Combat} drives the turn loop between two trainers; damage and type
 * effectiveness are computed by {@link ulb.models.combat.damage.DamageCalculator}.
 *
 * <p>
 * Type cycle: {@code FLORA → AQUA → PYRO → LITHO → FLORA}. Each type is strong against its predecessor and weak against
 * its successor; all other matchups are neutral.
 *
 * <p>
 * The main participants of this package are:
 * <ul>
 * <li>{@link ulb.models.combat.Combat} — the central engine coordinating action requests, turn resolution, and
 * combat-end detection.</li>
 * <li>{@link ulb.models.combat.CombatBugemon} — a combat-scoped Bugemon wrapper tracking transient HP and status
 * effects.</li>
 * <li>{@link ulb.models.combat.CombatTeam} — a roster of {@code CombatBugemon}s for one side, with active-fighter
 * management.</li>
 * <li>{@link ulb.models.combat.CombatBuilder} — fluent builder for assembling a {@code Combat} instance.</li>
 * <li>{@link ulb.models.combat.CombatResult} — the outcome enum ({@code VICTORY} or {@code DEFEAT}).</li>
 * </ul>
 */
package ulb.models.combat;
