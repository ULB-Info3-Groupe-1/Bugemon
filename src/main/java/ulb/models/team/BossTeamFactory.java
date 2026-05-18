package ulb.models.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.repositories.StaticDataRepository;

/** Produces a {@link Team} led by a named boss Bugémon, padded with random members. */
public class BossTeamFactory extends TeamFactory {

    private final String bossName;

    public BossTeamFactory(StaticDataRepository repo, Random random, String bossName) {
        super(repo, random);
        this.bossName = bossName;
    }

    @Override
    public Team create(int size) {
        Team team = new Team();
        this.repo.findByName(this.bossName).map(PlayerBugemon::new).ifPresent(team::add);
        List<Bugemon> available = new ArrayList<>(this.repo.getAllDefaultBugemons());
        available.removeIf(b -> b.name().equals(this.bossName));
        this.fillTeam(size, team, available);
        return team;
    }
}
