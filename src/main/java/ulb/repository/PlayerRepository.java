package ulb.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repository.dto.PlayerBugemonDTO;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;

public class PlayerRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerRepository.class);

    private final DatabaseConnection dbConnection;

    public PlayerRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(queries);
        this.dbConnection = dbConnection;
    }

    // ─── PLAYERS ────────────────────────────────────────────────────────────────

    /** @return the generated player ID */
    public int createPlayer(String playername) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("CreatePlayer"))) {
            ps.setString(1, playername);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(DatabaseColumns.COL_ID);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("createPlayer failed", e);
        }
        throw new IllegalStateException("createPlayer returned no id");
    }

    /** @return empty Optional if no player with that playername exists */
    public Optional<Integer> getPlayerIdByPlayername(String playername) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetPlayerByPlayername"))) {
            ps.setString(1, playername);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt(DatabaseColumns.COL_ID));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getPlayerIdByPlayername failed", e);
        }
        return Optional.empty();
    }

    // ─── PALYER BUGEMONS ────────────────────────────────────────────────────────

    public void savePlayerBugemon(PlayerBugemonDTO dto) {
        LOG.debug("Saving player bugemon: {}", dto);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("SavePlayerBugemon"))) {
            ps.setInt(1, dto.playerId());
            ps.setString(2, dto.bugemonName());
            ps.setInt(3, dto.currentDefense());
            ps.setInt(4, dto.currentAttackPower());
            ps.setInt(5, dto.currentInitiative());
            ps.setInt(6, dto.currentMaxHp());
            ps.setInt(7, dto.currentXp());
            ps.setInt(8, dto.currentLevel());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("savePlayerBugemon failed", e);
        }
    }

    public void updatePlayerBugemon(PlayerBugemonDTO dto) {
        LOG.debug("Updating player bugemon: {}", dto);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("UpdatePlayerBugemon"))) {
            ps.setInt(1, dto.currentDefense());
            ps.setInt(2, dto.currentAttackPower());
            ps.setInt(3, dto.currentInitiative());
            ps.setInt(4, dto.currentMaxHp());
            ps.setInt(5, dto.currentXp());
            ps.setInt(6, dto.currentLevel());
            ps.setInt(7, dto.playerId());
            ps.setString(8, dto.bugemonName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("updatePlayerBugemon failed", e);
        }
    }

    public List<PlayerBugemonDTO> getPlayerBugemons(int playerId) {
        LOG.debug("Getting bugemons for playerId: {}", playerId);

        List<PlayerBugemonDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetPlayerBugemons"))) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new PlayerBugemonDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID),
                        rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK),
                        rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                        rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getPlayerBugemons failed", e);
        }
        return result;
    }

    // ─── TEAMS ────────────────────────────────────────────────────────────────

    public void createTeam(int playerId, String teamName) {
        LOG.debug("Creating team '{}' for playerId: {}", teamName, playerId);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("CreateTeam"))) {
            ps.setInt(1, playerId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createTeam failed", e);
        }
    }

    /** Deletes all members of the team, then the team record itself. */
    public void deleteTeam(int playerId, String teamName) {
        LOG.debug("Deleting team '{}' for playerId: {}", teamName, playerId);

        try (PreparedStatement psMembers = this.dbConnection.prepareStatement(this.getSql("DeleteTeamMembers"));
                PreparedStatement psTeam = this.dbConnection.prepareStatement(this.getSql("DeleteTeam"))) {
            psMembers.setInt(1, playerId);
            psMembers.setString(2, teamName);
            psMembers.executeUpdate();

            psTeam.setInt(1, playerId);
            psTeam.setString(2, teamName);
            psTeam.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeam failed", e);
        }
    }

    public void deleteTeamMembers(int playerId, String teamName) {
        LOG.debug("Deleting team members for playerId: {} and teamName: {}", playerId, teamName);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("DeleteTeamMembers"))) {
            ps.setInt(1, playerId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeamMembers failed", e);
        }
    }

    public List<TeamDTO> getPlayerTeams(int playerId) {
        LOG.debug("Getting teams for playerId: {}", playerId);

        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetPlayerTeams"))) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(
                        new TeamDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID), rs.getString(DatabaseColumns.COL_NAME)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getPlayerTeams failed", e);
        }
        return result;
    }

    // ─── TEAM MEMBERS ─────────────────────────────────────────────────────────

    /**
     * Add a member to a team in the database. This method takes a TeamMemberDTO object containing the details of the
     * team member to be added, including the player ID, team name, Bugemon ID, and slot position. It executes an SQL
     * statement to insert a new record into the database representing this team member, associating it with the
     * specified team and player. The method ensures that the new team member is correctly linked to the appropriate
     * team and player in the database. If an error occurs during the database operation, an IllegalStateException is
     * thrown.
     *
     * @param dto
     *            the TeamMemberDTO object containing the details of the team member to be added
     */
    public void addTeamMember(TeamMemberDTO dto) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("AddTeamMember"))) {
            ps.setInt(1, dto.playerId());
            ps.setString(2, dto.teamName());
            ps.setString(3, dto.bugemonName());
            ps.setInt(4, dto.slotPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("addTeamMember failed", e);
        }
    }

    /**
     * Remove a member from a team in the database. This method takes the player ID, team name, and Bugemon ID as
     * parameters to identify the specific team member to be removed. It executes an SQL statement to delete the
     * corresponding record from the database, effectively removing the specified team member from the team. The method
     * ensures that only the team member matching the provided player ID, team name, and Bugemon ID is removed from the
     * database. If an error occurs during the database operation, an IllegalStateException is thrown.
     *
     * @param playerId
     *            the ID of the player whose team member is to be removed
     * @param teamName
     *            the name of the team from which to remove the member
     * @param bugemonName
     *            the name of the Bugemon to be removed from the team
     */
    public void removeTeamMember(int playerId, String teamName, String bugemonName) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("RemoveTeamMember"))) {
            ps.setInt(1, playerId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("removeTeamMember failed", e);
        }
    }

    /**
     * Retrieve a list of TeamMemberDTO objects representing the members of a specific team for a player from the
     * database. This method executes an SQL query to search for all team member records associated with the given
     * player ID and team name, and constructs a list of TeamMemberDTO objects containing the details of each team
     * member found. The details include the player ID, team name, Bugemon ID, and slot position of each team member. If
     * an error occurs during the database query, an IllegalStateException is thrown.
     *
     * @param playerId
     *            the ID of the player for whom to retrieve team members
     * @param teamName
     *            the name of the team for which to retrieve members
     * @return a list of TeamMemberDTO objects representing the members of the specified team for the player
     */
    public List<TeamMemberDTO> getTeamMembers(int playerId, String teamName) {
        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetTeamMembers"))) {
            ps.setInt(1, playerId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID),
                        rs.getString(DatabaseColumns.COL_TEAM_NAME), rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getTeamMembers failed", e);
        }
        return result;
    }

    public void renameTeam(int playerId, String oldTeamName, String newTeamName) {
        LOG.debug("Renaming team for playerId: {} from '{}' to '{}'", playerId, oldTeamName, newTeamName);

        try (PreparedStatement psRenameTeam = this.dbConnection.prepareStatement(this.getSql("RenameTeam"))) {
            psRenameTeam.setString(1, newTeamName);
            psRenameTeam.setInt(2, playerId);
            psRenameTeam.setString(3, oldTeamName);
            psRenameTeam.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("renameTeam failed", e);
        }
    }
}
