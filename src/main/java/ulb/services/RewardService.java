package ulb.services;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
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
            // TODO: Should throw if the bugemon is weak against the new attack's type (not
            // doing it now)
            bugemon.replaceAttack(toReplace, reward.getAttack());
            this.bugemonService.savePlayerBugemon(bugemon);
        } catch (IllegalAttackReplacementException e) {
            // do nothing
        }
    }
}
