package ulb.models.run;

import java.util.Collections;
import java.util.List;

import ulb.models.team.Team;

/**
 * Represents the player's team for the duration of an active run.
 *
 * <p>
 * Created from a persistent {@link ulb.models.team.Team} via {@link #fromTeam(ulb.models.team.Team)}, which wraps each
 * member in a {@link RunBugemon} with full HP. The member list is immutable after construction.
 */
public class RunTeam {
    private final String teamName;
    private final List<RunBugemon> members;

    public RunTeam(String teamName, List<RunBugemon> members) {
        this.teamName = teamName;
        this.members = List.copyOf(members);
    }

    public static RunTeam fromTeam(Team team) {
        List<RunBugemon> runMembers = team.getMembers().stream().map(RunBugemon::new).toList();
        return new RunTeam(team.getName(), runMembers);
    }

    public String getName() {
        return this.teamName;
    }

    public List<RunBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public int size() {
        return this.members.size();
    }

    /**
     * Returns the zero-based slot index of {@code runBugemon} within this team.
     *
     * @param runBugemon
     *            the Bugemon to locate
     * @return the slot index, or {@code -1} if the Bugemon is not a member of this team
     */
    public int getSlotOfMember(RunBugemon runBugemon) {
        return this.members.indexOf(runBugemon);
    }
}
