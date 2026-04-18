package ulb.services;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;

public class InventoryService {

    // TODO: hardcoded items to move to database
    private static final Item BAIE_REVIGORANTE = new Item("baie_revigorante", "Baie Revigorante",
            "Restaure 20 PV au Bugémon actif.", Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));
    private static final Item BAIE_TONIQUE = new Item("baie_tonique", "Baie Tonique",
            "Restaure 10 PV au Bugémon actif.", Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 10));
    private static final Item GEL_DEFENSIF = new Item("gel_defensif", "Gel Defensif",
            "Renforce temporairement la defense du Bugémon actif.", Item.ItemType.BOOST,
            new EffectStatModifier(EffectTarget.THROWER, EffectStat.DEFENSE, 10, EffectDuration.PERMANENT));
    private static final Item SERUM_OFFENSIF = new Item("serum_offensif", "Serum Offensif",
            "Renforce temporairement l'attaque du Bugémon actif.", Item.ItemType.BOOST,
            new EffectStatModifier(EffectTarget.THROWER, EffectStat.ATTACK, 10, EffectDuration.PERMANENT));

    private final Inventory inventory;

    public InventoryService() {
        this.inventory = new Inventory();
        this.inventory.addItem(BAIE_REVIGORANTE, 3);
        this.inventory.addItem(BAIE_TONIQUE, 2);
        this.inventory.addItem(GEL_DEFENSIF, 1);
        this.inventory.addItem(SERUM_OFFENSIF, 1);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

}
