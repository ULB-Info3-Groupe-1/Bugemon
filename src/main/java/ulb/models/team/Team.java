package ulb.models.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        PlayerBugemon toRemove = this.findByName(bugemon.getName())
                .orElseThrow(() -> new BugemonNotInTeamException("This Bugemon is not in the team!"));
        this.members.remove(toRemove);
    }

    public List<PlayerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public boolean contains(PlayerBugemon bugemon) {
        return this.members.stream().anyMatch(m -> m.getName().equals(bugemon.getName()));
    }

    public Optional<PlayerBugemon> findByName(String bugemonName) {
        return this.members.stream().filter(m -> m.getName().equals(bugemonName)).findFirst();
    }

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
