package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;

public class TestInventory {
    private Inventory inventory;
    private Item baieRevigorante;
    private Item baieTonique;

    @Before
    public void setUp() {
        inventory = new Inventory();

        baieRevigorante =
                new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                         Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));
        baieTonique = new Item("baie_tonique", "Baie Tonique", "Restaure 10 PV au Bugémon actif.",
                               Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
    }

    @Test
    public void testAddItemQuantity() {
        inventory.addItem(baieRevigorante, 3);

        assertEquals((int)inventory.getItems().get(baieRevigorante), (int)3);

        inventory.addItem(baieRevigorante, 4);

        assertEquals((int)inventory.getItems().get(baieRevigorante), (int)7);
    }

    @Test
    public void testUseItem() {
        inventory.addItem(baieRevigorante, 2);
        inventory.useItem(baieRevigorante);
        assertEquals((int)inventory.getItems().get(baieRevigorante), (int)1);
    }

    @Test
    public void testUseItemNotInInventory() {
        inventory.addItem(baieRevigorante, 2);
        assertThrows(IllegalStateException.class, () -> inventory.useItem(baieTonique));
    }

    @Test
    public void testHasItem() {
        inventory.addItem(baieRevigorante, 2);
        assertTrue(inventory.hasItem(baieRevigorante));
        assertFalse(inventory.hasItem(baieTonique));
    }
}
