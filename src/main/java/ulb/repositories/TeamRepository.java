package ulb.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.Team;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;

public class TeamRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(TeamRepository.class);
    private final StaticDataRepository staticDataRepository;
    private final BugemonRepository bugemonRepository;

    public TeamRepository(DatabaseConnection dbConnection, StaticDataRepository staticDataRepository,
            BugemonRepository bugemonRepository, Map<String, String> queries) {
        super(dbConnection, queries);
        this.staticDataRepository = staticDataRepository;
        this.bugemonRepository = bugemonRepository;
    }

    // --- TEAMS ---

    public void clearTeams(String playername) {
        LOG.debug("Clearing teams for playername: {}", playername);
        executeUpdate("ClearTeamMembers", playername);
        executeUpdate("ClearTeams", playername);
    }

    public void createTeam(String playername, String teamName)
            throws TeamNameAlreadyExistsException, TeamNameEmptyException {
        LOG.debug("Creating team '{}' for playername: {}", teamName, playername);
        this.checkValidName(teamName);
        if (this.teamNameAlreadyExists(playername, teamName)) {
            throw new TeamNameAlreadyExistsException(" Team name already exists: " + teamName);
        }
        executeUpdate("CreateTeam", playername, teamName);
    }

    /**
     * Delete a team and its members
     *
     * @param playername
     *            the player's name who owns the team
     * @param teamName
     *            the team's name to delete
     */
    public void deleteTeam(String playername, String teamName) throws TeamNotFoundException, TeamNameEmptyException {
        LOG.debug("Deleting team '{}' for playername: {}", teamName, playername);
        this.checkValidName(teamName);
        this.checkTeamExists(playername, teamName);
        executeUpdate("DeleteTeamMembers", playername, teamName);
        executeUpdate("DeleteTeam", playername, teamName);
    }

    /**
     * Rename a team
     *
     * @param playername
     *            the player's name who owns the team to rename
     * @param oldTeamName
     *            the team's name to rename
     * @param newTeamName
     *            the team's new name
     * @throws TeamNameAlreadyExistsException
     *             if the new team name already exists
     * @throws TeamNotFoundException
     *             if the old team name does not exist
     */
    public void renameTeam(String playername, String oldTeamName, String newTeamName)
            throws TeamNameAlreadyExistsException, TeamNotFoundException, TeamNameEmptyException {
        LOG.debug("Renaming team for playername: {} from '{}' to '{}'", playername, oldTeamName, newTeamName);
        if (this.teamNameAlreadyExists(playername, newTeamName)) {
            throw new TeamNameAlreadyExistsException(" Team name already exists: " + newTeamName);
        }
        this.checkTeamExists(playername, oldTeamName);
        this.checkValidName(newTeamName);
        executeUpdate("RenameTeam", newTeamName, playername, oldTeamName);
    }

    public void modifyTeam(String playername, String teamName, List<TeamMemberDTO> members)
            throws TeamEmptyException, TeamNotFoundException {
        if (members.isEmpty()) {
            throw new TeamEmptyException("Team is empty");
        }
        this.checkTeamExists(playername, teamName);
        executeUpdate("RemoveTeamComposition", playername, teamName);
        members.forEach(this::addTeamMember);
    }

    /**
     * Load all teams for a player
     *
     * @param playername
     *            the player's name who owns the teams
     * @return (List<BugemonTeam>) the teams of the player to be loaded
     * @throws TeamNotFoundException
     *             if the player has no teams
     */
    public List<Team> loadTeams(String playername) {
        List<Team> playerTeams = new ArrayList<>();

        Map<String, PlayerBugemonDTO> bugemonStatsMap = this.bugemonRepository.getPlayerBugemons(playername).stream()
                .collect(Collectors.toMap(PlayerBugemonDTO::bugemonName, pb -> pb));

        Map<String, Bugemon> defaultBugemonsMap = this.staticDataRepository.getAllDefaultBugemons().stream()
                .collect(Collectors.toMap(Bugemon::getName, b -> b));

        for (TeamDTO teamDto : this.getPlayerTeams(playername)) {
            Team team = new Team();
            team.setName(teamDto.teamName());

            this.getTeamMembers(playername, teamDto.teamName()).forEach(member -> {
                PlayerBugemonDTO pb = bugemonStatsMap.get(member.bugemonName());
                Bugemon base = defaultBugemonsMap.get(member.bugemonName());

                if (pb != null && base != null) {
                    StaticBugemonDataDTO staticDto = new StaticBugemonDataDTO(base.getName(), base.getType().name(),
                            base.getSpriteURL(), base.getAttackList(), base.isStarter());
                    team.add(BugemonFactory.createBugemon(staticDto, pb));
                }
            });
            playerTeams.add(team);
        }
        return playerTeams;
    }

    private List<TeamDTO> getPlayerTeams(String playername) {
        LOG.debug("Getting teams for playername: {}", playername);
        return executeQuery("GetPlayerTeams", rs -> new TeamDTO(playername, rs.getString(DatabaseColumns.COL_NAME)),
                playername);
    }

    /**
     * Load the current team for a player if it exists, otherwise return an empty optional
     *
     * @param playername
     *            (String) the player's name who owns the current team
     * @return (Optional<BugemonTeam>) the current team of the player if it exists, otherwise an empty optional
     */
    public Optional<Team> loadCurrentTeam(String playername) {
        LOG.debug("Getting current team for playername: {}", playername);

        if (!this.hasActiveTeam(playername)) {
            LOG.debug("No current team for playername: {}", playername);
            return Optional.empty();
        }

        List<PlayerBugemonDTO> teamMembers = executeQuery("GetPlayerCurrentTeam",
                rs -> new PlayerBugemonDTO(playername, rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE), rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK),
                        rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                        rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)),
                playername);

        Map<String, Bugemon> defaultBugemonsMap = this.staticDataRepository.getAllDefaultBugemons().stream()
                .collect(Collectors.toMap(Bugemon::getName, b -> b));

        Team currentTeam = executeQuery("GetPlayerCurrentTeamName",
                rs -> new Team(rs.getString(DatabaseColumns.COL_CURRENT_TEAM)), playername).get(0);

        for (PlayerBugemonDTO pb : teamMembers) {
            Bugemon base = defaultBugemonsMap.get(pb.bugemonName());
            if (base != null) {
                StaticBugemonDataDTO staticDto = new StaticBugemonDataDTO(base.getName(), base.getType().name(),
                        base.getSpriteURL(), base.getAttackList(), base.isStarter());
                currentTeam.add(BugemonFactory.createBugemon(staticDto, pb));
            }
        }

        return Optional.of(currentTeam);
    }

    public void setPlayerCurrentTeam(String playername, String teamName) {
        LOG.debug("Setting current team for {} to '{}'", playername, teamName);
        executeUpdate("SetPlayerCurrentTeam", teamName, playername);
    }

    public void unsetPlayerCurrentTeam(String playername) {
        LOG.debug("Unsetting current team for playername: {}", playername);
        executeUpdate("UnsetPlayerCurrentTeam", playername);
    }

    private boolean hasActiveTeam(String playername) {
        return executeQuery("GetPlayerCurrentTeamName", rs -> rs.getString("current_team"), playername).stream()
                .anyMatch(Objects::nonNull);
    }

    // --- TEAM MEMBERS ---

    public void addTeamMember(TeamMemberDTO dto) {
        executeUpdate("AddTeamMember", dto.playername(), dto.teamName(), dto.bugemonName(), dto.slotPosition());
    }

    public void removeTeamMember(String playername, String teamName, String bugemonName) throws TeamNotFoundException {
        this.checkTeamExists(playername, teamName);
        executeUpdate("RemoveTeamMember", playername, teamName, bugemonName);
    }

    public List<TeamMemberDTO> getTeamMembers(String playername, String teamName) {
        return executeQuery("GetTeamMembers",
                rs -> new TeamMemberDTO(rs.getString(DatabaseColumns.COL_PLAYERNAME),
                        rs.getString(DatabaseColumns.COL_TEAM_NAME), rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)),
                playername, teamName);
    }

    // --- Utils ---

    private boolean teamNameAlreadyExists(String playername, String teamName) {
        return !executeQuery("TeamNameAlreadyExists", rs -> true, playername, teamName).isEmpty();
    }

    private void checkTeamExists(String playername, String teamName) throws TeamNotFoundException {
        if (this.getPlayerTeams(playername).stream().noneMatch(team -> team.teamName().equals(teamName))) {
            throw new TeamNotFoundException(" Team name does not exist: " + teamName);
        }
    }

    private void checkValidName(String name) throws TeamNameEmptyException {
        if (name == null || name.isBlank()) {
            throw new TeamNameEmptyException(name);
        }
    }
}
