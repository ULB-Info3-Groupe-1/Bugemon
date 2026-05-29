package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.models.team.Team;
import bugemon.common.net.AckPacket;
import bugemon.common.net.PlayerSnapshotPacket;
import bugemon.common.net.ResetGameRequestPacket;
import bugemon.common.net.SaveGameRequestPacket;

/**
 * Client-side facade over the server's save/reset operations.
 *
 * <p>
 * This is the template for every remote service facade: it owns no game logic, only translates a controller intent into
 * a {@link bugemon.common.net.Packet} sent through the {@link NetworkManager} and returns a {@link CompletableFuture}
 * that completes off the JavaFX thread. Controllers attach their UI updates to that future via
 * {@code Platform.runLater}.
 */
public class RemoteSaveService {

    private final NetworkManager network;

    public RemoteSaveService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Asks the server to reset the player's progression to a brand-new game.
     *
     * @return a future completing with the fresh {@link PlayerSnapshotPacket}, or completing exceptionally with a
     *         {@link bugemon.client.net.NetworkException}
     */
    public CompletableFuture<PlayerSnapshotPacket> resetGame() {
        return this.network.sendAsync(new ResetGameRequestPacket(), PlayerSnapshotPacket.class);
    }

    /**
     * Persists the full in-progress game state (skills, active-team progression, inventory, and optionally the tower
     * run) in one round-trip.
     *
     * @param activeTeam
     *            the active team whose progression to save, or {@code null} if none
     * @param inventory
     *            the player's inventory
     * @param skillTreeState
     *            the player's skill-tree progression
     * @param tower
     *            the current tower-run snapshot, or {@code null} to leave the saved run untouched
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> saveGame(Team activeTeam, Inventory inventory, SkillTreeState skillTreeState,
            TowerDTO tower) {
        SaveGameRequestPacket request = new SaveGameRequestPacket(activeTeam, inventory, skillTreeState, tower);
        return this.network.sendAsync(request, AckPacket.class).thenAccept(ack -> {
        });
    }
}
