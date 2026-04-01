package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.*;

public class TestInventoryService {
    private Inventory expectedInventory;
    private Inventory inventory;
    private Item baieRevigorante;
    private Item baieTonique;
    private Item gelDefensif;
    private Item serumOffensif;

    @Before
    public void setUp() {
        expectedInventory = new Inventory();
        inventory = new Inventory();

        baieRevigorante = new Item("baie_revigorante", "Baie Revigorante",
                "Restaure 20 PV au Bugémon actif.", Item.ItemType.HEALING,
                new EffectHeal(EffectTarget.THROWER, 20));
        baieTonique = new Item("baie_tonique", "Baie Tonique", "Restaure 10 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
        gelDefensif = new Item("gel_defensif", "Gel Defensif",
                "Renforce temporairement la defense du Bugémon actif.", Item.ItemType.BOOST,
                new EffectStatModifier(EffectTarget.THROWER, EffectStat.DEFENSE, 10,
                        EffectDuration.PERMANENT));
        serumOffensif = new Item("serum_offensif", "Serum Offensif",
                "Renforce temporairement l'attaque du Bugémon actif.", Item.ItemType.BOOST,
                new EffectStatModifier(EffectTarget.THROWER, EffectStat.ATTACK, 10,
                        EffectDuration.PERMANENT));

        expectedInventory.addItem(baieRevigorante, 3);
        expectedInventory.addItem(baieTonique, 2);
        expectedInventory.addItem(gelDefensif, 1);
        expectedInventory.addItem(serumOffensif, 1);
    }

    @Test
    public void testAddStarterItem() {
        InventoryService.addStarterItem(inventory);
        assertEquals(expectedInventory.getMap(), inventory.getMap());

        for (Item item : expectedInventory.getMap().keySet()) {
            assertTrue(inventory.hasItem(item));
            assertEquals((int) expectedInventory.getMap().get(item),
                    (int) inventory.getMap().get(item));
        }
    }
}
