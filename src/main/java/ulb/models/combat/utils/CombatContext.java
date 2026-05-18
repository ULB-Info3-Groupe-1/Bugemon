package ulb.models.combat.utils;

import ulb.models.combat.CombatTeam;
import ulb.models.item.Inventory;

public record CombatContext(CombatTeam allyTeam, CombatTeam opponentTeam, Inventory allyInventory,
        Inventory opponentInventory) {
}
