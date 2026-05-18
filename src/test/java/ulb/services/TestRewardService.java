package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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

public class TestRewardService {

    private StaticDataRepository staticDataRepository;
    private RewardService rewardService;

    @Before
    public void setUp() {
        this.staticDataRepository = mock(StaticDataRepository.class);

        when(this.staticDataRepository.getAllItems())
                .thenReturn(List.of(new Item("item-01", "Potion", "Soin", Item.ItemType.HEALING, null)));
        when(this.staticDataRepository.getAllAttacks()).thenReturn(
                Map.of("atk-01", new Attack("atk-01", "TestAttack", BugemonType.FLORA, "Description", 50, null)));

        this.rewardService = new RewardService(this.staticDataRepository);
    }

    @Test
    public void testRewardsAreCorrectlyGenerated() {
        List<Reward> options = this.rewardService.generateRewards();

        assertEquals("We should have exactly 3 reward options", 3, options.size());
        assertTrue("StatReward Missing", options.stream().anyMatch(r -> r instanceof StatReward));
        assertTrue("AttackReward Missing", options.stream().anyMatch(r -> r instanceof AttackReward));
        assertTrue("Manque ItemReward Missing", options.stream().anyMatch(r -> r instanceof ItemReward));
    }

    @Test
    public void testStatRewardIsNotEmpty() {
        List<Reward> options = this.rewardService.generateRewards();
        StatReward statReward = (StatReward) options.stream().filter(r -> r instanceof StatReward).findFirst()
                .orElseThrow();

        assertTrue("Stat bonus should display a +", statReward.getSummary().contains("+"));
    }

    @Test
    public void testItemRewardIsAddedToInventory() {
        Inventory fakeInventory = new Inventory();
        InventoryRepository repo = mock(InventoryRepository.class);
        when(repo.getPlayerInventory("Player1")).thenReturn(fakeInventory);

        InventoryService.init("Player1", repo, mock(SkillService.class));

        Item fakeItem = new Item("item-01", "Potion", "Soin", Item.ItemType.HEALING, null);
        ItemReward itemReward = new ItemReward(fakeItem);
        itemReward.applyReward(null);

        assertTrue("Not in inventory!", fakeInventory.hasItem(fakeItem));
    }

    @Test
    public void testAddAttackToBugemon() {
        Bugemon fakeBugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        Attack fakeAttack = new Attack("atk-01", "TestAttack", BugemonType.FLORA, "Description", 50, null);

        AttackReward attackReward = new AttackReward(fakeAttack);

        attackReward.setIndexToReplace(0);

        attackReward.applyReward(fakeBugemon);

        assertTrue("Bugemon should have learned the new attack", fakeBugemon.getAttackList().contains(fakeAttack));

    }
}
