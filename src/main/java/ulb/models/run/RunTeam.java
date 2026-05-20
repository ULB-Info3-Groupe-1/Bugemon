package ulb.models.run;

import java.util.Collections;
import java.util.List;

import ulb.models.team.Team;

/**
 * Represents a team during a run.
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

    public int getSlotOfMember(RunBugemon runBugemon) {
        return this.members.indexOf(runBugemon);
    }
}
