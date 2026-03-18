/**
 * File name : ManualTrainer.java
 * Description : Class representing a manual trainer.
 *
 * @author Liefferinckx Romain
 * @date 02 March. 2026
 * @version 2.0
 */

package ulb.models.trainer;

import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Represents a human-controlled trainer whose actions are driven by the
 * controller layer through a <em>queue-based</em> API.
 *
 * <p>
 * Before each call to {@link ulb.models.combat.Combat#turn()}, the controller
 * must enqueue exactly one action via one of the convenience methods:
 * <ul>
 *   <li>{@link #registerAttack(Attack)}  — attack with a specific move.</li>
 *   <li>{@link #registerSwitch(Bugemon)} — voluntarily swap the active Bugemon
 *       (counts as the turn action; the opponent still attacks).</li>
 *   <li>{@link #registerForfeit()}       — immediately concede the match.</li>
 * </ul>
 * The queued action is consumed exactly once by {@link #getAction()} and
 * cleared afterwards; a new action must be queued every turn.
 *
 * <p>
 * A separate code path handles <em>forced</em> switches that occur when the
 * active Bugemon faints mid-turn. In that case the controller calls
 * {@link #switchAfterKO(Bugemon)} directly, which replaces
 * {@code currentBugemon} immediately without going through a combat turn.
 * </p>
 *
 * @see AutoTrainer
 * @see ulb.models.combat.Combat
 * @see TurnAction
 */
public class ManualTrainer extends Trainer {
    private Optional<TurnAction> pendingAction = Optional.empty();
    private Optional<Bugemon> bugemonTargetForSwitch = Optional.empty();

    private boolean forcedSwitch = false;
    private boolean switchedThisTurn = false;

    /**
     * Constructs a {@code ManualTrainer} with the given team.
     *
     * <p>
     * The first Bugemon in the team is automatically set as the active one.
     * Both the pending action and the KO switch target start as empty.
     * </p>
     *
     * @param team the {@link BugemonTeam} this trainer owns; must not be
     *             {@code null} and must contain at least one Bugemon.
     */
    public ManualTrainer(BugemonTeam team) {
        super(team);
    }

    // ── strategy contract ────────────────────────────────────────────────────

    /**
     * Returns and consumes the action that was previously queued by the
     * controller via {@link #registerAttack(Attack)}, {@link #registerSwitch(Bugemon)},
     * {@link #registerForfeit()}, or the lower-level {@link #registerAction(TurnAction)}.
     *
     * <p>
     * The pending action is cleared after this call; the controller must queue
     * a new action before the next turn.
     * </p>
     *
     * @return the {@link TurnAction} chosen for this turn; never {@code null}.
     * @throws IllegalStateException if no action has been queued yet.
     */
    @Override
    public TurnAction getAction() {
        return pendingAction
                .map(a -> {
                    pendingAction = Optional.empty();
                    return a;
                })
                .orElseThrow(()
                                     -> new IllegalStateException(
                                             "No action has been selected for this turn."));
    }

    /**
     * Reacts to the active Bugemon fainting by switching to the target
     * previously registered via {@link #registerSwitchAfterKO(Bugemon)}.
     *
     * <p>
     * If no KO switch target has been registered (i.e. the controller has not
     * yet called {@link #registerSwitchAfterKO(Bugemon)}), this method does nothing;
     * the controller is responsible for calling {@link #switchAfterKO(Bugemon)}
     * when the player has made their choice.
     * </p>
     */
    @Override
    public void reactToKo() {
        if (bugemonTargetForSwitch.isPresent()) {
            currentBugemon = bugemonTargetForSwitch.get();
            bugemonTargetForSwitch = Optional.empty();
        }
    }

    // ── forced KO switch ─────────────────────────────────────────────────────

    /**
     * Immediately replaces the active Bugemon with {@code target} after a KO,
     * without going through a combat turn.
     *
     * <p>
     * This method is called by the controller when the player selects a
     * replacement Bugemon following a mid-turn KO. It bypasses the normal
     * turn flow: no opponent attack is triggered and no turn counter is
     * incremented.
     * </p>
     *
     * @param target the alive {@link Bugemon} to send into battle; must not be
     *               {@code null} and must be alive.
     * @throws IllegalArgumentException if {@code target} is not alive.
     */
    public void switchAfterKO(Bugemon target) {
        if (!target.isAlive()) {
            throw new IllegalArgumentException("The target bugemon is not alive.");
        }
        currentBugemon = target;
    }

    // ── controller queue API ─────────────────────────────────────────────────

    /**
     * Enqueues an arbitrary {@link TurnAction} to be consumed on the next
     * {@link #getAction()} call.
     *
     * <p>
     * Any previously queued action is silently overwritten. Prefer the typed
     * convenience methods ({@link #registerAttack(Attack)}, {@link #registerSwitch(Bugemon)},
     * {@link #registerForfeit()}) to benefit from built-in validation.
     * </p>
     *
     * @param action the {@link TurnAction} to queue; must not be {@code null}.
     */
    public void registerAction(TurnAction action) {
        pendingAction = Optional.of(action);
    }

    /**
     * Queues an {@link TurnAction.AttackAction} for the given attack.
     *
     * <p>
     * The attack must belong to the current Bugemon's move-set. If it does not,
     * an {@link IllegalArgumentException} is thrown and no action is queued.
     * </p>
     *
     * @param attack the {@link Attack} to use; must be in the active Bugemon's
     *               attack list.
     * @throws IllegalArgumentException if {@code attack} is not in the active
     *                                  Bugemon's move-set.
     */
    public void registerAttack(Attack attack) {
        if (!checkCurrentBugemonHasAttack(attack)) {
            throw new IllegalArgumentException(
                    "The selected attack is not in the current bugemon's attack list.");
        }
        registerAction(new TurnAction.AttackAction(attack));
    }

    /**
     * Queues a {@link TurnAction.SwitchAction} for the given Bugemon.
     *
     * <p>
     * This represents a <em>voluntary</em> switch: it consumes the player's
     * turn and the opponent still attacks afterwards. The target must be alive;
     * if it is not, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param target the {@link Bugemon} to switch in; must be alive.
     * @throws IllegalArgumentException if {@code target} is not alive.
     */
    public void registerSwitch(Bugemon target) {
        if (!target.isAlive()) {
            throw new IllegalArgumentException("The target bugemon is not alive.");
        }
        registerAction(new TurnAction.SwitchAction(target));
    }

    /**
     * Queues a {@link TurnAction.ForfeitAction}, causing the player to
     * immediately concede the match on the next {@link ulb.models.combat.Combat#turn()} call.
     */
    public void registerForfeit() {
        registerAction(new TurnAction.ForfeitAction());
    }

    /**
     * Pre-registers a KO switch target to be applied by {@link #reactToKo()}.
     *
     * <p>
     * This is an alternative to {@link #switchAfterKO(Bugemon)} for cases where
     * the switch target is known before {@link #reactToKo()} is invoked by the
     * combat engine.
     * </p>
     *
     * @param target the {@link Bugemon} to switch in on KO; must not be
     *               {@code null}.
     */
    public void registerSwitchAfterKO(Bugemon target) {
        bugemonTargetForSwitch = Optional.of(target);
    }

    // ── state queries ─────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if an action has been queued and not yet consumed.
     *
     * @return {@code true} if {@link #getAction()} can be called without
     *         throwing, {@code false} otherwise.
     */
    public boolean hasPendingAction() {
        return pendingAction.isPresent();
    }

    /** Returns {@code true} if a forced post-KO switch is pending. */
    public boolean isForcedToSwitch() {
        return forcedSwitch;
    }

    /** Sets whether a forced post-KO switch is pending. */
    public void setForcedSwitch(boolean value) {
        this.forcedSwitch = value;
    }

    /** Returns {@code true} if the player has already used a voluntary switch this turn. */
    public boolean hasSwitchedThisTurn() {
        return switchedThisTurn;
    }

    /** Sets whether a voluntary switch has been used this turn. */
    public void setHasSwitchedThisTurn(boolean value) {
        this.switchedThisTurn = value;
    }

    /** Returns {@code true} if the player may perform a voluntary switch right now. */
    public boolean canVoluntarilySwitch() {
        return !forcedSwitch && !switchedThisTurn;
    }
}
