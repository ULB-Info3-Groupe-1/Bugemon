package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.Trainer;

public class CombatXpDistributor {

    /** XP formula: {@code 30 * floorLevel * multiplier * opponentCount}. */
    public int xpGain(final int floorLevel, final int multiplier, final int opponentCount) {
        return 30 * floorLevel * multiplier * opponentCount;
    }

    public List<LevelUp> distributeXp(CombatContext combatCtx) {
        return this.distributeXp(combatCtx.winner(), combatCtx.loser());
    }

    /** Convenience overload using floor=1 and multiplier=1 (placeholder values). */
    private List<LevelUp> distributeXp(final Trainer winner, final Trainer loser) {
        final int floorLevel = 1; // Placeholder for actual floor level
        final int multiplier = 1; // Placeholder for actual multiplier based on combat conditions

        return this.distributeXp(winner, loser, floorLevel, multiplier);
    }

    /**
     * Distributes XP equally among all participating Bugemons and returns the resulting level-ups.
     *
     * @return one {@link LevelUp} per level crossed per Bugemon
     */
    private List<LevelUp> distributeXp(final Trainer winner, final Trainer loser, final int floor,
            final int multiplier) {
        final Set<Bugemon> participatingBugemon = winner.getParticipatingBugemons();

        final int nAdversaries = loser.getTeamSize();
        final int numParticipatingBugemon = participatingBugemon.size();

        final int xpWon = this.xpGain(floor, multiplier, nAdversaries);
        final int xpPerBugemon = (xpWon / numParticipatingBugemon);

        List<LevelUp> generatedLevelUps = new ArrayList<>();
        for (Bugemon b : participatingBugemon) {
            generatedLevelUps.addAll(this.distributeXp(b, xpPerBugemon));
        }

        return generatedLevelUps;
    }

    private List<LevelUp> distributeXp(Bugemon bugemon, int amount) {
        int numLevelUps = bugemon.gainXp(amount);

        if (numLevelUps > 0) {
            bugemon.restoreHp();
        }

        List<LevelUp> generateLevelUps = new ArrayList<>();
        for (int i = 0; i < numLevelUps; i++) {
            generateLevelUps.add(new LevelUp(bugemon));
        }

        return generateLevelUps;
    }
}
