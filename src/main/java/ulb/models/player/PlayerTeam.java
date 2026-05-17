package ulb.models.player;

import java.util.Collections;
import java.util.List;

import ulb.models.team.Team;

public class PlayerTeam {
    private final List<PlayerBugemon> members;
    private final String name;

    public PlayerTeam(Team team, String name) {
        this.members = team.getMembers().stream().map(PlayerBugemon::new).toList();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<PlayerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }
}
