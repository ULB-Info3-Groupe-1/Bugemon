package ulb.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.reward.AttackReward;
import ulb.models.reward.ItemReward;
import ulb.models.reward.Reward;
import ulb.models.reward.StatReward;
import ulb.repositories.InventoryRepository;
import ulb.repositories.StaticDataRepository;
import ulb.utils.test.TestUtilsBugemons;

class TestRewardService {

    private static final String PLAYERNAME = "default_player";
    private StaticDataRepository staticDataRepository;
    private InventoryRepository inventoryRepository;
    private RewardService rewardService;

    @Before
    public void setUp() {
        this.staticDataRepository = mock(StaticDataRepository.class);
        this.inventoryRepository = mock(InventoryRepository.class);

        this.rewardService = new RewardService(this.staticDataRepository, this.inventoryRepository, PLAYERNAME);
    }

    @Test
    void testRewardsAreCorrectlyGenerated() {
        List<Reward> options = this.rewardService.generateRewards();

        Assertions.assertEquals(3, options.size(), "We should have exactly 3 reward options");
        assertTrue(options.stream().anyMatch(r -> r instanceof StatReward), "StatReward Missing");
        assertTrue(options.stream().anyMatch(r -> r instanceof AttackReward), "AttackReward Missing");
        assertTrue(options.stream().anyMatch(r -> r instanceof ItemReward), "Manque ItemReward Missing");
    }

    @Test
    void testStatRewardIsNotEmpty() {
        List<Reward> options = this.rewardService.generateRewards();
        StatReward statReward = (StatReward) options.stream().filter(r -> r instanceof StatReward).findFirst()
                .orElseThrow();

        assertTrue(statReward.getSummary().contains("+"), "Stat bonus should display a +");
    }

    @Test
    void testItemRewardIsAddedToInventory() {
        Inventory fakeInventory = new Inventory();
        Item fakeItem = new Item("item-01", "Potion", "Soin", Item.ItemType.HEALING, null);
        ItemReward itemReward = new ItemReward(fakeItem, fakeInventory);
        itemReward.applyReward(null);

        assertTrue(fakeInventory.hasItem(fakeItem), "Not in inventory!");
    }

    @Test
    void testAddAttackToBugemon() {
        Bugemon fakeBugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        Attack fakeAttack = new Attack("atk-01", "TestAttack", BugemonType.FLORA, "Description", 50, null);

        AttackReward attackReward = new AttackReward(fakeAttack);

        attackReward.setIndexToReplace(0);

        attackReward.applyReward(fakeBugemon);

        assertTrue(fakeBugemon.getAttackList().contains(fakeAttack), "Bugemon should have learned the new attack");

    }
}
