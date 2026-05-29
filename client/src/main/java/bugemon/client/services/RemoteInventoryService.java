package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.models.item.Inventory;
import bugemon.common.net.AckPacket;
import bugemon.common.net.SaveInventoryRequestPacket;

/**
 * Client-side facade over the server's inventory persistence. Inventory mutations are applied locally on the player's
 * {@link Inventory}; this facade persists the result.
 */
public class RemoteInventoryService {

    private final NetworkManager network;

    public RemoteInventoryService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Persists the player's inventory.
     *
     * @param inventory
     *            the inventory to persist
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> save(Inventory inventory) {
        return this.network.sendAsync(new SaveInventoryRequestPacket(inventory), AckPacket.class).thenAccept(ack -> {
        });
    }
}
