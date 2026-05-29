package bugemon.common.net;

import java.util.List;

import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.Team;

/**
 * Server reply carrying the data backing the team-management screen.
 *
 * @param playerBugemons
 *            all non-boss Bugemons the player can place in a team (owned ones at their saved progression, the rest at
 *            base stats)
 * @param savedTeams
 *            the player's persisted teams
 */
public record TeamScreenDataPacket(List<PlayerBugemon> playerBugemons, List<Team> savedTeams) implements Packet {
}
