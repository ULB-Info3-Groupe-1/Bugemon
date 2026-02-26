/**
 * File name : Trainer.java
 * Description : Class representing a trainer.
 * 
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import ulb.models.bugemon_team.BugemonTeam;

/**
 * This class represents a trainer, which has a team of bugemon.
 */
public class Trainer {
    // Attributes

    private BugemonTeam team;
    private int bugemonAlive;

    // Constructor
    /**
     * Constructor for the Trainer class, initializing the team of the trainer.
     * 
     * @param team (BugemonTeam) the team of the trainer, which is a list of
     *             bugemon.
     */
    public Trainer(BugemonTeam team) {
        this.team = team;
        this.bugemonAlive = team.getTeam().length;
    }

    // Methods

    /**
     * Returns true if the trainer is defeated, which means that all the bugemon in
     * the team are dead.
     * 
     * @return (boolean) true if the trainer is defeated, false otherwise.
     */
    public boolean isDefeated() {
        return bugemonAlive == 0;
    }

    /**
     * Decreases the number of bugemon alive in the team of the trainer.
     * 
     * @return (int) the number of bugemon alive in the team of the trainer.
     */
    public void decreaseBugemonAlive() {
        this.bugemonAlive--;
    }

    /**
     * Increases the number of bugemon alive in the team of the trainer.
     */
    public void increaseBugemonAlive() {
        this.bugemonAlive++;
    }

    // Getters and setters

    /**
     * Returns the number of bugemon alive in the team of the trainer.
     * 
     * @return (int) the number of bugemon alive in the team of the trainer.
     */
    public int countBugemonAlive() {
        return bugemonAlive;
    }

    /**
     * Returns the team of the trainer.
     * 
     * @return (BugemonTeam) the team of the trainer.
     */
    public BugemonTeam getTeam() {
        return team;
    }
}
