package bugemon.common.net;

import bugemon.common.models.item.Inventory;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.models.team.Team;

/**
 * The authoritative initial state of a player's profile, pushed by the server right after authentication.
 *
 * <p>
 * This is the "snapshot at login" half of the hybrid protocol: the server builds the live domain objects (reconstructed
 * from the database near the repositories) and ships them in one message, from which the client assembles its
 * {@code PlayerState}. Subsequent mutations travel as smaller DTO-carrying packets.
 *
 * @param activeTeam
 *            the player's currently selected team, or {@code null} if none is selected yet
 * @param inventory
 *            the player's inventory
 * @param skillTreeState
 *            the player's skill-tree progression
 */
public record PlayerSnapshotPacket(Team activeTeam, Inventory inventory, SkillTreeState skillTreeState)
        implements Packet {
}
