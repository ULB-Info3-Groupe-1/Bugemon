package ulb.models.combat;

import java.util.Optional;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;

/**
 * Immutable snapshot of the outcome of a single combat turn.
 *
 * <p>
 * A {@code TurnResult} is produced by {@link Combat#turn()} at the end of every round and carries
 * everything the controller layer needs to update the view and react to game-state changes:
 * <ul>
 * <li>{@link #first()} — the {@link AttackResult} of the trainer who acted first (highest
 * initiative, or random on tie).</li>
 * <li>{@link #second()} — the {@link AttackResult} of the trainer who acted second, or
 * {@link Optional#empty()} if the combat ended after the first hit or if the second trainer did not
 * attack (e.g. switched).</li>
 * <li>{@link #allyIsKo()} — {@code true} when the ally trainer's active
 * {@link ulb.models.bugemon.Bugemon} was knocked out during this turn, prompting the controller to
 * open the forced-switch menu; {@link false} otherwise.</li>
 * </ul>
 *
 * <p>
 * Both {@code first} and {@code second} may represent a non-attack action (switch, item use, …):
 * check {@link AttackResult#wasAttack()} before reading attack-specific fields.
 * </p>
 *
 * @param first
 *            the result of the first action this turn; never {@code null}.
 * @param second
 *            the result of the second action this turn, or {@link Optional#empty()} if no second
 *            action occurred.
 * @param allyIsKo
 *            {@code true} if the ally's active Bugemon fainted during this turn, {@link false}
 *            otherwise.
 *
 * @see Combat#turn()
 * @see AttackResult
 */
public record TurnResult(AttackResult first, Optional<AttackResult> second, boolean allyIsKo) {
    /**
     * Immutable snapshot of a single hit within a turn.
     *
     * <p>
     * An {@code AttackResult} describes one side's action during a turn. When the acting trainer
     * chose to attack, {@link #attack()} is present and {@link #efficiency()} holds the computed
     * type-matchup; when the trainer switched, used an item, or did nothing, {@link #attack()} is
     * {@link Optional#empty()} and {@link #efficiency()} is {@code null}.
     * </p>
     *
     * <p>
     * Use {@link #wasAttack()} as a guard before accessing attack-specific data to avoid
     * {@link java.util.NoSuchElementException}.
     * </p>
     *
     * @param attacker
     *            the {@link Trainer} who performed the action; never {@code null}.
     * @param defender
     *            the {@link Trainer} who received the action; never {@code null}.
     * @param attack
     *            the {@link Attack} used, or {@link Optional#empty()} if the action was not an
     *            attack.
     * @param efficiency
     *            the {@link Efficiency} of the attack against the defender's active Bugemon type,
     *            or {@code null} if no attack was made.
     *
     * @see ulb.services.CombatService#compareBugemonType(ulb.models.bugemon.BugemonType,
     *      ulb.models.bugemon.BugemonType)
     */
    public record AttackResult(Trainer attacker, Trainer defender, Optional<Attack> attack,
            Efficiency efficiency) {
        /**
         * Returns {@code true} if this result represents an actual attack, i.e. {@link #attack()}
         * is present.
         *
         * <p>
         * Use this as a guard before calling {@link #attack()}{@code .get()} or reading
         * {@link #efficiency()} to avoid accessing {@code null} / empty-optional values when the
         * trainer switched or used an item instead of attacking.
         * </p>
         *
         * @return {@code true} if an attack was performed, {@code false} otherwise.
         */
        public boolean wasAttack() {
            return this.attack.isPresent();
        }

        public String getAttackName() {
            return this.attack.orElseThrow().name();
        }
    }
}
