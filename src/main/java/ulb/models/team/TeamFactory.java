package ulb.models.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.repositories.StaticDataRepository;

// TODO: should these methods be static?

public class TeamFactory {

    private TeamFactory() {
    }

    public static Team generateRandom(StaticDataRepository repo, int size, Random random) {
        Team team = new Team();

        List<Bugemon> available = new ArrayList<>(repo.getAllDefaultBugemons());
        available.removeIf(Bugemon::isBoss);

        moveBugemonsToTeam(size, team, available, random);
        return team;
    }

    public static Team generateBossTeam(StaticDataRepository repo, int size, String bossName, Random random) {
        Team team = new Team();

        repo.findByName(bossName).map(PlayerBugemon::new).ifPresent(team::add);

        List<Bugemon> available = new ArrayList<>(repo.getAllDefaultBugemons());
        available.removeIf(b -> b.name().equals(bossName)); // remove boss to avoid adding twice

        moveBugemonsToTeam(size, team, available, random);
        return team;
    }

    /**
     * Moves numBugemons Bugemon from available to team.
     */
    private static void moveBugemonsToTeam(int numBugemons, Team team, List<Bugemon> available, Random random) {
        while (team.size() < numBugemons && !available.isEmpty()) {
            int idx = random.nextInt(available.size());
            team.add(new PlayerBugemon(available.remove(idx)));
        }
    }
}
