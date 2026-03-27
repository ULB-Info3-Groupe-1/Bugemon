package ulb.services;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.*;

public class InventoryService {
    private InventoryService() {
        // Private constructor to prevent instantiation
    }

    private static Item baieRevigorante =
            new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                     Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));
    private static Item baieTonique =
            new Item("baie_tonique", "Baie Tonique", "Restaure 10 PV au Bugémon actif.",
                     Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
    private static Item gelDefensif =
            new Item("gel_defensif", "Gel Defensif",
                     "Renforce temporairement la defense du Bugémon actif.", Item.ItemType.BOOST,
                     new EffectStatModifier(EffectTarget.THROWER, EffectStat.DEFENSE, 10,
                                            EffectDuration.PERMANENT));
    private static Item serumOffensif =
            new Item("serum_offensif", "Serum Offensif",
                     "Renforce temporairement l'attaque du Bugémon actif.", Item.ItemType.BOOST,
                     new EffectStatModifier(EffectTarget.THROWER, EffectStat.ATTACK, 10,
                                            EffectDuration.PERMANENT));

    // TODO: query the database to get the default items for stater inventory
    public static Inventory addStarterItem(Inventory inventory) {
        inventory.addItem(baieRevigorante, 3);
        inventory.addItem(baieTonique, 2);
        inventory.addItem(gelDefensif, 1);
        inventory.addItem(serumOffensif, 1);
        return inventory;
    }
}
