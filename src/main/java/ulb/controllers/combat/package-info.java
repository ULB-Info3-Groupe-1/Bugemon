/**
 * Contains the controller classes responsible for managing combat screens in the Bugemon
 * application.
 *
 * <h2>Package overview</h2>
 * <p>
 * This sub-package of {@link ulb.controllers} groups the controllers that drive the two combat
 * modes offered by the game. Each controller extends the abstract
 * {@link ulb.controllers.combat.CombatController} base class, which handles common initialisation
 * (pre-populating the view with placeholder {@link ulb.models.bugemon.Bugemon}s) and provides the
 * shared
 * {@link ulb.controllers.combat.CombatController#handleCombatResult(ulb.models.trainer.Trainer, ulb.models.trainer.Trainer)}
 * helper used to navigate to the appropriate outcome screen once a session ends.
 * </p>
 *
 * <h2>Class hierarchy</h2>
 * <ul>
 * <li>{@link ulb.controllers.combat.CombatController} — abstract generic base class parameterised
 * on a concrete {@link ulb.views.combat.CombatView} subtype. Initialises the view on construction
 * and exposes {@link ulb.controllers.combat.CombatController#handleCombatResult} to route the
 * player to the victory or defeat screen after a combat session concludes.</li>
 * <li>{@link ulb.controllers.combat.AutomaticCombatController} — drives a fully automated combat
 * session via
 * {@link ulb.controllers.combat.AutomaticCombatController#runAutoCombat(ulb.models.trainer.AutoTrainer)}.
 * Both the player's and the opponent's teams act randomly each turn; turns are resolved one by one
 * on a non-blocking JavaFX {@link javafx.animation.Timeline}.</li>
 * <li>{@link ulb.controllers.combat.ManualCombatController} — drives a player-controlled combat
 * session via
 * {@link ulb.controllers.combat.ManualCombatController#runManualCombat(ulb.models.trainer.ManualTrainer)}.
 * The player selects an attack, switches a Bugemon, or forfeits each turn through the UI, while the
 * opponent acts automatically.</li>
 * </ul>
 *
 * <h2>Combat lifecycle</h2>
 * <ol>
 * <li>The {@link ulb.controllers.MetaController} calls
 * {@link ulb.controllers.MetaController#launchAutoCombat()} or
 * {@link ulb.controllers.MetaController#launchManualCombat()}, which creates a player
 * {@link ulb.models.trainer.Trainer} and delegates to the appropriate controller.</li>
 * <li>The controller builds the opponent's {@link ulb.models.bugemon_team.BugemonTeam} by random
 * sampling from the full pool of available Bugemons (same size as the player's team).</li>
 * <li>The combat loop runs until one side is fully defeated, at which point
 * {@link ulb.controllers.combat.CombatController#handleCombatResult} is called to navigate to
 * {@link ulb.controllers.MetaController.Window#COMBAT_VICTORY} or
 * {@link ulb.controllers.MetaController.Window#COMBAT_DEFEAT}.</li>
 * </ol>
 *
 * <h2>Design notes</h2>
 * <ul>
 * <li>{@link ulb.controllers.combat.AutomaticCombatController} drives its loop via a JavaFX
 * {@link javafx.animation.Timeline} that fires one turn every 3 seconds, keeping the UI responsive
 * between turns.</li>
 * <li>{@link ulb.controllers.combat.ManualCombatController} processes one turn per player action;
 * the controller queues the chosen {@link ulb.models.trainer.TurnAction} on the
 * {@link ulb.models.trainer.ManualTrainer} and then calls {@link ulb.models.combat.Combat#turn()}
 * to resolve the round.</li>
 * <li>Each combat controller holds a typed reference to its {@link ulb.views.combat.CombatView}
 * subclass (via the {@code View} type parameter inherited from {@link ulb.controllers.Controller}),
 * eliminating the need for casts when accessing screen-specific view API.</li>
 * <li>Shared combat calculations (damage, attack priority, type effectiveness) are provided by the
 * stateless {@link ulb.services.CombatService} utility class rather than living inside the
 * controller or model.</li>
 * </ul>
 *
 * @see ulb.controllers
 * @see ulb.models.combat
 * @see ulb.views.combat
 * @see ulb.services.CombatService
 * @see ulb.controllers.MetaController
 */
package ulb.controllers.combat;
