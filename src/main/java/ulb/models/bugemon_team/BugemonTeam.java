package ulb.models.bugemon_team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyPresentInTeamException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;

/**
 * A team of up to {@value Configuration.Game#MAX_TEAM_SIZE} {@link Bugemon}s. Enforces capacity and uniqueness (by ID).
 * Implements {@link Iterable} for use in enhanced for-loops.
 */
public class BugemonTeam implements Iterable<Bugemon> {

    private final ArrayList<Bugemon> team;
    private String name;

    /**
     * Creates a new empty team with the default name
     */
    public BugemonTeam() {
        this.name = Configuration.Game.DEFAULT_TEAM_NAME;
        this.team = new ArrayList<>();
    }

    /**
     * Creates a new team with the given name
     *
     * @param name
     *            the name of the team
     */
    public BugemonTeam(String name) {
        this.name = name;
        this.team = new ArrayList<>();
    }

    /**
     * Copies the given team
     *
     * @param other
     *            the team to copy
     */
    public BugemonTeam(BugemonTeam other) {
        this.name = other.name;
        this.team = new ArrayList<>(other.team);
    }

    /**
     * Returns the number of Bugemons in the team
     *
     * @return (int) the number of Bugemons
     */
    public int size() {
        return this.team.size();
    }

    /**
     * Returns whether the team is full
     *
     * @return (boolean) whether the team is full
     */
    public boolean isFull() {
        return this.size() == Configuration.Game.MAX_TEAM_SIZE;
    }

    /**
     * Returns whether the team is empty
     *
     * @return (boolean) whether the team is empty
     */
    public boolean isEmpty() {
        return this.team.isEmpty();
    }

    /**
     * Adds a Bugemon to the team
     *
     * @param bugemon
     *            (Bugemon) the Bugemon to add
     * @throws TeamAlreadyFullException
     *             if the team already has {@value ulb.Configuration.Game#MAX_TEAM_SIZE} members
     * @throws BugemonAlreadyPresentInTeamException
     *             if a Bugemon with the same name is already in the team
     */
    public void add(Bugemon bugemon) throws TeamAlreadyFullException, BugemonAlreadyPresentInTeamException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }

        if (this.contains(bugemon)) {
            throw new BugemonAlreadyPresentInTeamException("This Bugemon already in the team!");
        }

        this.team.add(bugemon);
    }

    /**
     * Removes a Bugemon from the team
     *
     * @param bugemon
     *            (Bugemon) the Bugemon to remove
     * @throws TeamAlreadyEmptyException
     *             if the team is already empty
     * @throws BugemonNotInTeamException
     *             if the Bugemon is not in the team
     */
    public void remove(Bugemon bugemon) throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }
        if (!this.contains(bugemon)) {
            throw new BugemonNotInTeamException("This Bugemon is not in the team!");
        }
        this.team.removeIf(member -> member.getName().equals(bugemon.getName()));
    }

    /**
     * Adds all the Bugemons of another team to this team
     *
     * @param otherTeam
     *            (BugemonTeam) the other team to add the Bugemons from
     * @throws TeamAlreadyFullException
     *             if the team already has {@value ulb.Configuration.Game#MAX_TEAM_SIZE} members
     * @throws BugemonAlreadyPresentInTeamException
     *             if a Bugemon with the same name is already in the team
     */
    public void addAll(BugemonTeam otherTeam) throws TeamAlreadyFullException, BugemonAlreadyPresentInTeamException {
        for (Bugemon bugemon : otherTeam) {
            this.add(bugemon);
        }
    }

    /**
     * Returns the select Bugemon with the given name if it's in the team.
     *
     * @param bugemonName
     *            (String) the ID of the Bugemon to be returned
     * @return (Bugemon) the Bugemon with the given name
     */
    public Optional<Bugemon> get(String bugemonName) {
        return this.team.stream().filter(b -> bugemonName.equals(b.getName())).findFirst();
    }

    /**
     * Checks if a Bugemon with the same name is already in the team
     *
     * @param bugemon
     *            (Bugemon) the Bugemon to search for
     * @return (boolean) true if a Bugemon with the same name is already in the team, false otherwise
     */
    public boolean contains(Bugemon bugemon) {
        return this.team.stream().anyMatch(member -> member.getName().equals(bugemon.getName()));
    }

    /**
     * Adds or removes a bugemon from the team depending on whether it is already in the team.
     *
     * @param bugemon
     *            the bugemon to add or remove
     */
    public void addOrRemoveBugemon(Bugemon bugemon) {
        if (this.contains(bugemon)) {
            this.remove(bugemon);
        } else if (!this.isFull()) {
            this.add(bugemon);
        }
    }

    @Override
    public Iterator<Bugemon> iterator() {
        return this.team.iterator();
    }

    /**
     * Returns a stream of all the Bugemons in the team
     *
     * @return a stream of all the Bugemons in the team
     */
    public Stream<Bugemon> stream() {
        return this.team.stream();
    }

    /**
     * Returns a stream of all the alive Bugemons in the team
     *
     * @return a stream of all the alive Bugemons in the team
     */
    public Stream<Bugemon> aliveStream() {
        return this.team.stream().filter(Bugemon::isAlive);
    }

    /**
     * Returns an iterator of all the alive Bugemons in the team
     *
     * @return an iterator of all the alive Bugemons in the team
     */
    public Iterator<Bugemon> aliveIterator() {
        return this.team.stream().filter(Bugemon::isAlive).iterator();
    }

    /**
     * Removes all the Bugemons from the team
     */
    public void clear() {
        this.team.clear();
    }

    /**
     * Returns the first Bugemon in the team
     *
     * @return (Bugemon) the first Bugemon
     * @throws java.util.NoSuchElementException
     *             if the team is empty
     */
    public Bugemon getFirst() {
        return this.team.getFirst();
    }

    /**
     * Returns a list of all the Bugemons in the team
     *
     * @return a list of {@link Bugemon} : all the Bugemons in the team
     */
    public List<Bugemon> getAll() {
        return this.team;
    }

    /**
     * Kills all the Bugemons in the team
     */
    public void killAll() {
        this.team.forEach(Bugemon::kill);
    }

    /**
     * Restores the HP of all the Bugemons in the team
     */
    public void restoreHp() {
        this.team.forEach(Bugemon::restoreHp);
    }

    /**
     * Returns the position of the given Bugemon in the team
     *
     * @return (int) the position of the Bugemon
     * @throws BugemonNotInTeamException
     *             if the Bugemon is not in the team
     */
    public int getSlotPosition(Bugemon bugemon) throws BugemonNotInTeamException {
        int slot = this.team.indexOf(bugemon);
        if (slot == -1) {
            throw new BugemonNotInTeamException(
                    "This Bugemon is not in the team!\nSlot position cannot be determined.");
        }
        return slot;
    }

    /**
     * Returns the name of the team
     *
     * @return (String) the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the team
     *
     * @param name
     *            (String) the name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BugemonTeam other = (BugemonTeam) obj;
        return this.name.equals(other.name) && this.team.equals(other.team);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.name, this.team);
    }
}
