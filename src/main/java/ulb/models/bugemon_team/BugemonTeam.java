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

import java.util.Iterator;
import java.util.Optional;
import java.util.AbstractCollection;
import java.util.ArrayList;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;

/**
 * This class represents a team of up to 6 Bugemons
 */
public class BugemonTeam extends AbstractCollection<Bugemon> {

    // Attributes

    private static final int MAX_SIZE = 6;
    private final ArrayList<Bugemon> team =  new ArrayList<>();

    public BugemonTeam() {}

    /**
     * Returns the number of Bugemons in the team
     *
     * @return (int) the number of Bugemons currently in the team
     */
    @Override
    public int size() {
        return this.team.size();
    }

    /**
     * Checks if the team is full (i.e., has 6 Bugemons) or empty (i.e., has 0
     * Bugemons)
     *
     * @return (boolean) true if the team is full, false otherwise; true if the team
     *         is empty, false otherwise
     */
    public boolean isFull() {
        return this.size() == MAX_SIZE;
    }

    /**
     * Checks if the team is empty (i.e., has 0 Bugemons)
     *
     * @return (boolean) true if the team is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.team.isEmpty();
    }

    /**
     * Adds a Bugemon to the team if there is space and it is not already in the
     * team. If the
     * team is full or the Bugemon is already in the team, an exception is thrown.
     *
     * @param bugemon (Bugemon) the Bugemon to be added to the team
     */
    public void addBugemon(Bugemon bugemon) throws TeamAlreadyFullException, BugemonAlreadyExistsException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }

        if (this.contains(bugemon)) {
            throw new BugemonAlreadyExistsException(
                "This Bugemon already in the team!"
            );
        }

        this.team.add(bugemon);
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     * @param bugemon (Bugemon) the Bugemon to be removed from the team
     */
    public void removeBugemon(Bugemon bugemon) {
        this.removeBugemon(bugemon.getId());
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     *
     * @param bugemon (Bugemon) the Bugemon to be removed from the team
     */
    public void removeBugemon(String id) throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }

        this.team.stream()
            .filter(b -> id.equals(b.getId()))
            .findFirst()
            .ifPresentOrElse(
                (b) -> {
                    this.team.remove(b);
                },
                () -> {
                    throw new BugemonNotInTeamException("Bugemon not in the team!");
                }
            );
    }

    /**
     * Returns the select Bugemon with the given ID if it's in the team.
     *
     * @param id (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given ID
     */
    public Optional<Bugemon> getBugemon(String id) {
        return this.team.stream()
                .filter(b -> id.equals(b.getId()))
                .findFirst();
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
        return this.getBugemon(id).isPresent();
    }

    @Override
    public Iterator<Bugemon> iterator() {
        return this.team.iterator();
    }

    /**
     * Reset the state of all Bugemons in the team to their initial state, restoring their original stats.
     */
    public void reset() {
        this.team.forEach(Bugemon::reset);
    }

    /**
     * Returns the first bugemon in the team.
     */
    public Bugemon getFirst() {
        return this.team.getFirst();
    }
}
