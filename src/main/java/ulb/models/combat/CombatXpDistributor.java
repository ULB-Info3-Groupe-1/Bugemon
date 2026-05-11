package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.level_up.LevelUp;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.XpMultiplierEffect;
import ulb.models.trainer.Trainer;

public class CombatXpDistributor {

    /** XP formula: {@code 30 * floorLevel * multiplier * opponentCount}. */
    public int xpGain(final int floorLevel, final int multiplier, final int opponentCount) {
        return 30 * floorLevel * multiplier * opponentCount;
    }

    public List<LevelUp> distributeXp(CombatContext combatCtx, List<Skill> skills) {
        return this.distributeXp(combatCtx.winner(), combatCtx.loser(), skills);
    }

    /** Convenience overload using floor=1 and multiplier=1 (placeholder values). */
    private List<LevelUp> distributeXp(final Trainer winner, final Trainer loser, List<Skill> skills) {
        final int floorLevel = 1; // Placeholder for actual floor level
        final int multiplier = 1; // Placeholder for actual multiplier based on combat conditions

        return this.distributeXp(winner, loser, floorLevel, multiplier, skills);
    }

    /**
     * Distributes XP equally among all participating Bugemons and returns the resulting level-ups.
     *
     * @return one {@link LevelUp} per level crossed per Bugemon
     */
    private List<LevelUp> distributeXp(final Trainer winner, final Trainer loser, final int floor, final int multiplier,
            List<Skill> skills) {
        final Set<Bugemon> participatingBugemon = winner.getParticipatingBugemons();

        final int nAdversaries = loser.getTeamSize();
        final int numParticipatingBugemon = participatingBugemon.size();

        final int xpWon = this.xpGain(floor, multiplier, nAdversaries);
        final int xpPerBugemon = (xpWon / numParticipatingBugemon);

        List<LevelUp> generatedLevelUps = new ArrayList<>();
        for (Bugemon b : participatingBugemon) {
            generatedLevelUps.addAll(this.distributeXp(b, xpPerBugemon, skills));
        }

        return generatedLevelUps;
    }

    private List<LevelUp> distributeXp(Bugemon bugemon, int amount, List<Skill> skills) {
        double xpMultiplier = this.computeXpMultiplier(skills);
        int finalXp = (int) Math.round(amount * xpMultiplier);
        int numLevelUps = bugemon.gainXp(finalXp);

        if (numLevelUps > 0) {
            bugemon.restoreHp();
        }

        List<LevelUp> generateLevelUps = new ArrayList<>();
        for (int i = 0; i < numLevelUps; i++) {
            generateLevelUps.add(new LevelUp(bugemon));
        }

        return generateLevelUps;
    }

    /**
     * Multiplicative composition of every unlocked {@link XpMultiplierEffect} skill — returns 1.0 when no such skill is
     * unlocked.
     */
    private double computeXpMultiplier(List<Skill> skills) {
        double multiplier = 1.0;
        for (Skill skill : skills) {
            multiplier *= ((XpMultiplierEffect) skill.getEffect()).multiplier();
        }
        return multiplier;
    }
}
