/**
 * File name : Trainer.java
 * Description : Class representing a trainer.
 * 
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import java.util.Map;
import java.util.HashMap;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * This class represents a trainer, which has a team of bugemon.
 */
public class Trainer {

    // Attributes

    private BugemonTeam team;
    private Bugemon currentBugemon = null;

    // Constructor

    /**
     * Constructor for the Trainer class, initializing the team of the trainer.
     * 
     * @param team (BugemonTeam) the team of the trainer, which is a list of
     *             bugemon.
     */
    public Trainer(BugemonTeam team) {
        this.team = team;
        this.currentBugemon = team.getTeam().get(0);
    }

    // Methods

    /**
     * Returns a map of the bugemon in the team of the trainer, with their status
     * (alive or not).
     * 
     * @return (Map<Bugemon, Boolean>) a map of the bugemon in the team of the
     *         trainer, with their status (alive or not).
     */
    public Map<Bugemon, Boolean> teamStatus() {
        Map<Bugemon, Boolean> bugemonStatus = new HashMap<>();
        for (Bugemon bugemon : team.getTeam()) {
            bugemonStatus.put(bugemon, bugemon.isAlive());
        }
        return bugemonStatus;
    }

    /**
     * Returns true if the trainer is defeated (all bugemon in the team are
     * defeated), false otherwise.
     * 
     * @return (boolean) true if the trainer is defeated, false otherwise.
     */
    public boolean isDefeated() {
        for (Bugemon bugemon : team.getTeam()) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the initiative of the current bugemon.
     * 
     * @return (int) the initiative of the bugemon.
     */
    public int getCurrentBugemonInitiative() {
        return currentBugemon.getStats().getInitiative();
    }

    /**
     * Return the current bugemon in the team of the trainer.
     * 
     * @return (Bugemon) the current bugemon in the team of the trainer.
     */
    public Bugemon getCurrentBugemon() {
        return currentBugemon;
    }

    // Getters and setters

    /**
     * Sets the current bugemon in the team of the trainer to the specified bugemon.
     * 
     * @param bugemon (Bugemon) the new current bugemon in the team of the trainer.
     */
    public void setCurrentBugemon(Bugemon bugemon) {
        this.currentBugemon = bugemon;
    }

    /**
     * Returns the team of the trainer.
     * 
     * @return (BugemonTeam) the team of the trainer.
     */
    public BugemonTeam getTeam() {
        return team;
    }

    public int getTeamSize() {
        return team.size();
    }
}
