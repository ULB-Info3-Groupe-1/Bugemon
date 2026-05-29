package bugemon.client.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.net.AckPacket;
import bugemon.common.net.AttacksResponsePacket;
import bugemon.common.net.CreateBugemonRequestPacket;
import bugemon.common.net.CreateBugemonResponsePacket;
import bugemon.common.net.GetAttacksRequestPacket;
import bugemon.common.net.SavePlayerBugemonRequestPacket;

/**
 * Client-side facade over the server's Bugemon operations: fetching the static attack catalogue and creating a custom
 * Bugemon species (sprite shipped as raw bytes).
 */
public class RemoteBugemonService {

    private final NetworkManager network;

    public RemoteBugemonService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Fetches every static attack definition (the client filters by element type locally).
     *
     * @return a future completing with all attacks
     */
    public CompletableFuture<List<Attack>> getAttacks() {
        return this.network.sendAsync(new GetAttacksRequestPacket(), AttacksResponsePacket.class)
                .thenApply(AttacksResponsePacket::attacks);
    }

    /**
     * Requests creation of a custom Bugemon species.
     *
     * @param name
     *            species name
     * @param type
     *            element type
     * @param attackValue
     *            rounded attack stat
     * @param defenseValue
     *            rounded defense stat
     * @param initiativeValue
     *            rounded initiative stat
     * @param hpValue
     *            rounded max-HP stat
     * @param attacks
     *            assigned attacks
     * @param spriteBytes
     *            raw sprite image bytes
     * @return a future completing with the creation status
     */
    public CompletableFuture<CreateBugemonResponsePacket.Status> createBugemon(String name, ElementType type,
            int attackValue, int defenseValue, int initiativeValue, int hpValue, List<Attack> attacks,
            byte[] spriteBytes) {
        CreateBugemonRequestPacket request = new CreateBugemonRequestPacket(name, type, attackValue, defenseValue,
                initiativeValue, hpValue, attacks, spriteBytes);
        return this.network.sendAsync(request, CreateBugemonResponsePacket.class)
                .thenApply(CreateBugemonResponsePacket::status);
    }

    /**
     * Persists a single player-owned Bugemon's progression (after a level-up or reward).
     *
     * @param playerBugemon
     *            the Bugemon to persist
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> savePlayerBugemon(PlayerBugemon playerBugemon) {
        return this.network.sendAsync(new SavePlayerBugemonRequestPacket(playerBugemon), AckPacket.class)
                .thenAccept(ack -> {
                });
    }
}
