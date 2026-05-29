package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.net.AckPacket;
import bugemon.common.net.DeleteTowerRequestPacket;
import bugemon.common.net.GetTowerInitDataRequestPacket;
import bugemon.common.net.SaveTowerRequestPacket;
import bugemon.common.net.TowerInitDataPacket;

/**
 * Client-side facade over the server's tower persistence.
 *
 * <p>
 * The client owns the live {@link bugemon.common.models.tower.TowerState} during play (built and advanced via
 * {@link bugemon.common.models.tower.TowerEngine}); this facade fetches the data needed to start/resume a run and
 * persists snapshots back to the server.
 */
public class RemoteTowerService {

    private final NetworkManager network;

    public RemoteTowerService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Fetches everything needed to start or resume a run (saved snapshot + team, static items, skill tree).
     *
     * @return a future completing with the {@link TowerInitDataPacket}
     */
    public CompletableFuture<TowerInitDataPacket> getInitData() {
        return this.network.sendAsync(new GetTowerInitDataRequestPacket(), TowerInitDataPacket.class);
    }

    /**
     * Persists the given tower-run snapshot.
     *
     * @param towerDTO
     *            the run snapshot to persist
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> save(TowerDTO towerDTO) {
        return this.network.sendAsync(new SaveTowerRequestPacket(towerDTO), AckPacket.class).thenAccept(ack -> {
        });
    }

    /**
     * Deletes the player's saved tower run.
     *
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> delete() {
        return this.network.sendAsync(new DeleteTowerRequestPacket(), AckPacket.class).thenAccept(ack -> {
        });
    }
}
