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


public class Combat {

    // Attributes

    protected Trainer allyTrainer; // allied trainer
    protected Trainer adversaryTrainer; // adversary trainer
    protected int turn;

    // Constructor

    /**
     * Constructor for the Combat class, initializing the two trainers and the turn
     * of the combat.
     */
    public Combat(Trainer allyTrainer, Trainer adversaryTrainer) {
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
        this.turn = 0;
    }

    // Methods

    /**
     * Returns the winner of the combat if it is over, null otherwise.
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
     * Returns true if the combat is finished, false otherwise. A combat is finished if
     * one of the trainers is defeated (i.e., has no more Bugemon able to fight).
     * @return (boolean) true if the combat is finished, false otherwise
     */
    public boolean isFinished() {
        if (allyTrainer.isDefeated() || adversaryTrainer.isDefeated()) {
            return true;
        }
        return false;
    }

    /**
     * Increments the turn of the combat by 1.
     */
    public void incrementTurn() {
        this.turn++;
    }

    // Getters and setters

    /**
     * Returns the turn of the combat.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Returns the ally trainer.
     */
    public Trainer getAllyTrainer() {
        return this.allyTrainer;
    }

    /**
     * Returns the adversary trainer.
     */
    public Trainer getAdversaryTrainer() {
        return this.adversaryTrainer;
    }
}
