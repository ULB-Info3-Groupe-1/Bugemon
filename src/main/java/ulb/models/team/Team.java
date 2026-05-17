package ulb.models.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.exceptions.BugemonAlreadyPresentInTeamException;
import ulb.models.team.exceptions.BugemonNotInTeamException;
import ulb.models.team.exceptions.TeamAlreadyEmptyException;
import ulb.models.team.exceptions.TeamAlreadyFullException;

/**
 * A team of up to {@value Configuration.Game#MAX_TEAM_SIZE} {@link Bugemon}s. Enforces capacity and uniqueness (by ID).
 * Implements {@link Iterable} for use in enhanced for-loops.
 */
public class Team {
    private final List<PlayerBugemon> members;
    private String name;

    private static final int MAX_SIZE = Configuration.Game.MAX_TEAM_SIZE;

    public Team() {
        this.members = new ArrayList<>();
    }

    public Team(Team other) {
        this.members = new ArrayList<>(other.members);
        this.name = other.name;
    }

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

    public void add(PlayerBugemon bugemon) throws TeamAlreadyFullException, BugemonAlreadyPresentInTeamException {
        if (this.isFull()) {
            throw new TeamAlreadyFullException("Team already full!");
        }
        if (this.contains(bugemon)) {
            throw new BugemonAlreadyPresentInTeamException("This Bugemon is already in the team!");
        }
        this.members.add(bugemon);
    }

    public void remove(PlayerBugemon bugemon) throws TeamAlreadyEmptyException, BugemonNotInTeamException {
        if (this.size() == 0) {
            throw new TeamAlreadyEmptyException("Team already empty!");
        }
        if (!this.contains(bugemon)) {
            throw new BugemonNotInTeamException("This Bugemon is not in the team!");
        }
        this.members.remove(bugemon);
    }

    public List<PlayerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    /**
     * Checks if a Bugemon with the same name is already in the team
     *
     * @param bugemon
     *            (Bugemon) the Bugemon to search for
     * @return (boolean) true if a Bugemon with the same name is already in the team, false otherwise
     */
    public boolean contains(PlayerBugemon bugemon) {
        return this.members.contains(bugemon);
    }

    public void clear() {
        this.members.clear();
    }

    public int getSlotPosition(PlayerBugemon bugemon) {
        return this.members.indexOf(bugemon);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAll(List<PlayerBugemon> bugemons) {
        for (PlayerBugemon bugemon : bugemons) {
            this.add(bugemon);
        }
    }

}
