package ulb.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;

public class TeamFactory {
    /**
     * Generates a random {@link BugemonTeam} of the specified size by sampling
     * without replacement from the given pool of available {@link Bugemon}s.
     *
     * <p>
     * Each selected Bugemon is {@link Bugemon#clone() cloned} before being added
     * to the team so that the originals in {@code bugemonList} are not modified
     * during combat.
     * </p>
     *
     * @param bugemonList the pool of {@link Bugemon}s to sample from; must not
     *                    be {@code null} and must contain at least {@code teamSize}
     *                    distinct entries.
     * @param teamSize    the number of {@link Bugemon}s the resulting team should
     *                    contain; must be between {@code 1} and
     *                    {@code bugemonList.size()} inclusive.
     * @return a new {@link BugemonTeam} containing {@code teamSize} randomly
     *         chosen, cloned {@link Bugemon}s.
     * @throws RuntimeException if cloning a selected {@link Bugemon} fails.
     */
    public static BugemonTeam createRandomTeam(final List<Bugemon> bugemonList,
                                               final int teamSize) {
        List<Bugemon> allBugemons = Parser.getInstance().getBugemons();
        List<Bugemon> pool = new ArrayList<>(allBugemons);
        Collections.shuffle(pool);
        BugemonTeam team = new BugemonTeam();
        for (int i = 0; i < teamSize; i++) {
            team.add(pool.get(i).clone());
        }

        return team;
    }
}
