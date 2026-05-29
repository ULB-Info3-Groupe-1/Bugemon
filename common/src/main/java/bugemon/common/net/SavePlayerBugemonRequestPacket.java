package bugemon.common.net;

import bugemon.common.models.player.PlayerBugemon;

/**
 * Request asking the server to persist a single player-owned Bugemon's progression. The reply is an {@link AckPacket}.
 *
 * @param playerBugemon
 *            the Bugemon whose progression to persist
 */
public record SavePlayerBugemonRequestPacket(PlayerBugemon playerBugemon) implements Packet {
}
