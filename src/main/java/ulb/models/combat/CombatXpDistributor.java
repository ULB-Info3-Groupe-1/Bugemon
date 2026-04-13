package ulb.models.combat;

import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

public class CombatXpDistributor implements ICombatXpDistributor {

    private final BugemonService bugemonService;

    public CombatXpDistributor(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    /** XP formula: {@code 30 * floorLevel * multiplier * opponentCount}. */
    public int xpGain(final int floorLevel, final int multiplier, final int opponentCount) {
        return 30 * floorLevel * multiplier * opponentCount;
    }

    public void distributeXp(CombatContext combatCtx) {
        this.distributeXp(combatCtx.winner(), combatCtx.loser());
    }

    /** Convenience overload using floor=1 and multiplier=1 (placeholder values). */
    private void distributeXp(final Trainer winner, final Trainer loser) {
        final int floorLevel = 1; // Placeholder for actual floor level
        final int multiplier = 1; // Placeholder for actual multiplier based on combat conditions

        this.distributeXp(winner, loser, floorLevel, multiplier);
    }

    /**
     * Distributes XP equally among all participating Bugemons and returns the resulting level-ups.
     *
     * @return one {@link LevelUp} per level crossed per Bugemon
     */
    private void distributeXp(final Trainer winner, final Trainer loser, final int floor, final int multiplier) {
        final Set<Bugemon> participatingBugemon = winner.getParticipatingBugemons();

        final int nAdversaries = loser.getTeamSize();
        final int numParticipatingBugemon = participatingBugemon.size();

        final int xpWon = this.xpGain(floor, multiplier, nAdversaries);
        final int xpPerBugemon = (xpWon / numParticipatingBugemon);

        participatingBugemon.forEach(b -> this.bugemonService.distributeXp(b, xpPerBugemon));
    }
}
