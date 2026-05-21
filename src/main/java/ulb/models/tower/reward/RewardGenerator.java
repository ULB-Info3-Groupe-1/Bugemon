package ulb.models.tower.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.damage.Efficiency;
import ulb.models.item.Item;
import ulb.models.player.BonusStatsGenerator;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;

public class RewardGenerator {
    private final Random random;
    private final BonusStatsGenerator bonusStatsGenerator;

    public RewardGenerator(Random random) {
        this.random = random;
        this.bonusStatsGenerator = new BonusStatsGenerator(random);
    }

    public List<Reward> generate(List<Attack> availableAttacks, List<Item> availableItems, RunTeam runTeam) {
        List<Reward> rewards = new ArrayList<>();
        rewards.add(this.generateItemReward(availableItems));
        rewards.add(this.generateAttackReward(availableAttacks, runTeam));
        rewards.add(this.generateStatBonusReward());
        return rewards;
    }

    private ItemReward generateItemReward(List<Item> availableItems) {
        Item item = availableItems.get(this.random.nextInt(availableItems.size()));
        return new ItemReward(item, Configuration.Game.ITEM_QUANTITY_FOR_ITEM_REWARD);
    }

    private Reward generateAttackReward(List<Attack> availableAttacks, RunTeam runTeam) {
        Set<ElementType> teamTypes = runTeam.getMembers().stream().map(RunBugemon::getType).collect(Collectors.toSet());

        List<Attack> compatible = availableAttacks.stream().filter(attack -> teamTypes.stream().anyMatch(
                // keep only attacks such that there exists a bugemon in the team that is at
                // least "normal efficiency" against the type of the attack
                teamType -> Efficiency.preview(teamType, attack.type()).isAtLeastNormal()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (compatible.isEmpty()) {
            compatible = new ArrayList<>(availableAttacks);
        }

        Attack attack = compatible.get(this.random.nextInt(compatible.size()));
        return new AttackReward(attack);
    }

    private BonusStatsReward generateStatBonusReward() {
        return new BonusStatsReward(this.bonusStatsGenerator.generateBonusStats());
    }
}
