package ulb.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

public class TeamFactory {

    private TeamFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a random team of Bugemons from a list of Bugemons and a team size.
     *
     * @param teamSize
     *            the size of the team
     * @return the created team
     */
    public static BugemonTeam createRandomTeam(final List<Bugemon> bugemons, final int teamSize) {
        List<Bugemon> pool = new ArrayList<>(bugemons);
        Collections.shuffle(pool);

        BugemonTeam team = new BugemonTeam();
        pool.stream().limit(teamSize).map(Bugemon::new).forEach(team::add);

        return team;
    }

    /**
     * Creates a random team with one boss.
     *
     * @param teamSize
     *            the size of the team
     * @return the created team
     */
    public static BugemonTeam createRandomBossTeam(final List<Bugemon> bugemons, final int teamSize) {
        Bugemon bossBugemon = bugemons.stream().filter(b -> b.getName().equals(Configuration.Game.BOSS_NAME))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Boss '" + Configuration.Game.BOSS_NAME + "' not found."));
        List<Bugemon> noBossBugemons = bugemons.stream().filter(b -> !b.equals(bossBugemon)).toList();

        BugemonTeam bossTeam = createRandomTeam(noBossBugemons, teamSize - 1);
        bossTeam.add(new Bugemon(bossBugemon));
        bossTeam.shuffle();
        return bossTeam;
    }

}
