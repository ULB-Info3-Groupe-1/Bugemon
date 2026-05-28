package bugemon.common.models.tower.reward;

import bugemon.common.models.item.Item;

/**
 * A {@link Reward} that adds a quantity of a specific {@link bugemon.common.models.item.Item} to the player's inventory.
 */
public class ItemReward implements Reward {
    private final Item item;
    private final int quantity;

    public ItemReward(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
