package ulb.models.player;

import java.util.Collections;
import java.util.List;

public class PlayerTeam {
    private final List<PlayerBugemon> members;
    private final String name;

    public PlayerTeam(List<PlayerBugemon> members, String name) {
        this.members = members;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<PlayerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }
}
