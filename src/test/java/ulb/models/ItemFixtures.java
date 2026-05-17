package ulb.models;

import ulb.common.EffectTarget;
import ulb.models.effect.HealEffect;
import ulb.models.item.Item;
import ulb.models.item.ItemType;

public final class ItemFixtures {

    private ItemFixtures() {
    }

    public static Item healingItem(int amount) {
        String itemName = "healing Item";
        return new Item(itemName, itemName, "", ItemType.HEALING, new HealEffect(EffectTarget.THROWER, amount));
    }
}
