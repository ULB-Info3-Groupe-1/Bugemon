/**
 * Contains the classes implementing the combat system of the Bugemon game, including turn-based
 * combat logic, damage calculation, and effect management.
 *
 * <h2>Package overview</h2>
 * <p>
 * A combat session opposes two {@link ulb.models.trainer.Trainer}s, each commanding a
 * {@link ulb.models.bugemon_team.BugemonTeam}. Every session is managed by
 * {@link ulb.models.combat.Combat}, which drives the turn loop and determines the winner once one
 * side is fully defeated.
 * </p>
 *
 * <h2>Key classes</h2>
 * <ul>
 * <li>{@link ulb.models.combat.Combat} — the central class holding the two trainer references and a
 * turn counter. Provides {@link ulb.models.combat.Combat#isFinished()} and
 * {@link ulb.models.combat.Combat#getWinner()} used by the controller layer to observe combat
 * state. Supports both automatic and manual combat depending on the
 * {@link ulb.models.trainer.Trainer} subtypes passed at construction time.</li>
 * <li>{@link ulb.services.CombatService} — stateless utility class providing the three core combat
 * calculations:
 * <ul>
 * <li>{@link ulb.services.CombatService#attackPriority(ulb.models.trainer.Trainer, ulb.models.trainer.Trainer)}
 * — resolves turn order from initiative stats.</li>
 * <li>{@link ulb.services.CombatService#calculateDamage(ulb.models.bugemon.Attack, ulb.models.bugemon.Bugemon, ulb.models.bugemon.Bugemon)}
 * — computes damage from power, attack/defense stats, type effectiveness, and a random critical-hit
 * factor.</li>
 * <li>{@link ulb.services.CombatService#compareBugemonType(ulb.models.bugemon.BugemonType, ulb.models.bugemon.BugemonType)}
 * — evaluates the elemental type matchup between an attack type and the defender's type, returning
 * a {@link ulb.common.Efficiency} value.</li>
 * </ul>
 * </li>
 * <li>{@link ulb.models.combat.EffectManager} — tracks and manages the lifecycle of
 * {@link ulb.models.bugemon.ActiveEffect}s applied to {@link ulb.models.bugemon.Bugemon}s during
 * combat. Must be updated once per turn via {@link ulb.models.combat.EffectManager#update()} to
 * decrement durations and reverse expired stat modifications.</li>
 * <li>{@link ulb.models.combat.TurnResult} — immutable snapshot of the outcome of a single combat
 * turn, returned by {@link ulb.models.combat.Combat#turn()} and consumed by the controller layer to
 * update the view.</li>
 * </ul>
 *
 * <h2>Type-effectiveness cycle</h2>
 * <p>
 * Elemental types follow a fixed cycle defined by the declaration order of
 * {@link ulb.models.bugemon.BugemonType}: {@code FLORA → AQUA → PYRO → LITHO → (back to FLORA)}.
 * Each type is strong against the type that precedes it in the cycle and weak against the type that
 * follows it. Neutral matchups apply to all other combinations, including same-type encounters.
 * </p>
 *
 * <h2>Design notes</h2>
 * <ul>
 * <li>Damage calculation in
 * {@link ulb.services.CombatService#calculateDamage(ulb.models.bugemon.Attack, ulb.models.bugemon.Bugemon, ulb.models.bugemon.Bugemon)}
 * includes a 10% random critical-hit chance (x1.5 multiplier), making individual results
 * non-deterministic. Tests that assert relative damage ordering should use the overload that
 * accepts an explicit {@code criticFactor} parameter to ensure reproducibility.</li>
 * <li>{@link ulb.models.combat.Combat} is designed to be driven either synchronously (manual
 * combat, one turn per user action) or via a JavaFX {@link javafx.animation.Timeline} (automatic
 * combat, one turn per timer tick) without any changes to the model itself.</li>
 * </ul>
 *
 * @see ulb.models.bugemon
 * @see ulb.models.trainer
 * @see ulb.controllers.combat
 * @see ulb.services.CombatService
 */
package ulb.models.combat;
