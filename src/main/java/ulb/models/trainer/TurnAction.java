package ulb.models.trainer;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

/**
 * Represents the action a {@link Trainer} has decided to take for a given
 * combat turn.
 *
 * <p>
 * {@code TurnAction} is a sealed interface whose three permitted
 * implementations cover every possible player or AI decision:
 * <ul>
 *   <li>{@link AttackAction} — use an {@link Attack} against the opponent.</li>
 *   <li>{@link SwitchAction} — swap the active {@link Bugemon} for another
 *       alive member of the team.</li>
 *   <li>{@link ForfeitAction} — immediately concede the match.</li>
 * </ul>
 *
 * <p>
 * The {@link #isAttack()} method allows {@link ulb.models.combat.Combat} to
 * determine whether a given action generates a hit on the opponent without
 * pattern-matching on the concrete type. Passive actions ({@link SwitchAction},
 * {@link ForfeitAction}, and any future additions such as item use) return
 * {@code false}, letting the combat engine apply their side-effect and then
 * still allow the opponent to attack that turn.
 * </p>
 *
 * <p>
 * New action types should be added here as additional {@code record}
 * implementations of this interface. The only required contract is to implement
 * {@link #isAttack()}; the combat engine handles the rest via
 * {@link ulb.models.combat.Combat#turn()}.
 * </p>
 *
 * @see Trainer#selectAction()
 * @see ulb.models.combat.Combat
 */
public sealed interface TurnAction permits TurnAction.AttackAction, TurnAction.SwitchAction,
        TurnAction.ForfeitAction {
    /**
     * Returns {@code true} if this action generates a direct hit on the
     * opposing trainer's active {@link Bugemon}.
     *
     * <p>
     * {@link ulb.models.combat.Combat#turn()} uses this flag to decide whether
     * to call
     * {@link ulb.services.CombatService#calculateDamage(ulb.models.bugemon.Attack,
     * ulb.models.bugemon.Bugemon, ulb.models.bugemon.Bugemon)} for this side of the turn. Passive
     * actions (switch, forfeit, item use, …) must return
     * {@code false} so that the combat engine skips damage calculation for
     * them while still allowing the opponent to retaliate.
     * </p>
     *
     * @return {@code true} for {@link AttackAction}, {@code false} for all
     *         other action types.
     */
    boolean isAttack();

    // ── Concrete action types ─────────────────────────────────────────────────

    /**
     * Action in which the trainer uses an {@link Attack} against the opponent's
     * active {@link Bugemon}.
     *
     * <p>
     * The {@link ulb.models.combat.Combat} engine reads {@link #attack()} to
     * compute damage via
     * {@link ulb.services.CombatService#calculateDamage(ulb.models.bugemon.Attack,
     * ulb.models.bugemon.Bugemon, ulb.models.bugemon.Bugemon)} and to apply any
     * {@link ulb.models.bugemon.effect.Effect}s carried by the attack.
     * </p>
     *
     * @param attack the {@link Attack} to use; must not be {@code null} and
     *               must belong to the trainer's current {@link Bugemon}'s
     *               move-set.
     */
    record AttackAction(Attack attack) implements TurnAction {
        /**
         * {@inheritDoc}
         *
         * @return always {@code true} — an attack action generates a hit.
         */
        @Override
        public boolean isAttack() {
            return true;
        }
    }

    /**
     * Action in which the trainer swaps their active {@link Bugemon} for
     * another alive member of the team.
     *
     * <p>
     * {@link ulb.models.combat.Combat#turn()} applies the switch before
     * resolving damage for the turn. The switching trainer does not deal damage
     * this turn, but the opponent still attacks.
     * </p>
     *
     * @param target the {@link Bugemon} to switch in; must be alive and a
     *               member of the trainer's team.
     */
    record SwitchAction(Bugemon target) implements TurnAction {
        /**
         * {@inheritDoc}
         *
         * @return always {@code false} — a switch does not generate a hit.
         */
        @Override
        public boolean isAttack() {
            return false;
        }
    }

    /**
     * Action in which the trainer immediately concedes the match.
     *
     * <p>
     * When {@link ulb.models.combat.Combat#turn()} detects a
     * {@code ForfeitAction}, it calls {@link Trainer#killTeam()} on the
     * forfeiting trainer, marking all their {@link Bugemon}s as defeated so
     * that {@link ulb.models.combat.Combat#getWinner()} correctly returns the
     * opponent.
     * </p>
     */
    record ForfeitAction() implements TurnAction {
        /**
         * {@inheritDoc}
         *
         * @return always {@code false} — forfeiting does not generate a hit.
         */
        @Override
        public boolean isAttack() {
            return false;
        }
    }
}
