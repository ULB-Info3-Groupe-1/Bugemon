/**
 * File name : Combat.java
 * Description : Class representing a combat between two trainers.
 *
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 2.0
 */

package ulb.models.combat;

import java.util.Optional;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;
import ulb.models.trainer.TurnAction;
import ulb.services.CombatService;

/**
 * Orchestrates a turn-based combat between any two {@link Trainer}s.
 *
 * <p>
 * A {@code Combat} holds references to an <em>ally</em> trainer (the player's
 * side) and an <em>adversary</em> trainer (the opponent's side), together with
 * a turn counter incremented after each resolved round.
 * </p>
 *
 * <p>
 * The combat ends when at least one trainer has no more living
 * {@link ulb.models.bugemon.Bugemon}s, as detected by {@link #isFinished()}.
 * {@link #getWinner()} then returns the surviving trainer wrapped in an
 * {@link Optional}, or an empty {@link Optional} if the combat is still
 * ongoing.
 * </p>
 *
 * <p>
 * Each call to {@link #turn()} asks both trainers for their chosen
 * {@link TurnAction} via {@link Trainer#getAction()}, applies passive
 * actions (switches, items, …) immediately, then resolves any attacks.
 * The turn result is returned as a {@link TurnResult} so that the controller
 * layer can drive the view without inspecting the model directly.
 * </p>
 *
 * <p>
 * Adding a new passive action (e.g. an item use) only requires:
 * <ol>
 *   <li>A new {@link TurnAction} record with {@code isAttack() == false}.</li>
 *   <li>A new branch in {@link #applyPassiveAction(Trainer, TurnAction)}.</li>
 * </ol>
 * No other method needs to change.
 *
 * @see TurnResult
 * @see Trainer
 * @see TurnAction
 */
public class Combat {
    /** The allied (player-side) trainer participating in this combat. */
    private final Trainer allyTrainer;

    /** The adversary (opponent-side) trainer participating in this combat. */
    private final Trainer adversaryTrainer;

    /** The current turn number, starting at {@code 0}. */
    private int turn = 0;

    /** The result of the most recently resolved turn, or {@code null} before the first turn. */
    private TurnResult lastTurnResult;

    // ── constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs a {@code Combat} between the two given trainers.
     *
     * <p>
     * Either trainer may be an {@link ulb.models.trainer.AutoTrainer} or a
     * {@link ulb.models.trainer.ManualTrainer}; the combat does not distinguish
     * between them — it only calls the {@link Trainer} interface.
     * </p>
     *
     * @param allyTrainer      the allied (player-side) trainer; must not be
     *                         {@code null} and must have a non-empty team.
     * @param adversaryTrainer the adversary (opponent-side) trainer; must not be
     *                         {@code null} and must have a non-empty team.
     */
    public Combat(Trainer allyTrainer, Trainer adversaryTrainer) {
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
    }

    // ── public API ────────────────────────────────────────────────────────────

    /**
     * Resolves one full round of combat and returns a {@link TurnResult}
     * describing every hit that occurred.
     *
     * <p>The sequence of events within a turn is:
     * <ol>
     *   <li>Both trainers' current {@link ulb.models.bugemon.Bugemon}s are
     *       marked as having participated.</li>
     *   <li>Active status effects are ticked via
     *       {@link EffectManager#update()}.</li>
     *   <li>Both trainers select their action via
     *       {@link Trainer#getAction()}.</li>
     *   <li>If either trainer forfeits, their team is instantly defeated and
     *       an empty result is returned.</li>
     *   <li>Passive actions (switches, …) are applied for both trainers.</li>
     *   <li>Attacks are resolved in initiative order via
     *       {@link #resolveAttacks(Optional, Optional)}.</li>
     *   <li>The turn counter is incremented.</li>
     * </ol>
     *
     * @return a {@link TurnResult} describing the first and optional second hit
     *         of the turn, and whether the ally was knocked out; never
     *         {@code null}.
     */
    public TurnResult turn() {
        allyTrainer.markCurrentBugemonParticipation();
        adversaryTrainer.markCurrentBugemonParticipation();

        TurnAction allyAction = allyTrainer.getAction();
        TurnAction adversaryAction = adversaryTrainer.getAction();

        if (checkForForfeit(allyAction, adversaryAction)) {
            this.lastTurnResult = emptyResult();
            return this.lastTurnResult;
        }

        applyPassiveAction(allyTrainer, allyAction);
        applyPassiveAction(adversaryTrainer, adversaryAction);

        Optional<Attack> allyAttack = extractAttack(allyAction);
        Optional<Attack> adversaryAttack = extractAttack(adversaryAction);

        turn++;
        this.lastTurnResult = resolveAttacks(allyAttack, adversaryAttack);
        return this.lastTurnResult;
    }

