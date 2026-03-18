package ulb.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;
import ulb.utils.Parser;

public class TeamFactory {
    private static final Random RANDOM = new Random();
    private static final List<Bugemon> BUGEMON_LIST = Parser.getInstance().getBugemons();
    private static final String BOSS_ID = "finalboss";

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
    public static BugemonTeam createRandomTeam(final int teamSize) {
        BugemonTeam randomTeam = new BugemonTeam();

        while (randomTeam.size() != teamSize) {
            int randomIndex = RANDOM.nextInt(BUGEMON_LIST.size());
            Bugemon chosenBugemon = BUGEMON_LIST.get(randomIndex);

            if (!randomTeam.contains(chosenBugemon.getId())) {
                try {
                    randomTeam.addBugemon(chosenBugemon.clone());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException("Failed to clone Bugemon: " + chosenBugemon.getId(),
                            e);
                }
            }
        }
        return randomTeam;
    }

    public static BugemonTeam createBossTeam() {

        final Optional<Bugemon> bossBugemon = BUGEMON_LIST.stream().filter(obj -> obj.getId().equals(BOSS_ID))
                .findFirst();
        BugemonTeam bossTeam = new BugemonTeam();

        bossTeam.addBugemon(bossBugemon.orElseThrow(
                () -> new RuntimeException("Boss Bugemon with ID '" + BOSS_ID + "' not found in the list.")));

        return bossTeam;
    }
}
