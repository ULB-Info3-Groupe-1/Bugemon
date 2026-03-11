/**
 * Root package for all model classes of the Bugemon game.
 *
 * <h2>Package overview</h2>
 * <p>
 * This package acts as the root of the model layer in the application's MVC
 * architecture. It does not contain any classes directly; all concrete model
 * types are organised in the following sub-packages:
 * </p>
 *
 * <h2>Sub-packages</h2>
 * <ul>
 *   <li>{@link ulb.models.bugemon} — core game entities: the
 *       {@link ulb.models.bugemon.Bugemon} class and its associated data types
 *       ({@link ulb.models.bugemon.Attack}, {@link ulb.models.bugemon.effect.Effect},
 *       {@link ulb.models.bugemon.ActiveEffect}) together with the enumerations
 *       that describe elemental types
 *       ({@link ulb.models.bugemon.Bugemon.BType}), effect kinds
 *       ({@link ulb.models.bugemon.effect.EffectType}), targets
 *       ({@link ulb.models.bugemon.effect.EffectTarget}), and affected statistics
 *       ({@link ulb.models.bugemon.effect.EffectStat}).</li>
 *   <li>{@link ulb.models.bugemon_team} — the
 *       {@link ulb.models.bugemon_team.BugemonTeam} collection class that groups
 *       up to six {@link ulb.models.bugemon.Bugemon}s owned by a single trainer,
 *       together with the
 *       {@link ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException}
 *       raised on duplicate-addition attempts.</li>
 *   <li>{@link ulb.models.trainer} — the trainer hierarchy:
 *       {@link ulb.models.trainer.Trainer} (base class),
 *       {@link ulb.models.trainer.AutoTrainer} (AI-driven, random action
 *       selection), and {@link ulb.models.trainer.ManualTrainer}
 *       (player-driven, explicit action selection).</li>
 *   <li>{@link ulb.models.combat} — the combat system: abstract base class
 *       {@link ulb.models.combat.Combat}, concrete variants
 *       {@link ulb.models.combat.AutomaticCombat} and
 *       {@link ulb.models.combat.ManualCombat}, the stateless damage/priority
 *       helper {@link ulb.models.combat.CombatHelper}, and the
 *       {@link ulb.models.combat.EffectManager} that tracks active effects and
 *       reverses them on expiry.</li>
 * </ul>
 *
 * <h2>Architectural role</h2>
 * <p>
 * The model layer is deliberately decoupled from JavaFX and from the view layer.
 * Data that must be consumed by views is exposed through the
 * {@link ulb.common.dto.BugemonDTO} interface, which is implemented by
 * {@link ulb.models.bugemon.Bugemon} and limits the view's access to a safe,
 * read-only subset of the Bugemon's state. All mutation of model state is
 * coordinated through the controller layer ({@link ulb.controllers}).
 * </p>
 *
 * <h2>Persistence and initialisation</h2>
 * <p>
 * Model objects are not persisted between application runs. At startup the
 * {@link ulb.controllers.MetaController} delegates to {@link ulb.utils.Parser}
 * to deserialise the game's JSON resource files into
 * {@link ulb.models.bugemon.Bugemon} and {@link ulb.models.bugemon.Attack}
 * instances, which are then held in memory for the duration of the session.
 * </p>
 *
 * @see ulb.models.bugemon
 * @see ulb.models.bugemon_team
 * @see ulb.models.trainer
 * @see ulb.models.combat
 * @see ulb.common.dto.BugemonDTO
 * @see ulb.utils.Parser
 */
package ulb.models;
