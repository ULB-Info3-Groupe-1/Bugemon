package ulb.models.reward;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ulb.models.bugemon.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class TestPostCombatReward {

    private Inventory inventory;
    private List<Attack> attacks;
    private List<Item> items;


    @Test
    void testRewardsAreCorrectlyGenerated() {
        this.inventory = new Inventory();
        this.attacks = List.of(new Attack("atk-01", "TestAttack", BugemonType.FLORA, "Description", 50, null));
        this.items = List.of(new Item("item-01", "soin", "Restaure des PV", Item.ItemType.HEALING,null));

        PostCombatReward pcr = new PostCombatReward(inventory, attacks, items);
        List<Reward> options = pcr.getOptions();

        Assertions.assertEquals(3, options.size(), "On devrait avoir exactement 3 récompenses");
        assertTrue(options.stream().anyMatch(r -> r instanceof StatReward), "StatReward Missing");
        assertTrue(options.stream().anyMatch(r -> r instanceof AttackReward), "AttackReward Missing");
        assertTrue(options.stream().anyMatch(r -> r instanceof ItemReward), "Manque ItemReward Missing");
    }

    @Test
    void testStatRewardIsNotEmpty() {
        this.inventory = new Inventory();
        this.attacks = List.of(new Attack("atk", "TestAttack", BugemonType.FLORA, "", 30, new ArrayList<>()));
        this.items = List.of(new Item("item-01", "soin", "Restaure des PV", Item.ItemType.HEALING,null));

        PostCombatReward pcr = new PostCombatReward(inventory, attacks, items);

        StatReward statReward = (StatReward) pcr.getOptions().stream()
                .filter(r -> r instanceof StatReward)
                .findFirst()
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
}




