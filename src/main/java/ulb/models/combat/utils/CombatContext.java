package ulb.models.combat.utils;

import ulb.models.combat.CombatTeam;
import ulb.models.item.Inventory;

/**
 * Immutable snapshot of the two opposing sides at the start of a combat resolution pass.
 *
 * <p>
 * Bundles the ally and opponent {@link CombatTeam} instances together with their respective {@link Inventory} objects
 * so that resolvers and visitors can access all relevant combat state through a single parameter rather than several
 * separate ones.
 *
 * @param allyTeam
 *            the player's team
 * @param opponentTeam
 *            the opponent's team
 * @param allyInventory
 *            the player's item inventory
 * @param opponentInventory
 *            the opponent's item inventory
 */
public record CombatContext(CombatTeam allyTeam, CombatTeam opponentTeam, Inventory allyInventory,
        Inventory opponentInventory) {
}
