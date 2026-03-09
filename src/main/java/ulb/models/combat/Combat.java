/**
 * File name : Combat.java
 * Description : Class representing a combat between two trainers.
 *
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.combat;

import ulb.models.trainer.Trainer;

/**
 * Base class representing a turn-based combat between two {@link Trainer}s.
 *
 * <p>
 * A {@code Combat} holds references to an <em>ally</em> trainer (the player's
 * side) and an <em>adversary</em> trainer (the opponent's side), together with
 * a turn counter that is incremented via {@link #incrementTurn()} after each
 * round of play.
 * </p>
 *
 * <p>
 * The combat ends when at least one trainer has no more living
 * {@link ulb.models.bugemon.Bugemon}s, as detected by {@link #isFinished()}.
 * {@link #getWinner()} then returns the surviving trainer, or {@code null}
 * if the combat is still ongoing.
 * </p>
 *
 * <p>
 * Concrete subclasses define how each turn is resolved:
 * <ul>
 *   <li>{@link AutomaticCombat} — both sides choose randomly.</li>
 *   <li>{@link ManualCombat} — the ally side is driven by player input.</li>
 * </ul>
 * </p>
 *
 * @see AutomaticCombat
 * @see ManualCombat
 * @see Trainer
 */
public class Combat {
    // Attributes

    /** The allied (player-side) trainer participating in this combat. */
    protected Trainer allyTrainer;

    /** The adversary (opponent-side) trainer participating in this combat. */
    protected Trainer adversaryTrainer;

    /** The current turn number, starting at {@code 0}. */
    protected int turn;

    // Constructor

    /**
     * Constructs a new {@code Combat} between the two given trainers, with the
     * turn counter initialised to {@code 0}.
     *
     * @param allyTrainer      the allied {@link Trainer} (player side); must not
     *                         be {@code null}.
     * @param adversaryTrainer the adversary {@link Trainer} (opponent side); must
     *                         not be {@code null}.
     */
    public Combat(Trainer allyTrainer, Trainer adversaryTrainer) {
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
        this.turn = 0;
    }

    // Methods

    /**
     * Returns the winner of the combat if it is over, or {@code null} if the
     * combat is still ongoing.
     *
     * <p>
     * The winner is determined as follows:
     * <ul>
     *   <li>If the ally trainer is defeated, the adversary trainer wins.</li>
     *   <li>If the adversary trainer is defeated, the ally trainer wins.</li>
     *   <li>If neither trainer is defeated yet, {@code null} is returned.</li>
     * </ul>
     * </p>
     *
     * @return the winning {@link Trainer}, or {@code null} if the combat has not
     *         yet ended.
     * @see #isFinished()
     */
    public Trainer getWinner() {
        if (isFinished()) {
            if (allyTrainer.isDefeated()) {
                return adversaryTrainer;
            } else {
                return allyTrainer;
            }
        }
        return null;
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
        if (allyTrainer.isDefeated() || adversaryTrainer.isDefeated()) {
            return true;
        }
        return false;
    }

    /**
     * Increments the turn counter by one.
     *
     * <p>
     * This method should be called at the end of each full round (after both
     * trainers have had the opportunity to act) to keep the turn counter in sync
     * with the actual progression of the combat.
     * </p>
     *
     * @see #getTurn()
     */
    public void incrementTurn() {
        this.turn++;
    }

    // Getters and setters

    /**
     * Returns the current turn number.
     *
     * <p>
     * The counter starts at {@code 0} and is incremented by
     * {@link #incrementTurn()} after each round.
     * </p>
     *
     * @return the zero-based turn number of the current or most recently
     *         completed round.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Returns the allied (player-side) trainer.
     *
     * @return the ally {@link Trainer}; never {@code null}.
     */
    public Trainer getAllyTrainer() {
        return this.allyTrainer;
    }

    /**
     * Returns the adversary (opponent-side) trainer.
     *
     * @return the adversary {@link Trainer}; never {@code null}.
     */
    public Trainer getAdversaryTrainer() {
        return this.adversaryTrainer;
    }
}
