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
import ulb.repositories.InventoryRepository;
import ulb.repositories.StaticRepository;

// TODO: update services states

public class RewardService {
    private final RewardGenerator rewardGenerator;
    private final StaticRepository staticRepository;
    private final InventoryRepository inventoryRepository;

    public RewardService(StaticRepository staticRepository, InventoryRepository inventoryRepository, Random random) {
        this.rewardGenerator = new RewardGenerator(random);
        this.staticRepository = staticRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Reward> generateRewards(RunTeam runTeam) {
        List<Reward> rewards = this.rewardGenerator.generate(this.staticRepository.attacks(),
                this.staticRepository.items(), runTeam);
        return rewards;
    }

    public void applyItemReward(ItemReward reward, Inventory inventory) {
        inventory.addItem(reward.getItem(), reward.getQuantity());
    }

    public void applyStatBonusReward(BonusStatsReward reward, RunBugemon bugemon) {
        bugemon.applyUpgrade(reward.getBonus());
    }

    private void applyAttackReward(AttackReward reward, RunBugemon bugemon, Attack toReplace) { 
        this.applyAttackReward(reward, bugemon.getPlayerBugemon(), toReplace);
    }

    private void applyAttackReward(AttackReward reward, PlayerBugemon bugemon, Attack toReplace) {
        try {
            bugemon.replaceAttack(toReplace, reward.getAttack());
        } catch (IllegalAttackReplacementException e) {
            // do nothing
        }
    }
}
