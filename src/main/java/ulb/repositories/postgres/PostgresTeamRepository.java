package ulb.repositories.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.TeamRepository;

import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameInvalidException;

public class PostgresTeamRepository extends AbstractRepository implements TeamRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresTeamRepository.class);

    public PostgresTeamRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public List<TeamDTO> findAll(String playerName) {
        LOG.debug("Finding all teams for playername: {}", playerName);
        List<String> teamNames = this.getPlayerTeamNames(playerName);
        List<TeamDTO> teams = new ArrayList<>();
        for (String teamName : teamNames) {
            List<TeamMemberDTO> members = this.getTeamMembers(playerName, teamName);
            teams.add(new TeamDTO(playerName, teamName, members));
        }
        return teams;
    }

    @Override
    public TeamDTO loadTeam(String playerName, String teamName) {
        LOG.debug("Loading team '{}' for playername: {}", teamName, playerName);
        List<TeamMemberDTO> members = this.getTeamMembers(playerName, teamName);
        return new TeamDTO(playerName, teamName, members);
    }

    @Override
    public void saveTeam(String playerName, TeamDTO team) throws TeamEmptyException {
        LOG.debug("Saving team '{}' for playername: {}", team.teamName(), playerName);

        try {
            this.checkValidName(team.teamName());
            if (this.teamExists(playerName, team.teamName())) {
                LOG.debug("Team '{}' already exists for playername: {}, modifying it", team.teamName(), playerName);
                this.modifyTeam(playerName, team.teamName(), team.members());
            } else {
                LOG.debug("Team '{}' does not exist for playername: {}, creating it", team.teamName(), playerName);
                this.createTeam(playerName, team.teamName());
                this.modifyTeam(playerName, team.teamName(), team.members());
            }
        } catch (TeamEmptyException | TeamNameInvalidException e) {
            this.createTeam(playerName, team.teamName());
        }
    }

    @Override
    public void deleteTeam(String playerName, String teamName) {
        LOG.debug("Deleting team '{}' for playername: {}", teamName, playerName);
        try {
            this.checkValidName(teamName);
            if (this.teamExists(playerName, teamName)) {
                executeUpdate("DeleteTeamMembers", playerName, teamName);
                executeUpdate("DeleteTeam", playerName, teamName);
            }
        } catch (TeamNameInvalidException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private void createTeam(String playername, String teamName) {
        executeUpdate("CreateTeam", playername, teamName);
    }

    private void modifyTeam(String playername, String teamName, List<TeamMemberDTO> members)
            throws TeamEmptyException {
        if (members.isEmpty()) {
            throw new TeamEmptyException("Team is empty");
        }
        executeUpdate("RemoveTeamComposition", playername, teamName);
        members.forEach(member -> this.addTeamMember(playername, teamName, member));
    }

    private void addTeamMember(String playername, String teamName, TeamMemberDTO dto) {
        executeUpdate("AddTeamMember", playername, teamName, dto.bugemonName(), dto.slotPosition());
    }

    private List<String> getPlayerTeamNames(String playername) {
        LOG.debug("Getting team names for playername: {}", playername);
        return executeQuery("GetPlayerTeams", rs -> rs.getString(DatabaseColumns.COL_NAME), playername);
    }

    private List<TeamMemberDTO> getTeamMembers(String playername, String teamName) {
        return executeQuery("GetTeamMembers",
                rs -> new TeamMemberDTO(rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)),
                playername, teamName);
    }

    // --- Utils ---

    private boolean teamExists(String playername, String teamName) {
        if (this.getPlayerTeamNames(playername).stream().noneMatch(name -> name.equals(teamName))) {
            return false;
        }
        return true;
    }

    private void checkValidName(String name) throws TeamNameInvalidException {
        if (name == null || name.isBlank()) {
            throw new TeamNameInvalidException(name);
        }
    }
}
