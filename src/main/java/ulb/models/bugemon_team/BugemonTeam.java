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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;

/**
 * Represents a team of up to {@value #MAX_SIZE} {@link Bugemon}s owned by a
 * trainer.
 *
 * <p>
 * A {@code BugemonTeam} stores Bugemons in a fixed-size array and exposes
 * operations to add, remove, and query members. The team implements
 * {@link Iterable} so it can be used directly in enhanced for-loops; the
 * iterator skips {@code null} slots transparently.
 * </p>
 *
 * <p>
 * All mutating operations ({@link #addBugemon}, {@link #removeBugemon}) enforce
 * the team's capacity and uniqueness constraints, throwing the appropriate
 * unchecked exceptions on violation.
 * </p>
 *
 * @see Bugemon
 * @see ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException
 */
public class BugemonTeam extends AbstractCollection<Bugemon> {
    // Attributes

    private static final int MAX_SIZE = 6;
    private final ArrayList<Bugemon> team = new ArrayList<>();

    /**
     * Constructs an empty {@code BugemonTeam} with no members.
     *
     * <p>
     * After construction {@link #size()} returns {@code 0} and
     * {@link #isEmpty()} returns {@code true}.
     * </p>
     */
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
     * Checks if the team is full (i.e., has 6 Bugemons)
     *
     * @return (boolean) true if the team is full, false otherwise
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
    public void addBugemon(Bugemon bugemon)
            throws TeamAlreadyFullException, BugemonAlreadyExistsException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }

        if (this.contains(bugemon)) {
            throw new BugemonAlreadyExistsException("This Bugemon already in the team!");
        }

        this.team.add(bugemon);
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     *
     * <p>
     * This is a convenience overload of {@link #removeBugemon(String)} that
     * extracts the ID from the given {@link Bugemon} and delegates to it.
     * </p>
     *
     * @param bugemon (Bugemon) the Bugemon to be removed from the team
     * @throws TeamAlreadyEmptyException if the team contains no Bugemons.
     * @throws BugemonNotInTeamException if no Bugemon with the same ID exists
     *                                   in the team.
     */
    public void removeBugemon(Bugemon bugemon) {
        this.removeBugemon(bugemon.getId());
    }

    /**
     * Removes a Bugemon from the team if it is in the team. If the team is empty or
     * the Bugemon is not in the team, an exception is thrown.
     *
     * @param id (String) the ID of the Bugemon to be removed from the team
     */
    public void removeBugemon(String id)
            throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }

        this.team.stream()
                .filter(b -> id.equals(b.getId()))
                .findFirst()
                .ifPresentOrElse(
                        (b)
                                -> { this.team.remove(b); },
                        () -> { throw new BugemonNotInTeamException("Bugemon not in the team!"); });
    }

    /**
     * Returns the select Bugemon with the given ID if it's in the team.
     *
     * @param id (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given ID
     */
    public Optional<Bugemon> getBugemon(String id) {
        return this.team.stream().filter(b -> id.equals(b.getId())).findFirst();
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

    /**
     * Returns an {@link Iterator} over the non-{@code null} {@link Bugemon}s
     * in this team, in the order they were added.
     *
     * <p>
     * Empty slots (i.e., {@code null} entries in the backing array) are
     * silently skipped, so the iterator always yields exactly {@link #size()}
     * elements.
     * </p>
     *
     * @return an iterator over the live members of this team.
     */
    @Override
    public Iterator<Bugemon> iterator() {
        return this.team.iterator();
    }

    /**
     * Resets every {@link Bugemon} in the team to its initial state, restoring
     * all stats to the values they had when the Bugemon was first constructed.
     *
     * <p>
     * This method is typically called at the end of a combat session so that
     * the team can be reused for a subsequent battle without retaining any
     * in-combat stat modifications.
     * </p>
     *
     * @see Bugemon#reset()
     */
    public void reset() {
        this.team.forEach(Bugemon::reset);
    }

    /**
     * Returns the first {@link Bugemon} in the team, in insertion order.
     *
     * <p>
     * This is a convenience method equivalent to retrieving the element at
     * index {@code 0} of the backing list. It is typically used to initialise
     * the active Bugemon when a {@link ulb.models.trainer.Trainer} is
     * constructed.
     * </p>
     *
     * @return the first {@link Bugemon} in the team; never {@code null} if the
     *         team is non-empty.
     * @throws java.util.NoSuchElementException if the team is empty.
     */
    public Bugemon getFirst() {
        return this.team.getFirst();
    }
}
