package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.TeamRepository;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;

public class PostgresTeamRepository extends AbstractRepository implements TeamRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresTeamRepository.class);

    public PostgresTeamRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public List<TeamDTO> findAll(String playerName) {
        LOG.debug("Finding all teams for playername: {}", playerName);
        return this.getPlayerTeamNames(playerName).stream()
                .map(name -> new TeamDTO(playerName, name, this.getTeamMembers(playerName, name))).toList();
    }

    @Override
    public Optional<TeamDTO> findByName(String playerName, String teamName) {
        LOG.debug("Finding team '{}' for playername: {}", teamName, playerName);
        if (!this.teamExists(playerName, teamName)) {
            return Optional.empty();
        }
        return Optional.of(new TeamDTO(playerName, teamName, this.getTeamMembers(playerName, teamName)));
    }

    @Override
    public void save(String playerName, TeamDTO team) {
        LOG.debug("Saving team '{}' for playername: {}", team.teamName(), playerName);
        executeUpdate("CreateTeam", playerName, team.teamName());
        executeUpdate("RemoveTeamComposition", playerName, team.teamName());
        team.members().forEach(member -> this.addTeamMember(playerName, team.teamName(), member));
    }

    @Override
    public void delete(String playerName, String teamName) {
        LOG.debug("Deleting team '{}' for playername: {}", teamName, playerName);
        executeUpdate("DeleteTeamMembers", playerName, teamName);
        executeUpdate("DeleteTeam", playerName, teamName);
    }

    @Override
    public void deleteAll(String playerName) {
        LOG.debug("Removing all teams for playername: {}", playerName);
        executeUpdate("ClearTeamMembers", playerName);
        executeUpdate("ClearTeams", playerName);
    }

    @Override
    public Optional<String> getCurrentTeamName(String playername) {
        LOG.debug("Getting current teamName for playername: {}", playername);
        List<String> results = executeQuery("GetPlayerCurrentTeamName", rs -> rs.getString(DatabaseColumns.COL_NAME), playername);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(results.get(0));
    }

    private void addTeamMember(String playerName, String teamName, TeamMemberDTO dto) {
        executeUpdate("AddTeamMember", playerName, teamName, dto.bugemonName(), dto.slotPosition());
    }

    private List<String> getPlayerTeamNames(String playerName) {
        return executeQuery("GetPlayerTeams", rs -> rs.getString(DatabaseColumns.COL_NAME), playerName);
    }

    private List<TeamMemberDTO> getTeamMembers(String playerName, String teamName) {
        return executeQuery("GetTeamMembers", this::mapTeamMember, playerName, teamName);
    }

    private TeamMemberDTO mapTeamMember(ResultSet rs) throws SQLException {
        return new TeamMemberDTO(rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                rs.getInt(DatabaseColumns.COL_SLOT_POSITION));
    }

    private boolean teamExists(String playerName, String teamName) {
        return this.getPlayerTeamNames(playerName).stream().anyMatch(teamName::equals);
    }
}
