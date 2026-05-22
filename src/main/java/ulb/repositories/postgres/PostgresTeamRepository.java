package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.common.dto.persistence.TeamDTO;
import ulb.common.dto.persistence.TeamMemberDTO;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.TeamRepository;

public class PostgresTeamRepository extends AbstractRepository implements TeamRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresTeamRepository.class);

    private final BugemonRepository bugemonRepository;

    public PostgresTeamRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            BugemonRepository bugemonRepository) {
        super(dbConnection, queries);
        this.bugemonRepository = bugemonRepository;
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
        team.members().forEach(member -> {
            if (this.bugemonRepository.findByName(playerName, member.bugemonName()).isEmpty()) {
                LOG.debug("PlayerBugemon '{}' does not exist for player '{}', creating it", member.bugemonName(),
                        playerName);
                this.bugemonRepository.save(new PlayerBugemonDTO(playerName, member.bugemonName(), 0, 0, 0, 0, 0, 1,
                        this.bugemonRepository.findBase(member.bugemonName()).orElseThrow().attacks()));
            }
            this.addTeamMember(playerName, team.teamName(), member);
        });
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
