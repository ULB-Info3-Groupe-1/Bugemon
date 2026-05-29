package bugemon.common.net;

import bugemon.common.models.team.Team;

/**
 * Request asking the server to replace a persisted team with the current in-memory state of the given team.
 *
 * <p>
 * The reply is a {@link TeamOpResponsePacket} whose status is {@code NOT_FOUND} if the team does not exist.
 *
 * @param team
 *            the team whose changes are to be persisted
 */
public record ModifyTeamRequestPacket(Team team) implements Packet {
}
