/**
 * File name : BugemonTeam.java
 * Description : Data class representing a team of Bugemons
 * 
 * @author Brisbois Philippe
 * @coauthor Morbee Matteo
 * @date 27 feb. 2026
 * @version 1.1
 */

package ulb.models.bugemon_team;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;

/**
 * This class represents a team of up to 6 Bugemons
 */
public class BugemonTeam {

    // Attributes

    private final int MAX_SIZE = 6;
    private final Bugemon[] team = new Bugemon[MAX_SIZE];
    private int size = 0;

    public BugemonTeam() {}

    /**
     * Returns the number of Bugemons in the team
     *
     * @return (int) the number of Bugemons currently in the team
     */
    public int size() {
        return this.size;
    }

    /**
     * Checks if the team is full (i.e., has 6 Bugemons) or empty (i.e., has 0
     * Bugemons)
     *
     * @return (boolean) true if the team is full, false otherwise; true if the team
     *         is empty, false otherwise
     */
    public boolean isFull() {
        return this.size == MAX_SIZE;
    }

    /**
     * Checks if the team is empty (i.e., has 0 Bugemons)
     *
     * @return (boolean) true if the team is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Adds a Bugemon to the team if there is space and it is not already in the
     * team. If the
     * team is full or the Bugemon is already in the team, an exception is thrown.
     *
     * @param bugemon (Bugemon) the Bugemon to be added to the team
     */
    public void addBugemon(Bugemon bugemon) {
        if (this.isFull()) {
            throw new IllegalStateException("Team already full!");
        }

        if (this.isDuplicate(bugemon)) {
            throw new BugemonAlreadyExistsException(
                "This Bugemon already in the team!"
            );
        }

        for (int i = 0; i < MAX_SIZE; i++) {
            if (this.team[i] == null) {
                this.team[i] = bugemon;
                break;
            }
        }

        this.size++;
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     *
     * @param bugemon (Bugemon) the Bugemon to be removed from the team
     */
    public void removeBugemon(Bugemon bugemon) {
        if (this.size == 0) {
            throw new IllegalStateException("Team already empty!");
        }

        int currentSize = this.size;

        for (int i = 0; i < MAX_SIZE; i++) {
            if (this.team[i] != null && this.team[i].equals(bugemon)) {
                this.team[i] = null;
                this.size--;
                break;
            }
        }

        if (currentSize == this.size) {
            throw new IllegalArgumentException("Bugemon not in the team!");
        }
    }

    /**
     * Removes a Bugemon from the team at the specified index if it is in the team.
     * If the
     * team is empty.
     *
     * @param index (int) the index of the Bugemon to be removed from the team
     */
    public void removeBugemon(int index) {
        if (this.size == 0) {
            throw new IllegalStateException("Team already full!");
        }
        if (index >= MAX_SIZE || index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds!");
        }
        if (this.team[index] == null) {
            throw new IllegalArgumentException("Cannot remove an empty slot!");
        }

        this.team[index] = null;
        this.size--;
    }

    /**
     * Returns a copy of the team array
     *
     * @return (Bugemon[]) a copy of the team array
     */
    public Bugemon[] getTeam() {
        return this.team.clone();
    }

    /**
     * Returns the Bugemon at the specified index if it is in the team. If the index
     * is out of bounds or the slot is empty, an exception is thrown.
     *
     * @param index (int) the index of the Bugemon to be returned
     * @return (Bugemon) the Bugemon at the specified index
     */
    public Bugemon getBugemon(int index) {
        if (index >= MAX_SIZE || index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds!");
        }
        if (this.team[index] == null) {
            throw new IllegalArgumentException("Cannot get an empty slot!");
        }

        return this.team[index];
    }

    /**
     * Returns the select Bugemon with the given ID if it's in the team.
     * 
     * @param id (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given ID
     */
    public Bugemon getBugemonById(String id) {
        for (Bugemon bugemon : this.team) {
            if (bugemon != null && bugemon.getId().equals(id)) {
                return bugemon;
            }
        }
        throw new IllegalArgumentException("No Bugemon with the given ID found in the team!");
    }

    /**
     * Checks if a Bugemon with the same ID is already in the team
     *
     * @param bugemon (Bugemon) the Bugemon to be checked for duplication
     * @return (boolean) true if a Bugemon with the same ID is already in the team,
     *         false otherwise
     */
    private boolean isDuplicate(Bugemon bugemon) {

        for (Bugemon b : this.team) {
            if (b != null && bugemon.getId().equals(b.getId())) return true;
        }
        return false;
    }
}
