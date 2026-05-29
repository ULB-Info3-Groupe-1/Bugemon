package bugemon.common.net;

/**
 * Request asking the server to delete the named team and clear the active-team selection.
 *
 * <p>
 * The reply is a {@link TeamOpResponsePacket} whose status is {@code NOT_FOUND} if no such team exists.
 *
 * @param teamName
 *            the name of the team to delete
 */
public record DeleteTeamRequestPacket(String teamName) implements Packet {
}
