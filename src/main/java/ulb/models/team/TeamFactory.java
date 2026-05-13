package ulb.models.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ulb.models.bugemon.Bugemon;
import ulb.repositories.BugemonRepository;

// TODO: should these methods be static?

public class TeamFactory {

    private TeamFactory() {
    }

    public static Team generateRandom(BugemonRepository repo, int size, Random random) {
        Team team = new Team();

        List<Bugemon> available = new ArrayList<>(repo.findAll());
        available.removeIf(Bugemon::isBoss);

        moveBugemonsToTeam(size, team, available, random);
        return team;
    }

    public static Team generateBossTeam(
            BugemonRepository repo, int size, String bossId, Random random) {
        Team team = new Team();

        repo.findById(bossId).ifPresent(team::add);

        List<Bugemon> available = new ArrayList<>(repo.findAll());
        available.removeIf(b -> b.id().equals(bossId)); // remove boss to avoid adding twice

        moveBugemonsToTeam(size, team, available, random);
        return team;
    }

    /**
     * Moves numBugemons Bugemon from available to team.
     */
    private static void moveBugemonsToTeam(int numBugemons, Team team, List<Bugemon> available, Random random) {
        while (team.size() < numBugemons && !available.isEmpty()) {
            int idx = random.nextInt(available.size());
            team.add(available.remove(idx));
        }
    }
}
