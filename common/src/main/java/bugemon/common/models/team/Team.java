package bugemon.common.models.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import bugemon.common.Configuration;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.exceptions.BugemonAlreadyPresentInTeamException;
import bugemon.common.models.team.exceptions.BugemonNotInTeamException;
import bugemon.common.models.team.exceptions.TeamAlreadyEmptyException;
import bugemon.common.models.team.exceptions.TeamAlreadyFullException;

/**
 * An ordered collection of up to {@value Configuration.Game#MAX_TEAM_SIZE} {@link PlayerBugemon}s that represents a
 * player's active combat team.
 *
 * <p>
 * Membership is enforced by Bugemon name: no two members may share the same name, and the total count may never exceed
 * the configured maximum. Violations throw unchecked exceptions from {@code bugemon.common.models.team.exceptions}.
 */
public class Team {
    private final List<PlayerBugemon> members;
    private String name;

    private static final int MAX_SIZE = Configuration.Game.MAX_TEAM_SIZE;

    /** Creates an empty team with no name. */
    public Team() {
        this.members = new ArrayList<>();
    }

    /**
     * Copy constructor — creates a shallow copy of {@code other}, sharing the same {@link PlayerBugemon} instances.
     *
     * @param other
     *            the team to copy; must not be {@code null}
     */
    public Team(Team other) {
        this.members = new ArrayList<>(other.members);
        this.name = other.name;
    }

    /**
     * Creates a team pre-populated with the given members.
     *
     * @param members
     *            the initial members; must contain at most {@value Configuration.Game#MAX_TEAM_SIZE} distinct entries
     * @throws IllegalArgumentException
     *             if {@code members} exceeds the maximum size or contains duplicates
     */
    public Team(List<PlayerBugemon> members) {
        if (members.size() > MAX_SIZE) {
            throw new IllegalArgumentException("Team cannot have more than " + MAX_SIZE + " members.");
        }
        if (members.size() != members.stream().distinct().count()) {
            throw new IllegalArgumentException("Team cannot have duplicate members.");
        }
        this.members = new ArrayList<>(members);
    }

    public int size() {
        return this.members.size();
    }

    public boolean isFull() {
        return this.size() == MAX_SIZE;
    }

    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    /**
     * Adds {@code bugemon} to this team.
     *
     * @param bugemon
     *            the Bugemon to add; must not be {@code null}
     * @throws TeamAlreadyFullException
     *             if the team already has {@value Configuration.Game#MAX_TEAM_SIZE} members
     * @throws BugemonAlreadyPresentInTeamException
     *             if a member with the same name is already present
     */
    public void add(PlayerBugemon bugemon) throws TeamAlreadyFullException, BugemonAlreadyPresentInTeamException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }
        if (this.contains(bugemon)) {
            throw new BugemonAlreadyPresentInTeamException("This Bugemon is already in the team!");
        }
        this.members.add(bugemon);
    }

    /**
     * Removes the member whose name matches {@code bugemon} from this team.
     *
     * @param bugemon
     *            the Bugemon to remove; matched by name
     * @throws TeamAlreadyEmptyException
     *             if the team is already empty
     * @throws BugemonNotInTeamException
     *             if no member with the same name exists in the team
     */
    public void remove(PlayerBugemon bugemon) throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }
        PlayerBugemon toRemove = this.findByName(bugemon.getName())
                .orElseThrow(() -> new BugemonNotInTeamException("This Bugemon is not in the team!"));
        this.members.remove(toRemove);
    }

    /**
     * Returns an unmodifiable view of the team members in insertion order.
     *
     * @return an unmodifiable list of current members
     */
    public List<PlayerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    /**
     * Returns {@code true} if a member with the same name as {@code bugemon} is already in this team.
     *
     * @param bugemon
     *            the Bugemon to look up; matched by name
     * @return {@code true} if the team contains a member with that name
     */
    public boolean contains(PlayerBugemon bugemon) {
        return this.members.stream().anyMatch(m -> m.getName().equals(bugemon.getName()));
    }

    /**
     * Returns the team member whose name equals {@code bugemonName}, if present.
     *
     * @param bugemonName
     *            the name to search for
     * @return an {@link Optional} containing the matching member, or empty if none found
     */
    public Optional<PlayerBugemon> findByName(String bugemonName) {
        return this.members.stream().filter(m -> m.getName().equals(bugemonName)).findFirst();
    }

    /** Removes all members from this team and clears its name. */
    public void clear() {
        this.members.clear();
        this.name = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds all Bugémons in {@code bugemons} to this team in iteration order.
     *
     * @param bugemons
     *            the list of Bugémons to add
     * @throws TeamAlreadyFullException
     *             if adding any entry would exceed the maximum size
     * @throws BugemonAlreadyPresentInTeamException
     *             if any entry is already a member
     */
    public void addAll(List<PlayerBugemon> bugemons) {
        for (PlayerBugemon bugemon : bugemons) {
            this.add(bugemon);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Team other = (Team) obj;
        return this.members.equals(other.members) && this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return this.members.hashCode();
    }

}
