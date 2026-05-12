package ulb.models.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.level_up.Upgrade;

public class PostCombatReward {

    private Random random;
    private List<Reward> options;
    private Inventory inventory;
    private final List<Attack> availableAttacks;
    private final List<Item> availableItems;
    private static final int MAX_HP_BONUS = 10;
    private static final int MAX_STAT_BONUS = 3;

    public PostCombatReward(Inventory inventory, List<Attack> availableAttacks, List<Item> availableItems) {
        this.inventory = inventory;
        this.availableAttacks = availableAttacks;
        this.availableItems = availableItems;
        this.random = new Random();
        this.generateRewards();
    }

    public void generateRewards() {
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
        this.options.add(new ItemReward(this.availableItems.get(randomItemIndex), this.inventory));

        java.util.Collections.shuffle(this.options);
    }

}
