package bugemon.common.net;

import bugemon.common.models.team.Team;

/**
 * Request asking the server to persist (insert or overwrite) the given team for the authenticated player.
 *
 * <p>
 * Name-collision is pre-checked client-side against the cached team list, so this operation simply persists. The reply
 * is an {@link AckPacket}.
 *
 * @param team
 *            the team to persist
 */
public record SaveTeamRequestPacket(Team team) implements Packet {
}
