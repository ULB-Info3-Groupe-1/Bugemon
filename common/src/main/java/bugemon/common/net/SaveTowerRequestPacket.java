package bugemon.common.net;

import bugemon.common.dto.persistence.TowerDTO;

/**
 * Request asking the server to persist the current tower-run snapshot. The reply is an {@link AckPacket}.
 *
 * @param towerDTO
 *            the run snapshot to persist
 */
public record SaveTowerRequestPacket(TowerDTO towerDTO) implements Packet {
}
