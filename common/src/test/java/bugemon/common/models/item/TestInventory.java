package bugemon.common.models.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.models.effect.Effect;

public class TestInventory {

    private Inventory inventory;
    private Item item1;
    private Item item2;

    @Before
    public void setUp() {
        this.inventory = new Inventory();
        this.item1 = new Item("item1", "Potion", "Heals", ItemType.HEALING, mock(Effect.class));
        this.item2 = new Item("item2", "Boost", "Buffs", ItemType.BOOST, mock(Effect.class));
    }

    @Test
    public void shouldReturnFalseForHasItem_whenInventoryIsEmpty() {
        assertFalse(this.inventory.hasItem(this.item1));
    }

    @Test
    public void shouldReturnTrueForHasItem_whenItemIsAdded() {
        this.inventory.addItem(this.item1, 1);
        assertTrue(this.inventory.hasItem(this.item1));
    }

    @Test
    public void shouldIncreaseQuantity_whenAddingExistingItem() {
        this.inventory.addItem(this.item1, 1);
        this.inventory.addItem(this.item1, 2);

        Map<Item, Integer> map = this.inventory.getMap();
        assertEquals(Integer.valueOf(3), map.get(this.item1));
    }

    @Test
    public void shouldReturnEmpty_whenUseItemIsCalledAndItemNotPresent() {
        Optional<Item> usedItem = this.inventory.useItem(this.item1);
        assertFalse(usedItem.isPresent());
    }

    @Test
    public void shouldDecrementQuantity_whenUseItemIsCalledAndMultipleExist() {
        this.inventory.addItem(this.item1, 2);
        Optional<Item> usedItem = this.inventory.useItem(this.item1);

        assertTrue(usedItem.isPresent());
        assertEquals(this.item1, usedItem.get());

        Map<Item, Integer> map = this.inventory.getMap();
        assertEquals(Integer.valueOf(1), map.get(this.item1));
        assertTrue(this.inventory.hasItem(this.item1));
    }

    @Test
    public void shouldRemoveItem_whenUseItemIsCalledAndOnlyOneExists() {
        this.inventory.addItem(this.item1, 1);
        Optional<Item> usedItem = this.inventory.useItem(this.item1);

        assertTrue(usedItem.isPresent());
        assertFalse(this.inventory.hasItem(this.item1));
        assertFalse(this.inventory.getMap().containsKey(this.item1));
    }

    @Test
    public void shouldReturnItem_whenGetItemIsCalledWithValidId() {
        this.inventory.addItem(this.item1, 1);
        Item retrieved = this.inventory.getItem("item1");
        assertEquals(this.item1, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException_whenGetItemIsCalledWithInvalidId() {
        this.inventory.getItem("invalid_id");
    }

    @Test
    public void shouldReturnIndependentMap_whenGetMapIsCalled() {
        this.inventory.addItem(this.item1, 1);
        Map<Item, Integer> map = this.inventory.getMap();

        map.put(this.item2, 5);

        assertFalse(this.inventory.hasItem(this.item2));
    }
}