    /**
     * Returns the winner of the combat if it is over, or an empty
     * {@link Optional} if the combat is still ongoing.
     *
     * <p>
     * The winner is determined as follows:
     * <ul>
     *   <li>If the ally trainer is defeated, the adversary trainer wins.</li>
     *   <li>If the adversary trainer is defeated, the ally trainer wins.</li>
     *   <li>If neither trainer is defeated yet, an empty {@link Optional} is
     *       returned.</li>
     * </ul>
     *
     * @return an {@link Optional} containing the winning {@link Trainer}, or
     *         an empty {@link Optional} if the combat has not yet ended.
     * @see #isFinished()
     */
    public Optional<Trainer> getWinner() {
        return allyTrainer.isDefeated() ? Optional.of(adversaryTrainer)
        : adversaryTrainer.isDefeated() ? Optional.of(allyTrainer)
                                        : Optional.empty();
    }

    /**
     * Returns {@code true} if the combat is finished, {@code false} otherwise.
     *
     * <p>
     * A combat is considered finished when at least one of the two trainers has
     * been defeated, meaning all of their {@link ulb.models.bugemon.Bugemon}s
     * have fainted (HP &le; 0).
     * </p>
     *
     * @return {@code true} if either trainer is defeated, {@code false} if both
     *         still have at least one living Bugemon.
     * @see Trainer#isDefeated()
     */
    public boolean isFinished() {
        return allyTrainer.isDefeated() || adversaryTrainer.isDefeated();
    }

    /**
     * Returns the allied (player-side) trainer.
     *
     * @return the ally {@link Trainer}; never {@code null}.
     */
    public Trainer getAllyTrainer() {
        return allyTrainer;
    }

    /**
     * Returns the adversary (opponent-side) trainer.
     *
     * @return the adversary {@link Trainer}; never {@code null}.
     */
    public Trainer getAdversaryTrainer() {
        return adversaryTrainer;
    }

    /**
     * Returns the result of the most recently resolved turn.
     *
     * @return the last {@link TurnResult}, or {@code null} if no turn has been played yet.
     */
    public TurnResult getLastTurnResult() {
        return lastTurnResult;
    }

