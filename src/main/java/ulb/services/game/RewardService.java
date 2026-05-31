package ulb.services.game;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.combat.damage.Efficiency;
import ulb.models.item.Inventory;
import ulb.models.player.PlayerBugemon;
import ulb.models.player.exceptions.IllegalAttackReplacementException;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.tower.reward.AttackReward;
import ulb.models.tower.reward.BonusStatsReward;
import ulb.models.tower.reward.ItemReward;
import ulb.models.tower.reward.Reward;
import ulb.models.tower.reward.RewardGenerator;

/**
 * Service that manages the tower-room reward flow.
 *
 * <p>
 * Delegates reward generation to {@link ulb.models.tower.reward.RewardGenerator} and coordinates the three reward types
 * with the appropriate model mutations:
 * <ul>
 * <li>{@link ulb.models.tower.reward.ItemReward} — adds items to the player's inventory</li>
 * <li>{@link ulb.models.tower.reward.BonusStatsReward} — applies a stat bonus to a
 * {@link ulb.models.run.RunBugemon}</li>
 * <li>{@link ulb.models.tower.reward.AttackReward} — replaces an attack on a selected Bugemon</li>
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
