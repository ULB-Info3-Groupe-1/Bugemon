package ulb.models.bugemon_team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;

/**
 * A team of up to {@value #MAX_SIZE} {@link Bugemon}s. Enforces capacity and uniqueness (by ID). Implements
 * {@link Iterable} for use in enhanced for-loops.
 */
public class BugemonTeam implements Iterable<Bugemon> {

    private final ArrayList<Bugemon> team;
    private String name;

    public BugemonTeam() {
        this.name = Configuration.Game.DEFAULT_TEAM_NAME;
        this.team = new ArrayList<>();
    }

    public BugemonTeam(String name) {
        this.name = name;
        this.team = new ArrayList<>();
    }

    public BugemonTeam(BugemonTeam other) {
        this.name = other.name;
        this.team = new ArrayList<>(other.team);
    }

    public int size() {
        return this.team.size();
    }

    public boolean isFull() {
        return this.size() == Configuration.Game.MAX_TEAM_SIZE;
    }

    public boolean isEmpty() {
        return this.team.isEmpty();
    }

    /**
     * @throws TeamAlreadyFullException
     *             if the team already has {@value #MAX_SIZE} members
     * @throws BugemonAlreadyExistsException
     *             if a Bugemon with the same ID is already in the team
     */
    public void add(Bugemon bugemon) throws TeamAlreadyFullException, BugemonAlreadyExistsException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }

        if (this.contains(bugemon)) {
            throw new BugemonAlreadyExistsException("This Bugemon already in the team!");
        }

        this.team.add(bugemon);
    }

    /**
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
     * Returns the select Bugemon with the given name if it's in the team.
     *
     * @param name
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

    public Stream<Bugemon> stream() {
        return this.team.stream();
    }

    public Stream<Bugemon> aliveStream() {
        return this.team.stream().filter(Bugemon::isAlive);
    }

    public Iterator<Bugemon> aliveIterator() {
        return this.team.stream().filter(Bugemon::isAlive).iterator();
    }

    public void clear() {
        this.team.clear();
    }

    /**
     * @throws java.util.NoSuchElementException
     *             if the team is empty
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

    public void restoreHp() {
        this.team.forEach(Bugemon::restoreHp);
    }

    /**
     * @throws BugemonNotInTeamException
     *             if the Bugemon is not in the team
     */
    public int getSlotPosition(Bugemon bugemon) {
        int slot = this.team.indexOf(bugemon);
        if (slot == -1) {
            throw new BugemonNotInTeamException(
                    "This Bugemon is not in the team!\nSlot position cannot be determined.");
        }
        return slot;
    }

    public String getName() {
        return this.name;
    }

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
        return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.team, other.team);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.name, this.team);
    }
}
