package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.net.GetStaticDataRequestPacket;
import bugemon.common.net.StaticDataPacket;

/**
 * Client-side facade for fetching the immutable game catalogue (species, attacks, items, skill tree) once at login.
 * The result is cached by the caller and reused for local combat, reward and level-up logic.
 */
public class RemoteStaticDataService {

    private final NetworkManager network;

    public RemoteStaticDataService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Fetches the immutable game catalogue.
     *
     * @return a future completing with the {@link StaticDataPacket}
     */
    public CompletableFuture<StaticDataPacket> getStaticData() {
        return this.network.sendAsync(new GetStaticDataRequestPacket(), StaticDataPacket.class);
    }
}
