package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.Configuration;
import ulb.common.CombatSummary;
import ulb.common.LevelUpResult;
import ulb.models.bugemon.Attack;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatResult;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.damage.Efficiency;
import ulb.models.combat.factory.AutoCombatFactory;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.combat.factory.ManualCombatFactory;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.player.PlayerInputHandler;
import ulb.models.run.RunBugemon;
import ulb.models.skills.SkillContext;
import ulb.models.team.factory.RandomTeamFactory;
import ulb.models.team.factory.TeamFactory;

/**
 * Handles combat lifecycle (XP finalisation, damage preview) and creates
 * {@link CombatFactory} instances.
 */
public class CombatService {

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;
    private final Random random;
    private final BugemonService bugemonService;

    public CombatService(Random random, BugemonService bugemonService) {
        this(new DamageCalculator(), new EffectProcessor(), random, bugemonService);
    }

    public CombatService(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random,
            BugemonService bugemonService) {
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
        this.bugemonService = bugemonService;
    }

    public TeamFactory createRandomOpponentFactory() {
        return new RandomTeamFactory(this.random);
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
        return this.damageCalculator.previewEfficiency(attack.type(), defender.getType());
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
