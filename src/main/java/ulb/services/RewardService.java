package ulb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.level_up.Upgrade;
import ulb.models.reward.AttackReward;
import ulb.models.reward.ItemReward;
import ulb.models.reward.Reward;
import ulb.models.reward.StatReward;

public class RewardService {

    private static final int MAX_HP_BONUS = 10;
    private static final int MAX_STAT_BONUS = 5;
    private final Random random;

    public RewardService() {
        this.random = new Random();
    }

    public List<Reward> generateRewards(Inventory inventory, List<Attack> availableAttacks, List<Item> availableItems) {
        List<Reward> options = new ArrayList<>();

        int[] stats = new int[4];
        int statChoice = this.random.nextInt(4);

        stats[statChoice] = (statChoice == 0) ? this.random.nextInt(MAX_HP_BONUS) + 1
                : this.random.nextInt(MAX_STAT_BONUS) + 1;

        Upgrade randomUpgrade = new Upgrade(stats[0], stats[1], stats[2], stats[3]);
        options.add(new StatReward(randomUpgrade));

        int randomAttackIndex = this.random.nextInt(availableAttacks.size());
        options.add(new AttackReward(availableAttacks.get(randomAttackIndex)));

        int randomItemIndex = this.random.nextInt(availableItems.size());
        options.add(new ItemReward(availableItems.get(randomItemIndex), inventory));

        Collections.shuffle(options);

        return options;
    }
}
