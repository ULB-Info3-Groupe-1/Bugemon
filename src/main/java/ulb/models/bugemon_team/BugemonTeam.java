/**
 * Data class representing a team of up to 6 Bugemons.
 *
 * <p>Provides methods to add, remove, and query Bugemons in the team,
 * as well as a factory method to generate a random team from a given pool.</p>
 *
 * @author  Brisbois Philippe
 * @author  Morbee Matteo
 * @date    27 feb. 2026
 * @version 1.1
 * @since   1.0
 */

package ulb.models.bugemon_team;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;

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

        if (this.contains(bugemon)) {
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

    public void removeBugemon(Bugemon bugemon) {
        this.removeBugemon(bugemon.getId());
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     *
     * @param bugemon (Bugemon) the Bugemon to be removed from the team
     */
    public void removeBugemon(String id) {
        if (this.size == 0) {
            throw new IllegalStateException("Team already empty!");
        }

        int currentSize = this.size;

        for (int i = 0; i < MAX_SIZE; i++) {
            if (this.team[i] != null && id.equals(this.team[i].getId())) {
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
     * Returns a copy of the team array
     *
     * @return (List<Bugemon>) a copy of the team array
     */
    public List<Bugemon> getTeam() {
        return Arrays.asList(team.clone());
    }

    /**
     * Returns the select Bugemon with the given ID if it's in the team.
     *
     * @param id (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given ID
     */
    public Bugemon getBugemon(String id) {
        for (Bugemon bugemon : this.team) {
            if (bugemon != null && id.equals(bugemon.getId())) {
                return bugemon;
            }
        }
        throw new IllegalArgumentException(
            "No Bugemon with the given ID found in the team!"
        );
    }

    /**
     * Checks if a Bugemon with the same ID is already in the team
     *
     * @param bugemon (Bugemon) the Bugemon to search for
     * @return (boolean) true if a Bugemon with the same ID is already in the team,
     *         false otherwise
     */
    public boolean contains(Bugemon bugemon) {
        return this.contains(bugemon.getId());
    }

    /**
     * Checks if a Bugemon with the same ID is already in the team
     *
     * @param id (String) the ID of Bugemon to search for
     * @return (boolean) true if a Bugemon with the given ID is already in the team,
     *         false otherwise
     */
    public boolean contains(String id) {
        for (Bugemon bugemon : this.team) {
            if (bugemon != null && id.equals(bugemon.getId())) return true;
        }
        return false;
    }

    /**
     * Generates a random bugemon team of 6 bugemons
     * @return the bugemon list created
     */
    public static BugemonTeam createRandomTeam(
        final List<Bugemon> bugemonList,
        final int teamSize
    ) {
        Random rand = new Random();
        BugemonTeam randomTeam = new BugemonTeam();

        while (randomTeam.size() != teamSize) {
            int randomIndex = rand.nextInt(bugemonList.size());
            if (!randomTeam.contains(bugemonList.get(randomIndex).getId())) {
                randomTeam.addBugemon(
                    new Bugemon(bugemonList.get(randomIndex))
                );
            }
        }

        return randomTeam;
    }

    /**
     * Resets the state of all Bugemons in the team to their default values.
     */
    public void reset() {
        return;
    }
}