    /**
     * Returns the current turn number.
     *
     * <p>The counter starts at {@code 0} and is incremented at the end of every
     * non-forfeit turn.</p>
     *
     * @return the zero-based turn index.
     */
    public int getTurn() {
        return turn;
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Checks whether either trainer has chosen to forfeit, and if so instantly
     * defeats that trainer's entire team.
     *
     * @param allyAction      the action chosen by the ally trainer.
     * @param adversaryAction the action chosen by the adversary trainer.
     * @return {@code true} if at least one trainer forfeited and the combat
     *         should end immediately, {@code false} otherwise.
     */
    private boolean checkForForfeit(TurnAction allyAction, TurnAction adversaryAction) {
        if (allyAction instanceof TurnAction.ForfeitAction) {
            allyTrainer.killTeam();
            return true;
        }
        if (adversaryAction instanceof TurnAction.ForfeitAction) {
            adversaryTrainer.killTeam();
            return true;
        }
        return false;
    }

    /**
     * Applies the immediate effect of a non-attack action for the given trainer.
     *
     * <p>
     * Currently handled passive actions:
     * <ul>
     *   <li>{@link TurnAction.SwitchAction} — sets the trainer's active
     *       {@link ulb.models.bugemon.Bugemon} to the chosen target.</li>
     * </ul>
     * Future passive actions (e.g. item use) should add a new branch here
     * without modifying any other method.
     *
     * @param trainer the trainer performing the action; must not be {@code null}.
     * @param action  the action to apply; no-op if it is an attack or forfeit.
     */
    private void applyPassiveAction(Trainer trainer, TurnAction action) {
        trainer.applyPassiveAction(action);
    }

    /**
     * Extracts the {@link Attack} from an {@link TurnAction.AttackAction}, or
     * returns an empty {@link Optional} if the action is not an attack.
     *
     * @param action the action to inspect; must not be {@code null}.
     * @return an {@link Optional} containing the attack, or empty if the action
     *         is a switch, forfeit, or any other passive action.
     */
    private Optional<Attack> extractAttack(TurnAction action) {
        if (action instanceof TurnAction.AttackAction aa) {
            return Optional.of(aa.attack());
        }
        return Optional.empty();
    }

    /**
     * Resolves the attack phase of a turn given the (possibly absent) attacks
     * of each side.
     *
     * <p>Three cases are handled:
     * <ul>
     *   <li><strong>Both attack</strong> — initiative order is determined via
     *       {@link CombatHelper#attackPriority(Trainer, Trainer)}; the faster
     *       trainer hits first. If the combat is already finished after the
     *       first hit, the second hit is skipped.</li>
     *   <li><strong>Only ally attacks</strong> — the ally hits the adversary;
     *       no retaliation.</li>
     *   <li><strong>Only adversary attacks</strong> — the adversary hits the
     *       ally; no retaliation.</li>
     *   <li><strong>Neither attacks</strong> (both switched, both used items,
     *       …) — an empty result is returned.</li>
     * </ul>
     *
     * @param allyAttack      the ally's attack wrapped in an {@link Optional},
     *                        or empty if the ally did not attack this turn.
     * @param adversaryAttack the adversary's attack wrapped in an
     *                        {@link Optional}, or empty if the adversary did not
     *                        attack this turn.
     * @return a {@link TurnResult} describing what happened; never {@code null}.
     */
    private TurnResult resolveAttacks(Optional<Attack> allyAttack,
                                      Optional<Attack> adversaryAttack) {
        if (allyAttack.isPresent() && adversaryAttack.isPresent()) {
            Trainer first = CombatService.attackPriority(allyTrainer, adversaryTrainer);
            Trainer second = first == allyTrainer ? adversaryTrainer : allyTrainer;
            Attack firstAttack = first == allyTrainer ? allyAttack.get() : adversaryAttack.get();
            Attack secondAttack = second == allyTrainer ? allyAttack.get() : adversaryAttack.get();

            TurnResult.AttackResult firstResult = applyAttack(first, second, firstAttack);
            Optional<TurnResult.AttackResult> secondResult =
                    isFinished() ? Optional.empty()
                                 : Optional.of(applyAttack(second, first, secondAttack));

            boolean allyKO = isAllyKo(firstResult, secondResult);
            return new TurnResult(firstResult, secondResult, allyKO);
        }

        if (allyAttack.isPresent()) {
            TurnResult.AttackResult hit =
                    applyAttack(allyTrainer, adversaryTrainer, allyAttack.get());
            return new TurnResult(hit, Optional.empty(), false);
        }

        if (adversaryAttack.isPresent()) {
            TurnResult.AttackResult hit =
                    applyAttack(adversaryTrainer, allyTrainer, adversaryAttack.get());
            boolean allyKO = !allyTrainer.isCurrentBugemonAlive() && !allyTrainer.isDefeated();
            return new TurnResult(hit, Optional.empty(), allyKO);
        }

        return emptyResult();
    }

    /**
     * Returns {@code true} if the ally trainer's current
     * {@link ulb.models.bugemon.Bugemon} was knocked out during this turn's
     * attack resolution.
     *
     * <p>The ally can be knocked out by the first hit (when the adversary has
     * higher initiative) or by the second hit (normal case). Both possibilities
     * are checked.</p>
     *
     * @param first  the {@link TurnResult.AttackResult} of the first hit.
     * @param second the optional {@link TurnResult.AttackResult} of the second
     *               hit; may be empty if the combat ended after the first hit.
     * @return {@code true} if the ally's current Bugemon is no longer alive
     *         after this turn, {@code false} otherwise.
     */
    private boolean isAllyKo(TurnResult.AttackResult first,
                             Optional<TurnResult.AttackResult> second) {
        boolean koByFirst = first.defender() == allyTrainer && !allyTrainer.isCurrentBugemonAlive();
        boolean koBySecond = second.isPresent() && second.get().defender() == allyTrainer
                             && !allyTrainer.isCurrentBugemonAlive();
        return koByFirst || koBySecond;
    }

    /**
     * Applies a single attack from {@code attacker} to {@code defender},
     * computes type effectiveness, triggers a KO reaction if the defending
     * Bugemon faints, and records the result.
     *
     * <p>
     * If the defending Bugemon survives, any {@link ulb.models.bugemon.Effect}s
     * carried by the attack are applied via
     * {@link EffectManager#applyEffect(Trainer, Trainer, Attack)}.
     * Effects are <em>not</em> applied if the Bugemon faints, because it will
     * be switched out before they could take effect.
     * </p>
     *
     * @param attacker the trainer dealing the damage; must not be {@code null}.
     * @param defender the trainer receiving the damage; must not be {@code null}.
     * @param attack   the attack being used; must not be {@code null}.
     * @return an {@link TurnResult.AttackResult} capturing the participants, the
     *         attack used, and the computed {@link CombatHelper.Efficiency}.
     */
    private TurnResult.AttackResult applyAttack(Trainer attacker, Trainer defender, Attack attack) {
        int damage = CombatService.calculateDamage(attack, attacker.getCurrentBugemon(),
                                                   defender.getCurrentBugemon());
        defender.takeDamage(damage);

        Efficiency efficiency =
                CombatService.compareBugemonType(attack.type(), defender.getCurrentBugemonType());

        if (!defender.isCurrentBugemonAlive() && !defender.isDefeated()) {
            defender.reactToKo();
        }
        return new TurnResult.AttackResult(attacker, defender, Optional.of(attack), efficiency);
    }

    /**
     * Builds a {@link TurnResult} in which neither trainer performed an attack
     * (e.g. both forfeited, both switched, or a forfeit was detected before
     * attack resolution).
     *
     * @return a {@link TurnResult} with two empty {@link TurnResult.AttackResult}s
     *         and an empty {@code allyKnockedOut} flag.
     */
    private TurnResult emptyResult() {
        return new TurnResult(
                new TurnResult.AttackResult(allyTrainer, adversaryTrainer, Optional.empty(), null),
                Optional.of(new TurnResult.AttackResult(adversaryTrainer, allyTrainer,
                                                        Optional.empty(), null)),
                false);
    }
}
