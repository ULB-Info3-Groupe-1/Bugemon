package ulb.models.reward;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.services.InventoryService;

public class ItemReward extends Reward {

    private final Item item;

    public ItemReward(Item item) {
        super(RewardType.ITEM);
        this.item = item;
    }

    @Override
    public String getSummary() {
        return "Bonus : Nouvel objet : " + this.item.name();
    }

    @Override
    public void applyReward(Bugemon target) {
        Inventory inventory = InventoryService.getInstance().loadInventory();
        inventory.addItem(this.item, 1);
        InventoryService.getInstance().saveInventory(inventory);
    }
}
