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

import java.lang.Iterable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;

/**
 * This class represents a team of up to 6 Bugemons
 */
public class BugemonTeam implements Iterable<Bugemon> {

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
     * Returns a shallow copy of the team array in a List
     *
     * @return (List<Bugemon>) the List of the team array
     */
    public List<Bugemon> getTeam() {
        return Arrays.asList(this.team);
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
     * Returns the Bugemon at the specified index in the team array
     *
     * @param index (int) the index of the Bugemon to be returned
     * @return (Bugemon) the Bugemon at the specified index
     */
    public Bugemon get(int index) {
        if (index < 0 || index >= MAX_SIZE) {
            throw new IndexOutOfBoundsException(
                "Index " +
                    index +
                    " is out of bounds for team of size " +
                    MAX_SIZE
            );
        }
        return this.team[index];
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
            Bugemon chosenBugemon = bugemonList.get(randomIndex);

            if (!randomTeam.contains(chosenBugemon.getId())) {
                try {
                    randomTeam.addBugemon(chosenBugemon.clone());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(
                        "Failed to clone Bugemon: " + chosenBugemon.getId(),
                        e
                    );
                }
            }
        }

        return randomTeam;
    }

    @Override
    public Iterator<Bugemon> iterator() {
        return Arrays.stream(this.team)
                    .filter(b -> b != null)
                    .iterator();
    }
}
