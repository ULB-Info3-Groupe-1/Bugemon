package ulb.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/**
 * Produces a {@link Team} led by a named boss Bugémon, padded with random members.
 */
public class BossTeamFactory extends TeamFactory {

    public BossTeamFactory(Random random) {
        super(random);
    }

    @Override
    public Team create(int size, List<Bugemon> bugemons) {
        this.checkSize(size);
        Bugemon boss = bugemons.stream().filter(Bugemon::isBoss).findAny()
                .orElseThrow(() -> new IllegalStateException("No boss Bugemon found"));
        Team team = new Team();
        team.add(new PlayerBugemon(boss));
        List<Bugemon> available = new ArrayList<>(bugemons);
        available.removeIf(Bugemon::isBoss);
        this.fillTeam(size, team, available);
        return team;
    }
}
