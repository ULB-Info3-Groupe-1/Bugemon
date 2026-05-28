package bugemon.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bugemon.common.CombatSummary;
import bugemon.common.Configuration;
import bugemon.common.LevelUpResult;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.combat.Combat;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.combat.CombatResult;
import bugemon.common.models.combat.CombatTeam;
import bugemon.common.models.combat.damage.DamageCalculator;
import bugemon.common.models.combat.damage.Efficiency;
import bugemon.common.models.combat.factory.AutoCombatFactory;
import bugemon.common.models.combat.factory.CombatFactory;
import bugemon.common.models.combat.factory.ManualCombatFactory;
import bugemon.common.models.combat.utils.EffectProcessor;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.player.PlayerInputHandler;
import bugemon.common.models.run.RunBugemon;
import bugemon.common.models.skills.SkillContext;
import bugemon.common.models.team.factory.BossTeamFactory;
import bugemon.common.models.team.factory.RandomTeamFactory;
import bugemon.common.models.team.factory.TeamFactory;

/**
 * Handles combat lifecycle (XP finalisation, damage preview) and creates {@link CombatFactory} instances.
 */
public class CombatService {

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;
    private final Random random;

    public CombatService(Random random) {
        this(new DamageCalculator(), new EffectProcessor(), random);
    }

    public CombatService(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random) {
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
    }

    public TeamFactory createOpponentFactory(boolean isBoss) {
        return isBoss ? new BossTeamFactory(this.random) : new RandomTeamFactory(this.random);
    }

    public TeamFactory createBossOpponentFactory() {
        return new BossTeamFactory(this.random);
    }

    public CombatFactory createManualCombatFactory(Inventory defaultInventory, PlayerInputHandler handler,
            TeamFactory opponentFactory, int floor, boolean bossMode) {
        return new ManualCombatFactory(defaultInventory, this.damageCalculator, this.effectProcessor, this.random,
                handler, opponentFactory, floor, bossMode);
    }

    public CombatFactory createAutoCombatFactory(TeamFactory opponentFactory, int floor, boolean bossMode) {
        return new AutoCombatFactory(this.damageCalculator, this.effectProcessor, this.random, opponentFactory, floor,
                bossMode);
    }

    public CombatSummary finalizeCombat(Combat combat, SkillContext skillContext) {
        CombatTeam playerTeam = combat.getPlayerTeam();
        CombatResult result = combat.getResult();

        int totalXp = 0;
        List<LevelUpResult> levelUpResults = new ArrayList<>();

        if (result == CombatResult.VICTORY) {
            int opponentCount = combat.getOpponentTeamSize();
            int baseXp = this.computeCombatXp(combat.getFloor(), combat.isBossMode(), opponentCount);
            totalXp = (int) (baseXp * skillContext.getXpMultiplier());
            int xpPerBugemon = this.computeXpPerBugemon(totalXp, playerTeam.size());
            List<CombatBugemon> participants = playerTeam.getParticipants();
            for (CombatBugemon participant : participants) {
                RunBugemon runBugemon = participant.getRunBugemon();
                int numLevelPassed = runBugemon.addXp(xpPerBugemon);
                for (int i = 0; i < numLevelPassed; i++) {
                    levelUpResults.add(new LevelUpResult(runBugemon, runBugemon.getLevel() - numLevelPassed + i + 1));
                }
            }
        }

        playerTeam.syncToRunTeam();

        return new CombatSummary(result, levelUpResults);
    }

    public Efficiency previewEfficiency(Attack attack, CombatBugemon defender) {
        return this.previewEfficiency(attack, defender.getType());
    }

    public Efficiency previewEfficiency(Attack attack, ElementType typeDefender) {
        return this.damageCalculator.previewEfficiency(attack.type(), typeDefender);
    }

    private int computeCombatXp(int floorNumber, boolean isBoss, int opponentCount) {
        int typeMultiplier = isBoss ? Configuration.Game.BOSS_MULTIPLIER : Configuration.Game.NORMAL_MULTIPLIER;
        return Configuration.Game.BASE_XP * floorNumber * typeMultiplier * opponentCount;
    }

    private int computeXpPerBugemon(int totalXp, int participantCount) {
        if (participantCount <= 0) {
            return 0;
        }
        return totalXp / participantCount;
    }
}
