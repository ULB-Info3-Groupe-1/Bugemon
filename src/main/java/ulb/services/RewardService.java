package ulb.services;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.item.Inventory;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.tower.reward.AttackReward;
import ulb.models.tower.reward.BonusStatsReward;
import ulb.models.tower.reward.ItemReward;
import ulb.models.tower.reward.Reward;
import ulb.models.tower.reward.RewardGenerator;
import ulb.repositories.InventoryRepository;
import ulb.repositories.StaticRepository;

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

    public void applyAttackReward(AttackReward reward, RunBugemon bugemon, Attack toReplace) {
        bugemon.replaceAttack(toReplace, reward.getAttack());
    }
}
