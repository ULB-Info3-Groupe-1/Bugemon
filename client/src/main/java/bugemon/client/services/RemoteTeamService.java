package bugemon.client.services;

import java.util.concurrent.CompletableFuture;

import bugemon.client.net.NetworkManager;
import bugemon.common.models.team.Team;
import bugemon.common.net.AckPacket;
import bugemon.common.net.DeleteTeamRequestPacket;
import bugemon.common.net.GetTeamScreenDataRequestPacket;
import bugemon.common.net.ModifyTeamRequestPacket;
import bugemon.common.net.RenameTeamRequestPacket;
import bugemon.common.net.SaveTeamRequestPacket;
import bugemon.common.net.SetActiveTeamRequestPacket;
import bugemon.common.net.TeamOpResponsePacket;
import bugemon.common.net.TeamScreenDataPacket;

/**
 * Client-side facade over the server's team operations.
 *
 * <p>
 * Reads are served from a {@link TeamScreenData} snapshot fetched via {@link #getScreenData()}; the methods here cover
 * the screen data fetch and every mutation. Each mutation future completes off the JavaFX thread.
 */
public class RemoteTeamService {

    private final NetworkManager network;

    public RemoteTeamService(NetworkManager network) {
        this.network = network;
    }

    /**
     * Fetches the selectable Bugemons and saved teams for the team-management screen.
     *
     * @return a future completing with a {@link TeamScreenData} cache
     */
    public CompletableFuture<TeamScreenData> getScreenData() {
        return this.network.sendAsync(new GetTeamScreenDataRequestPacket(), TeamScreenDataPacket.class)
                .thenApply(p -> new TeamScreenData(p.playerBugemons(), p.savedTeams()));
    }

    /**
     * Persists (inserts or overwrites) the given team.
     *
     * @param team
     *            the team to save
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> save(Team team) {
        return this.network.sendAsync(new SaveTeamRequestPacket(team), AckPacket.class).thenAccept(ack -> {
        });
    }

    /**
     * Deletes the named team.
     *
     * @param teamName
     *            the team to delete
     * @return a future completing with the operation status
     */
    public CompletableFuture<TeamOpResponsePacket.Status> deleteTeam(String teamName) {
        return this.network.sendAsync(new DeleteTeamRequestPacket(teamName), TeamOpResponsePacket.class)
                .thenApply(TeamOpResponsePacket::status);
    }

    /**
     * Renames the given team.
     *
     * @param team
     *            the team to rename
     * @param newName
     *            the desired new name
     * @return a future completing with the operation status
     */
    public CompletableFuture<TeamOpResponsePacket.Status> renameTeam(Team team, String newName) {
        return this.network.sendAsync(new RenameTeamRequestPacket(team, newName), TeamOpResponsePacket.class)
                .thenApply(TeamOpResponsePacket::status);
    }

    /**
     * Replaces the persisted team with the current state of the given team.
     *
     * @param team
     *            the team whose changes to persist
     * @return a future completing with the operation status
     */
    public CompletableFuture<TeamOpResponsePacket.Status> modify(Team team) {
        return this.network.sendAsync(new ModifyTeamRequestPacket(team), TeamOpResponsePacket.class)
                .thenApply(TeamOpResponsePacket::status);
    }

    /**
     * Marks the named team as active.
     *
     * @param teamName
     *            the team to activate
     * @return a future completing once the server acknowledges
     */
    public CompletableFuture<Void> setActiveTeam(String teamName) {
        return this.network.sendAsync(new SetActiveTeamRequestPacket(teamName), AckPacket.class).thenAccept(ack -> {
        });
    }
}
