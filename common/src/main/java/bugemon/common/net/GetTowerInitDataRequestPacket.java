package bugemon.common.net;

/**
 * Request asking the server for everything needed to start or resume a tower run in one round-trip.
 *
 * <p>
 * The reply is a {@link TowerInitDataPacket}. The client then owns the live run (creation, navigation, display) and
 * sends only persistence requests back.
 */
public record GetTowerInitDataRequestPacket() implements Packet {
}
