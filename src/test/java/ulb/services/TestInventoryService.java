package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;

public class TestInventoryService {
    private Inventory expectedInventory;
    private Item baieRevigorante;
    private Item baieTonique;
    private Item gelDefensif;
    private Item serumOffensif;

    @Before
    public void setUp() {
        this.expectedInventory = new Inventory();

        this.baieRevigorante = new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));
        this.baieTonique = new Item("baie_tonique", "Baie Tonique", "Restaure 10 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
        this.gelDefensif = new Item("gel_defensif", "Gel Defensif",
                "Renforce temporairement la defense du Bugémon actif.", Item.ItemType.BOOST,
                new EffectStatModifier(EffectTarget.THROWER, EffectStat.DEFENSE, 10, EffectDuration.PERMANENT));
        this.serumOffensif = new Item("serum_offensif", "Serum Offensif",
                "Renforce temporairement l'attaque du Bugémon actif.", Item.ItemType.BOOST,
                new EffectStatModifier(EffectTarget.THROWER, EffectStat.ATTACK, 10, EffectDuration.PERMANENT));

        this.expectedInventory.addItem(this.baieRevigorante, 3);
        this.expectedInventory.addItem(this.baieTonique, 2);
        this.expectedInventory.addItem(this.gelDefensif, 1);
        this.expectedInventory.addItem(this.serumOffensif, 1);
    }

    @Test
    public void testAddStarterItem() {
        InventoryService inventoryService = new InventoryService();
        assertEquals(this.expectedInventory.getMap(), inventoryService.getInventory().getMap());

        for (Item item : this.expectedInventory.getMap().keySet()) {
            assertTrue(inventoryService.getInventory().hasItem(item));
            assertEquals((int) this.expectedInventory.getMap().get(item),
                    (int) inventoryService.getInventory().getMap().get(item));
        }
    }
}
