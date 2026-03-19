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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
public class BugemonTeam implements Iterable<Bugemon> {
    // Attributes

    public static final int MAX_SIZE = 6;
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

    public void add(Bugemon bugemon)
            throws TeamAlreadyFullException, BugemonAlreadyExistsException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }

        if (this.contains(bugemon)) {
            throw new BugemonAlreadyExistsException("This Bugemon already in the team!");
        }

        this.team.add(bugemon);
    }

    public void remove(Bugemon bugemon)
            throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }
        if (!this.contains(bugemon)) {
            throw new BugemonNotInTeamException("This Bugemon is not in the team!");
        }
        this.team.removeIf(member -> member.getId().equals(bugemon.getId()));
    }

    /**
     * Returns the select Bugemon with the given ID if it's in the team.
     *
     * @param id (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given ID
     */
    public Optional<Bugemon> get(String id) {
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
        return this.team.stream().anyMatch(member -> member.getId().equals(bugemon.getId()));
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

    public Stream<Bugemon> stream() {
        return this.team.stream();
    }

    public Stream<Bugemon> aliveStream() {
        return this.team.stream().filter(Bugemon::isAlive);
    }

    public Iterator<Bugemon> aliveIterator() {
        return this.team.stream().filter(Bugemon::isAlive).iterator();
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
    public void clear() {
        this.team.clear();
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

    public List<Bugemon> getAll() {
        return this.team;
    }

    public void killAll() {
        this.team.forEach(Bugemon::kill);
    }
}
