package bugemon.server.services;

import java.util.List;
import java.util.Random;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.damage.Efficiency;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.player.exceptions.IllegalAttackReplacementException;
import bugemon.common.models.run.RunBugemon;
import bugemon.common.models.run.RunTeam;
import bugemon.common.models.tower.reward.AttackReward;
import bugemon.common.models.tower.reward.BonusStatsReward;
import bugemon.common.models.tower.reward.ItemReward;
import bugemon.common.models.tower.reward.Reward;
import bugemon.common.models.tower.reward.RewardGenerator;

/**
 * Service that manages the tower-room reward flow.
 *
 * <p>
 * Delegates reward generation to {@link bugemon.common.models.tower.reward.RewardGenerator} and coordinates the three reward types
 * with the appropriate model mutations:
 * <ul>
 * <li>{@link bugemon.common.models.tower.reward.ItemReward} — adds items to the player's inventory</li>
 * <li>{@link bugemon.common.models.tower.reward.BonusStatsReward} — applies a stat bonus to a
 * {@link bugemon.common.models.run.RunBugemon}</li>
 * <li>{@link bugemon.common.models.tower.reward.AttackReward} — replaces an attack on a selected Bugemon</li>
 * </ul>
 * All changes are persisted immediately via {@link BugemonService} and {@link InventoryService}.
 */
public class RewardService {
    private final RewardGenerator rewardGenerator;
    private final BugemonService bugemonService;
    private final InventoryService inventoryService;

    public RewardService(BugemonService bugemonService, InventoryService inventoryService, Random random) {
        this.rewardGenerator = new RewardGenerator(random);
        this.bugemonService = bugemonService;
        this.inventoryService = inventoryService;
    }

    public List<Reward> generateRewards(RunTeam runTeam) {
        return this.rewardGenerator.generate(this.bugemonService.getAttacks(), this.inventoryService.getItems(),
                runTeam);
    }

    public void applyItemReward(ItemReward reward, Inventory inventory) {
        inventory.addItem(reward.getItem(), reward.getQuantity());
        this.inventoryService.save(inventory);
    }

    public void applyStatBonusReward(BonusStatsReward reward, RunBugemon bugemon) {
        bugemon.applyBonus(reward.getBonus());
        this.bugemonService.savePlayerBugemon(bugemon.getPlayerBugemon());
    }

    public void applyAttackReward(AttackReward reward, RunBugemon bugemon, Attack toReplace) {
        this.applyAttackReward(reward, bugemon.getPlayerBugemon(), toReplace);
    }

    public void applyAttackReward(AttackReward reward, PlayerBugemon bugemon, Attack toReplace) {
        try {
            if (Efficiency.preview(reward.getAttack().type(), bugemon.getType()) == Efficiency.SUPER_EFFICIENT) {
                return;
            }
            bugemon.replaceAttack(toReplace, reward.getAttack());
            this.bugemonService.savePlayerBugemon(bugemon);
        } catch (IllegalAttackReplacementException e) {
            // do nothing
        }
    }
}
