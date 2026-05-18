package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Item;
import ulb.models.level_up.Upgrade;
import ulb.models.reward.AttackReward;
import ulb.models.reward.ItemReward;
import ulb.models.reward.Reward;
import ulb.models.reward.StatReward;
import ulb.repositories.StaticDataRepository;

public class RewardService {

    // constants
    private static final int MAX_HP_BONUS = 10;
    private static final int MAX_STAT_BONUS = 5;

    private final StaticDataRepository staticDatarepository;
    private Random random;
    private List<Reward> options;
    private final List<Attack> availableAttacks;
    private final List<Item> availableItems;

    public RewardService(StaticDataRepository staticDataRepository) {
        this.staticDatarepository = staticDataRepository;

        this.availableAttacks = new ArrayList<>(this.staticDatarepository.getAllAttacks().values());
        this.availableItems = new ArrayList<>(this.staticDatarepository.getAllItems());
        this.random = new Random();
    }

    public List<Reward> generateRewards() {
        this.options = new ArrayList<>();

        int[] stats = new int[4];
        int statChoice = this.random.nextInt(4);

        stats[statChoice] = (statChoice == 0) ? this.random.nextInt(MAX_HP_BONUS) + 1
                : this.random.nextInt(MAX_STAT_BONUS) + 1;

        Upgrade randomUpgrade = new Upgrade(stats[0], stats[1], stats[2], stats[3]);
        this.options.add(new StatReward(randomUpgrade));

        int randomAttackIndex = this.random.nextInt(this.availableAttacks.size());
        this.options.add(new AttackReward(this.availableAttacks.get(randomAttackIndex)));

        int randomItemIndex = this.random.nextInt(this.availableItems.size());
        this.options.add(new ItemReward(this.availableItems.get(randomItemIndex)));

        java.util.Collections.shuffle(this.options);

        return this.options;
    }

    public List<Reward> getCurrentRewardOptions() {
        return this.options;
    }
}
