package ulb.services;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.Trainer;

/** Static utility methods for XP distribution and level-up processing after combat. */
public class LevelUpService {
    private LevelUpService() {
        // Private constructor to prevent instantiation
    }

    /** XP formula: {@code 30 * floorLevel * multiplier * opponentCount}. */
    public static int xpGain(final int floorLevel, final int multiplier, final int opponentCount) {
        return 30 * floorLevel * multiplier * opponentCount;
    }

    /** Convenience overload using floor=1 and multiplier=1 (placeholder values). */
    public static List<LevelUp> distributeXpAndGetLevelUps(final Trainer winner, final Trainer loser) {
        final int floorLevel = 1; // Placeholder for actual floor level
        final int multiplier = 1; // Placeholder for actual multiplier based on combat conditions

        return distributeXpAndGetLevelUps(winner, loser, floorLevel, multiplier);
    }

    /**
     * Distributes XP equally among all participating Bugemons and returns the resulting level-ups.
     *
     * @return one {@link LevelUp} per level crossed per Bugemon
     */
    public static List<LevelUp> distributeXpAndGetLevelUps(final Trainer winner, final Trainer loser, final int floor,
            final int multiplier) {
        final Set<Bugemon> participatingBugemon = winner.getParticipatingBugemons();

        final int nAdversaries = loser.getTeamSize();
        final int numParticipatingBugemon = participatingBugemon.size();

        final int xpWon = xpGain(floor, multiplier, nAdversaries);
        final int xpPerBugemon = (xpWon / numParticipatingBugemon);

        // My functional bros will love this one :-D
        //
        // process each participating Bugemon and collect all level-ups
        return participatingBugemon.stream().flatMap(bugemon -> {
            int numLevelUps = bugemon.gainXp(xpPerBugemon);

            if (numLevelUps > 0) {
                bugemon.restoreHp();
            }

            // create one LevelUp object per level gained (e.g., 2 levels = 2 LevelUps)
            return IntStream.range(0, numLevelUps).mapToObj(i -> new LevelUp(bugemon));
        }).toList();
    }
}
