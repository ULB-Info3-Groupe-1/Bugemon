package bugemon.client.services;

import java.util.List;
import java.util.Optional;

import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.Team;

/**
 * Immutable client-side cache of the data backing the team-management screen, fetched in one round-trip.
 *
 * <p>
 * It answers every read the controller used to delegate to the server-side {@code TeamService}/{@code BugemonService}
 * (team names, lookups, "is saved" checks, selectable Bugemons) purely locally, so only mutations need to travel back
 * to the server.
 */
public class TeamScreenData {

    private final List<PlayerBugemon> playerBugemons;
    private final List<Team> savedTeams;

    public TeamScreenData(List<PlayerBugemon> playerBugemons, List<Team> savedTeams) {
        this.playerBugemons = playerBugemons;
        this.savedTeams = savedTeams;
    }

    /** Returns all non-boss Bugemons the player can place in a team. */
    public List<PlayerBugemon> getPlayerBugemons() {
        return this.playerBugemons;
    }

    /**
     * Finds a selectable Bugemon by species name.
     *
     * @param name
     *            the species name
     * @return the matching {@link PlayerBugemon}
     * @throws java.util.NoSuchElementException
     *             if no selectable Bugemon has that name
     */
    public PlayerBugemon getPlayerBugemon(String name) {
        return this.playerBugemons.stream().filter(b -> b.getName().equals(name)).findFirst().orElseThrow();
    }

    /** Returns the names of all saved teams. */
    public List<String> getTeamNames() {
        return this.savedTeams.stream().map(Team::getName).toList();
    }

    /**
     * Returns the saved team with the given name, if any.
     *
     * @param teamName
     *            the team name to look up
     */
    public Optional<Team> getTeam(String teamName) {
        return this.savedTeams.stream().filter(t -> teamName.equals(t.getName())).findFirst();
    }

    public boolean teamExists(String teamName) {
        return this.getTeam(teamName).isPresent();
    }

    /**
     * Returns {@code true} if a saved team matches {@code team} by name and ordered member names — the same notion of
     * equality the server uses when comparing persisted team records.
     *
     * @param team
     *            the working team to check
     */
    public boolean isTeamSaved(Team team) {
        if (team.getName() == null) {
            return false;
        }
        return this.getTeam(team.getName()).map(saved -> this.memberNames(saved).equals(this.memberNames(team)))
                .orElse(false);
    }

    private List<String> memberNames(Team team) {
        return team.getMembers().stream().map(PlayerBugemon::getName).toList();
    }
}
