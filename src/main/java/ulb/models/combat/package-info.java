/**
 * Contains the classes implementing the combat system of the Bugemon game,
 * including turn-based combat logic, damage calculation, and effect management.
 *
 * <h2>Package overview</h2>
 * <p>
 * A combat session opposes two {@link ulb.models.trainer.Trainer}s, each
 * commanding a {@link ulb.models.bugemon_team.BugemonTeam}. Every session is
 * managed by a subclass of {@link ulb.models.combat.Combat}, which drives the
 * turn loop and determines the winner once one side is fully defeated.
 * </p>
 *
 * <h2>Key classes</h2>
 * <ul>
 *   <li>{@link ulb.models.combat.Combat} — abstract base class holding the two
 *       trainer references and a turn counter. Provides
 *       {@link ulb.models.combat.Combat#isFinished()} and
 *       {@link ulb.models.combat.Combat#getWinner()} used by all subclasses and
 *       the controller layer to observe combat state.</li>
 *   <li>{@link ulb.models.combat.AutomaticCombat} — a fully automated combat
 *       variant in which both {@link ulb.models.trainer.AutoTrainer}s choose
 *       their attacks at random each turn. Intended for simulated or
 *       demonstration combats.</li>
 *   <li>{@link ulb.models.combat.ManualCombat} — a player-driven combat variant
 *       where the ally {@link ulb.models.trainer.ManualTrainer} selects an
 *       action ({@link ulb.models.trainer.ManualTrainer.TAction#ATTACK},
 *       {@link ulb.models.trainer.ManualTrainer.TAction#SWITCH}, or
 *       {@link ulb.models.trainer.ManualTrainer.TAction#FORFEIT}) each turn
 *       while the opponent {@link ulb.models.trainer.AutoTrainer} acts
 *       automatically.</li>
 *   <li>{@link ulb.models.combat.CombatHelper} — stateless utility class
 *       providing the three core combat calculations:
 *     <ul>
 *       <li>{@link ulb.models.combat.CombatHelper#attackPriority} — resolves
 *           turn order from initiative stats.</li>
 *       <li>{@link ulb.models.combat.CombatHelper#calculateDamage} — computes
 *           damage from power, attack/defense stats, type effectiveness, and a
 *           random critical-hit factor.</li>
 *       <li>{@link ulb.models.combat.CombatHelper#compareBType} — evaluates the
 *           elemental type matchup between an attack type and the defender's
 *           type, returning a
 *           {@link ulb.models.combat.CombatHelper.Efficiency} value.</li>
 *     </ul>
 *   </li>
 *   <li>{@link ulb.models.combat.EffectManager} — tracks and manages the
 *       lifecycle of {@link ulb.models.bugemon.ActiveEffect}s applied to
 *       {@link ulb.models.bugemon.Bugemon}s during combat. Must be updated
 *       once per turn via
 *       {@link ulb.models.combat.EffectManager#update()} to decrement
 *       durations and reverse expired stat modifications.</li>
 * </ul>
 *
 * <h2>Type-effectiveness cycle</h2>
 * <p>
 * Elemental types follow a fixed cycle defined by the declaration order of
 * {@link ulb.models.bugemon.Bugemon.BType}:
 * {@code FLORA → AQUA → PYRO → LITHO → (back to FLORA)}.
 * Each type is strong against the type that precedes it in the cycle and weak
 * against the type that follows it. Neutral matchups apply to all other
 * combinations, including same-type encounters.
 * </p>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>Damage calculation in {@link ulb.models.combat.CombatHelper#calculateDamage}
 *       includes a 10 % random critical-hit chance (×1.5 multiplier), making
 *       individual results non-deterministic. Tests that assert relative damage
 *       ordering should use the overload that accepts an explicit
 *       {@code criticFactor} parameter to ensure reproducibility.</li>
 *   <li>Both {@link ulb.models.combat.AutomaticCombat} and
 *       {@link ulb.models.combat.ManualCombat} currently run their turn loops
 *       synchronously on the calling thread. Long-running sessions may block
 *       the JavaFX Application Thread; a future refactor should move the loop
 *       to a background thread.</li>
 * </ul>
 *
 * @see ulb.models.bugemon
 * @see ulb.models.trainer
 * @see ulb.controllers.combat
 */
package ulb.models.combat;
