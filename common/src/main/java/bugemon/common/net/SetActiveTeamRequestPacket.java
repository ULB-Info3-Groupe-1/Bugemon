package bugemon.common.net;

/**
 * Request asking the server to mark the named team as the player's active team.
 *
 * <p>
 * The reply is an {@link AckPacket}.
 *
 * @param teamName
 *            the name of the team to activate
 */
public record SetActiveTeamRequestPacket(String teamName) implements Packet {
}
