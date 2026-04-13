/**
 * Turn-based combat system. {@link ulb.models.combat.Combat} drives the turn loop between two trainers; damage and type
 * effectiveness are computed by the stateless {@link ulb.services.CombatService}.
 *
 * Type cycle: {@code FLORA → AQUA → PYRO → LITHO → FLORA}. Each type is strong against its predecessor and weak against
 * its successor; all other matchups are neutral.
 *
 * Damage includes a 10% critical-hit chance (x1.5). Tests asserting exact damage values should use the
 * {@code criticFactor} overload of {@link ulb.services.CombatService#calculateDamage} for reproducibility.
 */
package ulb.models.combat;
