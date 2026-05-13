package ulb.models.run;

import java.util.Collections;
import java.util.List;

import ulb.models.player.PlayerTeam;

/**
 * Represents a team during a run.
 */
public class RunTeam {
    private final List<RunBugemon> members;

    public RunTeam(List<RunBugemon> members) {
        this.members = List.copyOf(members);
    }

    public static RunTeam fromTeam(PlayerTeam team) {
        List<RunBugemon> runMembers = team.getMembers().stream().map(RunBugemon::new).toList();
        return new RunTeam(runMembers);
    }

    public List<RunBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    public int size() {
        return this.members.size();
    }
}
