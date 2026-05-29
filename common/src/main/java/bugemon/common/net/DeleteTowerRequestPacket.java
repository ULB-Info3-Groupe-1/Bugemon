package bugemon.common.net;

/**
 * Request asking the server to delete the player's saved tower run (e.g. on defeat or run completion). The reply is an
 * {@link AckPacket}.
 */
public record DeleteTowerRequestPacket() implements Packet {
}
