package bugemon.common.net;

import bugemon.common.models.item.Inventory;

/**
 * Request asking the server to persist the player's inventory. The reply is an {@link AckPacket}.
 *
 * @param inventory
 *            the inventory to persist
 */
public record SaveInventoryRequestPacket(Inventory inventory) implements Packet {
}
