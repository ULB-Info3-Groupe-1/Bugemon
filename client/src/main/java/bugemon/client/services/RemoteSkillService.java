package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.models.skills.SkillTree;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.net.AckPacket;
import bugemon.common.net.GetSkillTreeRequestPacket;
import bugemon.common.net.SaveSkillTreeRequestPacket;
import bugemon.common.net.SkillTreeResponsePacket;

/**
 * Client-side facade over the server's skill-tree operations.
 *
 * <p>
 * The skill-tree definition is fetched once for rendering and point-allocation validation; the actual allocation is
 * pure model logic performed locally on the {@link SkillTreeState}, after which the updated state is persisted through
 * {@link #save(SkillTreeState)}.
 */
public class RemoteSkillService {

    private final NetworkManager network;

    public RemoteSkillService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Fetches the static skill-tree definition.
     *
     * @return a future completing with the {@link SkillTree}
     */
    public CompletableFuture<SkillTree> getSkillTree() {
        return this.network.sendAsync(new GetSkillTreeRequestPacket(), SkillTreeResponsePacket.class)
                .thenApply(SkillTreeResponsePacket::skillTree);
    }

    /**
     * Persists the player's skill-tree progression.
     *
     * @param state
     *            the up-to-date skill-tree state
     * @return a future completing once the server has acknowledged the save
     */
    public CompletableFuture<Void> save(SkillTreeState state) {
        return this.network.sendAsync(new SaveSkillTreeRequestPacket(state), AckPacket.class).thenAccept(ack -> {
        });
    }
}
