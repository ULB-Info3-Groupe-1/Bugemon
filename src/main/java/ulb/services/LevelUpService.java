package ulb.services;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.Trainer;

/**
 * Utility class providing helper methods for level-up and experience point calculations.
 * <p>
 * Handles XP requirements per level, XP distribution after combat, and level-up processing
 * for Bugemons.
 * </p>
 * <p>
 * This class is not meant to be instantiated; all methods are static.
 * </p>
 */
public class LevelUpService {
    private static final int BASE_XP = 50;

    private List<LevelUp> pendingLevelUp;

    public LevelUpService() {
        this.pendingLevelUp = new ArrayList<>();
    }

    /**
     * Calculates the XP required to reach a given level.
     * <p>
     * Uses a custom formula with specific values for levels 1-4, and a linear
     * progression for higher levels.
     * </p>
     *
     * @param level the target level
     * @return the total XP required to reach the specified level
     */
    public static int xpRequiredForLevel(final int level) {
        switch (level) {
            case 1 -> {
                return BASE_XP;
            }
            case 2 -> {
                return BASE_XP * 3; // 150
            }
            case 3 -> {
                return BASE_XP * 5; // 250
            }
            case 4 -> {
                return BASE_XP * 7; // 350
            }
            default -> {
                return BASE_XP + BASE_XP * (level - 1);
            }
        }
    }

    /**
     * Calculates the XP gained from defeating opponents in combat.
     *
     * @param floorLevel the current floor level (affects XP gain)
     * @param multiplier a multiplier based on combat conditions
     * @param opponentCount the number of opponents defeated
     * @return the total XP gained
     */
    public static int xpGain(final int floorLevel, final int multiplier, final int opponentCount) {
        return 30 * floorLevel * multiplier * opponentCount;
    }

    /**
     * Distributes XP to the winner's team after combat using default floor level and multiplier.
     * <p>
     * This is a convenience method that uses placeholder values for floor level (1) and
     * multiplier (1).
     * </p>
     *
     * @param winner the trainer who won the combat
     * @param loser the trainer who lost the combat
     * @return the XP awarded to each participating Bugemon
     */
    public static int distributeXp(final Trainer winner, final Trainer loser) {
        final int floorLevel = 1; // Placeholder for actual floor level
        final int multiplier = 1; // Placeholder for actual multiplier based on combat conditions

        return distributeXp(winner, loser, floorLevel, multiplier);
    }

    /**
     * Distributes XP to the winner's team after combat.
     * <p>
     * XP is divided equally among all Bugemons that participated in the fight.
     * Only Bugemons that participated (as determined by {@link Bugemon#getParticipation()})
     * receive XP.
     * </p>
     *
     * @param winner the trainer who won the combat
     * @param loser the trainer who lost the combat
     * @param floor the current floor level
     * @param multiplier a multiplier based on combat conditions
     * @return the XP awarded to each participating Bugemon
     */
    public static int distributeXp(final Trainer winner, final Trainer loser, final int floor,
                                   final int multiplier) {
        final List<Bugemon> participatingBugemon =
                winner.getTeam().stream().filter(Bugemon::getParticipation).toList();

        final int nAdversaries = loser.getTeamSize();
        final int numParticipatingBugemon = participatingBugemon.size();

        final int xpWon = xpGain(floor, multiplier, nAdversaries);
        final int xpPerBugemon = (int)(xpWon / numParticipatingBugemon);

        participatingBugemon.forEach(b -> b.addXp(xpPerBugemon));

        return xpPerBugemon;
    }

    public boolean hasPendingLevelUp() {
        return !this.pendingLevelUp.isEmpty();
    }

    /**
     * Processes level-ups for all Bugemons that have gained sufficient XP.
     * <p>
     * Checks each Bugemon's XP against the requirement for their next level,
     * and creates a {@link LevelUp} object for each Bugemon that qualifies.
     * </p>
     *
     * @param bugemons the list of Bugemons to check for level-ups
     * @return a list of {@link LevelUp} objects for all Bugemons that leveled up
     */
    public void levelUp(final List<Bugemon> bugemons) {
        this.pendingLevelUp = bugemons.stream()
                .filter(b -> b.getXp() >= xpRequiredForLevel(b.getLevel()))
                .map(b -> b.levelUp())
                .toList();
    }
}
