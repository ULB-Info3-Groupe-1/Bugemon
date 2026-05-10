package ulb.models.reward;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;

public class ItemReward extends Reward {

    private final Item item;
    private final Inventory inventory;

    public ItemReward(Item item, Inventory inventory) {
        super(RewardType.ITEM);
        this.item = item;
        this.inventory = inventory;
    }

    @Override
    public String getSummary() {
        return "Bonus : Nouvel objet : " + this.item.name();
    }

    @Override
    public void applyReward(Bugemon target) {
        this.inventory.addItem(this.item, 1);
    }
}
