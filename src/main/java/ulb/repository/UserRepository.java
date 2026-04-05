package ulb.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class UserRepository {
    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private final DatabaseRepository dbRepository;
    private final DatabaseConnection dbConnection;

    public UserRepository(DatabaseRepository dbRepository, DatabaseConnection dbConnection) {
        this.dbRepository = dbRepository;
        this.dbConnection = dbConnection;
    }

    // ─── USERS ────────────────────────────────────────────────────────────────

    /** @return the generated user ID */
    public int createUser(String username) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("CreateUser"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(DatabaseColumns.COL_ID);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("createUser failed", e);
        }
        throw new IllegalStateException("createUser returned no id");
    }

    /** @return empty Optional if no user with that username exists */
    public Optional<Integer> getUserIdByUsername(String username) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetUserByUsername"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt(DatabaseColumns.COL_ID));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserIdByUsername failed", e);
        }
        return Optional.empty();
    }

    // ─── USER BUGEMONS ────────────────────────────────────────────────────────

    public void saveUserBugemon(UserBugemonDTO dto) {
        LOG.debug("Saving user bugemon: {}", dto);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("SaveUserBugemon"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.bugemonName());
            ps.setInt(3, dto.currentDefense());
            ps.setInt(4, dto.currentAttackPower());
            ps.setInt(5, dto.currentInitiative());
            ps.setInt(6, dto.currentMaxHp());
            ps.setInt(7, dto.currentXp());
            ps.setInt(8, dto.currentLevel());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("saveUserBugemon failed", e);
        }
    }

    public void updateUserBugemon(UserBugemonDTO dto) {
        LOG.debug("Updating user bugemon: {}", dto);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("UpdateUserBugemon"))) {
            ps.setInt(1, dto.currentDefense());
            ps.setInt(2, dto.currentAttackPower());
            ps.setInt(3, dto.currentInitiative());
            ps.setInt(4, dto.currentMaxHp());
            ps.setInt(5, dto.currentXp());
            ps.setInt(6, dto.currentLevel());
            ps.setInt(7, dto.userId());
            ps.setString(8, dto.bugemonName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("updateUserBugemon failed", e);
        }
    }

    public List<UserBugemonDTO> getUserBugemons(int userId) {
        LOG.debug("Getting bugemons for userId: {}", userId);

        List<UserBugemonDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetUserBugemons"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserBugemonDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                        rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK),
                        rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                        rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserBugemons failed", e);
        }
        return result;
    }

    // ─── TEAMS ────────────────────────────────────────────────────────────────

    public void createTeam(int userId, String teamName) {
        LOG.debug("Creating team '{}' for userId: {}", teamName, userId);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("CreateTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createTeam failed", e);
        }
    }

    /** Deletes all members of the team, then the team record itself. */
    public void deleteTeam(int userId, String teamName) {
        LOG.debug("Deleting team '{}' for userId: {}", teamName, userId);

        try (PreparedStatement psMembers = this.dbConnection
                .prepareStatement(this.dbRepository.getSql("DeleteTeamMembers"));
                PreparedStatement psTeam = this.dbConnection.prepareStatement(this.dbRepository.getSql("DeleteTeam"))) {
            psMembers.setInt(1, userId);
            psMembers.setString(2, teamName);
            psMembers.executeUpdate();

            psTeam.setInt(1, userId);
            psTeam.setString(2, teamName);
            psTeam.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeam failed", e);
        }
    }

    public void deleteTeamMembers(int userId, String teamName) {
        LOG.debug("Deleting team members for userId: {} and teamName: {}", userId, teamName);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("DeleteTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeamMembers failed", e);
        }
    }

    public List<TeamDTO> getUserTeams(int userId) {
        LOG.debug("Getting teams for userId: {}", userId);

        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetUserTeams"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamDTO(rs.getInt(DatabaseColumns.COL_USER_ID), rs.getString(DatabaseColumns.COL_NAME)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserTeams failed", e);
        }
        return result;
    }

    // ─── TEAM MEMBERS ─────────────────────────────────────────────────────────

    /**
     * Add a member to a team in the database. This method takes a TeamMemberDTO object containing the details of the
     * team member to be added, including the user ID, team name, Bugemon ID, and slot position. It executes an SQL
     * statement to insert a new record into the database representing this team member, associating it with the
     * specified team and user. The method ensures that the new team member is correctly linked to the appropriate team
     * and user in the database. If an error occurs during the database operation, an IllegalStateException is thrown.
     *
     * @param dto
     *            the TeamMemberDTO object containing the details of the team member to be added
     */
    public void addTeamMember(TeamMemberDTO dto) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("AddTeamMember"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.teamName());
            ps.setString(3, dto.bugemonName());
            ps.setInt(4, dto.slotPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("addTeamMember failed", e);
        }
    }

    /**
     * Remove a member from a team in the database. This method takes the user ID, team name, and Bugemon ID as
     * parameters to identify the specific team member to be removed. It executes an SQL statement to delete the
     * corresponding record from the database, effectively removing the specified team member from the team. The method
     * ensures that only the team member matching the provided user ID, team name, and Bugemon ID is removed from the
     * database. If an error occurs during the database operation, an IllegalStateException is thrown.
     *
     * @param userId
     *            the ID of the user whose team member is to be removed
     * @param teamName
     *            the name of the team from which to remove the member
     * @param bugemonName
     *            the name of the Bugemon to be removed from the team
     */
    public void removeTeamMember(int userId, String teamName, String bugemonName) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("RemoveTeamMember"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("removeTeamMember failed", e);
        }
    }

    /**
     * Retrieve a list of TeamMemberDTO objects representing the members of a specific team for a user from the
     * database. This method executes an SQL query to search for all team member records associated with the given user
     * ID and team name, and constructs a list of TeamMemberDTO objects containing the details of each team member
     * found. The details include the user ID, team name, Bugemon ID, and slot position of each team member. If an error
     * occurs during the database query, an IllegalStateException is thrown.
     *
     * @param userId
     *            the ID of the user for whom to retrieve team members
     * @param teamName
     *            the name of the team for which to retrieve members
     * @return a list of TeamMemberDTO objects representing the members of the specified team for the user
     */
    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                        rs.getString(DatabaseColumns.COL_TEAM_NAME), rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getTeamMembers failed", e);
        }
        return result;
    }

    public void renameTeam(int userId, String oldTeamName, String newTeamName) {
        LOG.debug("Renaming team for userId: {} from '{}' to '{}'", userId, oldTeamName, newTeamName);

        try (PreparedStatement psRenameTeam = this.dbConnection
                .prepareStatement(this.dbRepository.getSql("RenameTeam"))) {
            psRenameTeam.setString(1, newTeamName);
            psRenameTeam.setInt(2, userId);
            psRenameTeam.setString(3, oldTeamName);
            psRenameTeam.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("renameTeam failed", e);
        }
    }
}
