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
        this.inventory = new Inventory();

        this.baieRevigorante = new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));
        this.baieTonique = new Item("baie_tonique", "Baie Tonique", "Restaure 10 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
    }

    @Test
    public void testAddItemQuantity() {
        this.inventory.addItem(this.baieRevigorante, 3);

        assertEquals((int) this.inventory.getMap().get(this.baieRevigorante), (int) 3);

        this.inventory.addItem(this.baieRevigorante, 4);

        assertEquals((int) this.inventory.getMap().get(this.baieRevigorante), (int) 7);
    }

    @Test
    public void testUseItem() {
        this.inventory.addItem(this.baieRevigorante, 2);
        this.inventory.useItem(this.baieRevigorante);
        assertEquals((int) this.inventory.getMap().get(this.baieRevigorante), (int) 1);
    }

    @Test
    public void testUseItemNotInInventory() {
        this.inventory.addItem(this.baieRevigorante, 2);
        assertThrows(IllegalStateException.class, () -> this.inventory.useItem(this.baieTonique));
    }

    @Test
    public void testHasItem() {
        this.inventory.addItem(this.baieRevigorante, 2);
        assertTrue(this.inventory.hasItem(this.baieRevigorante));
        assertFalse(this.inventory.hasItem(this.baieTonique));
    }
}
