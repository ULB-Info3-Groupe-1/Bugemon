package bugemon.common.models.item;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import bugemon.common.models.effect.Effect;

public class TestItem {

    @Test
    public void shouldConstructItem_whenValidParametersAreProvided() {
        Effect mockEffect = mock(Effect.class);
        Item item = new Item("id1", "Potion", "Heals 20 HP", ItemType.HEALING, mockEffect);

        assertEquals("id1", item.id());
        assertEquals("Potion", item.name());
        assertEquals("Heals 20 HP", item.description());
        assertEquals(ItemType.HEALING, item.type());
        assertEquals(mockEffect, item.effect());
    }
}
