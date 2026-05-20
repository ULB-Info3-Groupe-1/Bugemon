package ulb.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/**
 * Produces a {@link Team} led by a named boss Bugémon, padded with random
 * members.
 */
public class BossTeamFactory extends TeamFactory {

    private static final String BOSS_NAME = Configuration.Game.BOSS_NAME;

    public BossTeamFactory(Random random) {
        super(random);
    }

    @Override
    public Team create(int size, List<Bugemon> bugemons) {
        this.checkSize(size);
        Bugemon boss = bugemons.stream()
                .filter(b -> b.name().equals(BOSS_NAME))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown boss: " + BOSS_NAME));
        Team team = new Team();
        team.add(new PlayerBugemon(boss));
        List<Bugemon> available = new ArrayList<>(bugemons);
        available.removeIf(b -> b.name().equals(BOSS_NAME));
        this.fillTeam(size, team, available);
        return team;
    }
}
