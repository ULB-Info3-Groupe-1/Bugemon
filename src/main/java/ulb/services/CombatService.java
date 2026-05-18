package ulb.services;

import java.util.Random;

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
import ulb.models.player.PlayerInputHandler;

/**
 * Handles combat lifecycle (XP finalisation, damage preview) and creates {@link CombatFactory} instances.
 */
public class CombatService {

    private static final int BASE_XP = 30;
    private static final int BOSS_MULTIPLIER = 2;
    private static final int NORMAL_MULTIPLIER = 1;

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;
    private final Random random;

    public CombatService(DamageCalculator damageCalculator, EffectProcessor effectProcessor, Random random) {
        this.damageCalculator = damageCalculator;
        this.effectProcessor = effectProcessor;
        this.random = random;
    }

    public CombatFactory createManualCombatFactory(PlayerInputHandler handler, int floor, boolean bossMode) {
        return new ManualCombatFactory(this.damageCalculator, this.effectProcessor, this.random, handler, floor,
                bossMode);
    }

    public CombatFactory createAutoCombatFactory(int floor, boolean bossMode) {
        return new AutoCombatFactory(this.damageCalculator, this.effectProcessor, this.random, floor, bossMode);
    }

    public void finalizeCombat(Combat combat) {
        CombatTeam playerTeam = combat.getPlayerTeam();

        if (combat.getResult() == CombatResult.VICTORY) {
            int opponentCount = combat.getOpponentTeamSize();
            int totalXp = this.computeCombatXp(combat.getFloor(), combat.isBossMode(), opponentCount);
            int xpPerBugemon = this.computeXpPerBugemon(totalXp, playerTeam.size());
            playerTeam.getParticipants().forEach(participant -> participant.addXp(xpPerBugemon));
        }
    }

    public Efficiency previewEfficiency(Attack attack, CombatBugemon defender) {
        return this.damageCalculator.previewEfficiency(attack.type(), defender.getType());
    }

    // TODO: move it
    private int computeCombatXp(int floorNumber, boolean isBoss, int opponentCount) {
        int typeMultiplier = isBoss ? BOSS_MULTIPLIER : NORMAL_MULTIPLIER;
        return BASE_XP * floorNumber * typeMultiplier * opponentCount;
    }

    private int computeXpPerBugemon(int totalXp, int participantCount) {
        if (participantCount <= 0) {
            return 0;
        }
        return totalXp / participantCount;
    }
}
