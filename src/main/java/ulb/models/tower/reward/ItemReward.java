package ulb.models.tower.reward;

import ulb.models.item.Item;

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
