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

    private Trainer trainer1; // allied trainer 
    private Trainer trainer2; // adversary trainer
    private int turn;

    // Constructor

    /**
     * Constructor for the Combat class, initializing the two trainers and the turn
     * of the combat.
     */
    public Combat(Trainer trainer1, Trainer trainer2) {
        this.trainer1 = trainer1;
        this.trainer2 = trainer2;
        this.turn = 0;
    }

    // Methods

    /**
     * Returns true if the combat is over, false otherwise.
     */
    public boolean isCombatOver() {
        return trainer1.isDefeated() || trainer2.isDefeated();
    }

    public void nextTurn() {
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
     * Returns the trainer 1.
     */
    public Trainer getTrainer1() {
        return trainer1;
    }

    /**
     * Returns the trainer 2.
     */
    public Trainer getTrainer2() {
        return trainer2;
    }
}
