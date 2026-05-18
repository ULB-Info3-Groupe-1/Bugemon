package ulb.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/**
 * Produces a {@link Team} led by a named boss Bugémon, padded with random
 * members.
 */
public class BossTeamFactory extends TeamFactory {

    private final String bossName;

    public BossTeamFactory(List<Bugemon> bugemons, Random random, String bossName) {
        super(bugemons, random);
        this.bossName = bossName;
    }

    @Override
    public Team create(int size) {
        this.checkSize(size);
        Team team = new Team();
        this.bugemons.stream().filter(b -> b.name().equals(this.bossName)).findFirst().map(PlayerBugemon::new)
                .ifPresent(team::add);
        List<Bugemon> available = new ArrayList<>(this.bugemons);
        available.removeIf(b -> b.name().equals(this.bossName));
        this.fillTeam(size, team, available);
        return team;
    }
}
