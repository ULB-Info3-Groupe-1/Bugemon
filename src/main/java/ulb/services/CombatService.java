package ulb.services;

import java.util.Random;

import ulb.models.combat.Combat;
import ulb.models.combat.CombatResult;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.strategy.PlayerStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.run.RunTeam;

/**
 * Stateless utility for combat calculations: attack priority, damage formula.
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

    /**
     * Creates a combat.
     *
     * At the end of the combat, HPs are restored and XP is distributed.
     */
    public Combat createCombat(RunTeam playerRunTeam, CombatTeam opponentTeam, Inventory playerInventory,
            Inventory opponentInventory, PlayerInputHandler handler, int floor, boolean bossMode) {
        CombatTeam playerCombatTeam = CombatTeam.fromRunTeam(playerRunTeam);
        CombatStrategy playerStrategy = new PlayerStrategy(handler);
        CombatStrategy opponentStrategy = new AutoStrategy(this.random);
        return new Combat(playerCombatTeam, opponentTeam, floor, bossMode, playerInventory, opponentInventory,
                playerStrategy, opponentStrategy, this.damageCalculator, this.effectProcessor);
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
