package ulb.models.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Item;
import ulb.models.level_up.Upgrade;

public class PostCombatReward {

    private Random random;
    private final List<Attack> availableAttacks;
    private final List<Item> availableItems;
    private static final int MAX_HP_BONUS = 10;
    private static final int MAX_STAT_BONUS = 3;

    public PostCombatReward(List<Attack> availableAttacks, List<Item> availableItems) {
        this.availableAttacks = availableAttacks;
        this.availableItems = availableItems;
        this.random = new Random();
        this.generateRewards();
    }

    public void generateRewards() {
        List<Reward> options = new ArrayList<>();

        int[] stats = new int[4];
        int statChoice = this.random.nextInt(4);

        stats[statChoice] = (statChoice == 0) ? this.random.nextInt(MAX_HP_BONUS) + 1
                : this.random.nextInt(MAX_STAT_BONUS) + 1;

        Upgrade randomUpgrade = new Upgrade(stats[0], stats[1], stats[2], stats[3]);
        options.add(new StatReward(randomUpgrade));

        int randomAttackIndex = this.random.nextInt(this.availableAttacks.size());
        options.add(new AttackReward(this.availableAttacks.get(randomAttackIndex)));

        int randomItemIndex = this.random.nextInt(this.availableItems.size());
        options.add(new ItemReward(this.availableItems.get(randomItemIndex)));

        java.util.Collections.shuffle(options);
    }

}
