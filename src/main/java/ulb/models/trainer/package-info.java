/**
 * Contains the classes representing the trainers who own and field
 * {@link ulb.models.bugemon.Bugemon}s during combat in the Bugemon game.
 *
 * <h2>Package overview</h2>
 * <p>
 * A {@link ulb.models.trainer.Trainer} owns a
 * {@link ulb.models.bugemon_team.BugemonTeam} and tracks the currently active
 * {@link ulb.models.bugemon.Bugemon} on the field. It exposes the core combat
 * interface: querying HP and initiative, dealing damage to the active Bugemon,
 * and checking whether the trainer has been defeated (all Bugemons fainted).
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 *   <li>{@link ulb.models.trainer.Trainer} — base class holding the team and
 *       the current Bugemon reference. Used directly when no action-selection
 *       strategy is required (e.g., as a wrapper for result-screen logic).</li>
 *   <li>{@link ulb.models.trainer.AutoTrainer} — extends {@code Trainer} with
 *       automatic (random) action selection. It provides
 *       {@link ulb.models.trainer.AutoTrainer#getRandomAttack()} and
 *       {@link ulb.models.trainer.AutoTrainer#selectRandomBugemon()}, making it
 *       suitable for fully automated combats and as the AI opponent in manual
 *       combats.</li>
 *   <li>{@link ulb.models.trainer.ManualTrainer} — extends {@code Trainer} with
 *       explicit action selection driven by the player (or the controller layer).
 *       Before each turn the caller enqueues a {@link ulb.models.trainer.TurnAction}
 *       (attack, switch, or forfeit) via one of the convenience methods:
 *       {@link ulb.models.trainer.ManualTrainer#registerAttack(ulb.models.bugemon.Attack)},
 *       {@link ulb.models.trainer.ManualTrainer#registerSwitch(ulb.models.bugemon.Bugemon)},
 *       or {@link ulb.models.trainer.ManualTrainer#registerForfeit()}.</li>
 * </ul>
 *
 * <h2>Action model ({@link ulb.models.trainer.TurnAction})</h2>
 * <p>
 * The {@link ulb.models.trainer.TurnAction} sealed interface encodes the three
 * possible turn choices via its permitted record implementations:
 * </p>
 * <ul>
 *   <li>{@link ulb.models.trainer.TurnAction.AttackAction} — use a specific
 *       {@link ulb.models.bugemon.Attack}.</li>
 *   <li>{@link ulb.models.trainer.TurnAction.SwitchAction} — swap the active
 *       Bugemon for another alive member of the team.</li>
 *   <li>{@link ulb.models.trainer.TurnAction.ForfeitAction} — immediately
 *       concede the match.</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>Trainer instances are created by the
 *       {@link ulb.controllers.MetaController} and passed to a
 *       {@link ulb.models.combat.Combat} instance.</li>
 *   <li>Team stats are restored to their initial values after a combat session
 *       via {@link ulb.controllers.MetaController#resetTeam()}, which delegates
 *       to {@link ulb.models.bugemon_team.BugemonTeam#reset()}.</li>
 *   <li>Only one {@code Bugemon} is active at a time per trainer; the active
 *       member is changed via
 *       {@link ulb.models.trainer.Trainer#setCurrentBugemon(ulb.models.bugemon.Bugemon)}
 *       or the higher-level
 *       {@link ulb.models.trainer.AutoTrainer#selectRandomBugemon()} /
 *       {@link ulb.models.trainer.ManualTrainer#switchAfterKO(ulb.models.bugemon.Bugemon)}
 *       methods.</li>
 * </ul>
 *
 * @see ulb.models.bugemon_team.BugemonTeam
 * @see ulb.models.combat
 * @see ulb.controllers.MetaController
 */
package ulb.models.trainer;
