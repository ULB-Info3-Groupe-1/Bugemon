package bugemon.common.net;

import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.models.team.Team;

/**
 * Request asking the server to persist the full in-progress game state in one round-trip: skill tree, active-team
 * Bugemon progression, inventory, and (optionally) the tower run. The reply is an {@link AckPacket}.
 *
 * @param activeTeam
 *            the active team whose member progression is saved, or {@code null} if none
 * @param inventory
 *            the player's inventory
 * @param skillTreeState
 *            the player's skill-tree progression
 * @param tower
 *            the current tower-run snapshot to persist, or {@code null} to leave the saved run untouched
 */
public record SaveGameRequestPacket(Team activeTeam, Inventory inventory, SkillTreeState skillTreeState,
        TowerDTO tower) implements Packet {
}
