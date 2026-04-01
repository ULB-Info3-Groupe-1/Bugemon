package ulb.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class UserRepository {
    private final DatabaseRepository dbRepository;

    private final DatabaseConnection dbConnection;

    public UserRepository(DatabaseRepository dbRepository, DatabaseConnection dbConnection) {
        this.dbRepository = dbRepository;
        this.dbConnection = dbConnection;
    }

    // ─── USERS ────────────────────────────────────────────────────────────────

    /**
     * Create a new user in the database with the specified username. This method executes an SQL
     * statement to insert a new user record into the database and retrieves the generated user ID
     * for the newly created user.
     * @param username the username of the new user
     * @return the ID of the newly created user
     */
    public int createUser(String username) {
        try (PreparedStatement ps =
                     this.dbConnection.prepareStatement(this.dbRepository.getSql("CreateUser"))) {
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

    /**
     * Retrieve the user ID associated with the specified username from the database. This method
     * executes an SQL query to search for a user record with the given username and returns the
     * corresponding user ID if found. If no user is found with the specified username, an empty
     * Optional is returned.
     * @param username the username for which to retrieve the user ID. This should correspond to a
     *         user that has been previously created in the database.
     * @return an Optional containing the user ID associated with the specified username if found,
     *         or an empty Optional if no user is found with that username. If an error occurs
     *         during the database query, an IllegalStateException is thrown.
     */
    public Optional<Integer> getUserIdByUsername(String username) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("GetUserByUsername"))) {
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

    /**
     * Save a UserBugemon to the database. This method takes a UserBugemonDTO object containing the
     * details of the UserBugemon to be saved and executes an SQL statement to insert a new record
     * into the database representing this UserBugemon. The details of the UserBugemon, such as its
     * current stats and level, are included in the DTO and are used to populate the corresponding
     * fields in the database record.
     * @param dto the UserBugemonDTO object containing the details of the UserBugemon to be saved to
     *         the database. This DTO should include the user ID, Bugemon ID, and the current stats
     *         and level of the UserBugemon. The method will use this information to create a new
     *         record in the database representing this UserBugemon. If an error occurs during the
     *         database operation, an IllegalStateException is thrown.
     */
    public void saveUserBugemon(UserBugemonDTO dto) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("SaveUserBugemon"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.bugemonId());
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

    /**
     * Update an existing UserBugemon in the database. This method takes a UserBugemonDTO object
     * containing the updated details of the UserBugemon and executes an SQL statement to update the
     * corresponding record in the database. The method identifies the UserBugemon to be updated
     * based on the user ID and Bugemon ID provided in the DTO, and updates the current stats and
     * level of the UserBugemon in the database with the new values from the DTO. If an error occurs
     * during the database operation, an IllegalStateException is thrown.
     * @param dto the UserBugemonDTO object containing the updated details of the UserBugemon to be
     *         updated in the database. This DTO should include the user ID, Bugemon ID, and the new
     *         current stats and level of the UserBugemon. The method will use this information to
     *         identify the existing record in the database representing this UserBugemon and update
     *         it with the new values. If an error occurs during the database operation, an
     *         IllegalStateException is thrown.
     */
    public void updateUserBugemon(UserBugemonDTO dto) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("UpdateUserBugemon"))) {
            ps.setInt(1, dto.currentDefense());
            ps.setInt(2, dto.currentAttackPower());
            ps.setInt(3, dto.currentInitiative());
            ps.setInt(4, dto.currentMaxHp());
            ps.setInt(5, dto.currentXp());
            ps.setInt(6, dto.currentLevel());
            ps.setInt(7, dto.userId());
            ps.setString(8, dto.bugemonId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("updateUserBugemon failed", e);
        }
    }

    /**
     * Retrieve a list of UserBugemonDTO objects representing the UserBugemons owned by a specific
     * user from the database. This method executes an SQL query to search for all UserBugemon
     * records associated with the given user ID and constructs a list of UserBugemonDTO objects
     * containing the details of each UserBugemon found. The details include the Bugemon ID, current
     * stats, and level of each UserBugemon. If an error occurs during the database query, an
     * IllegalStateException is thrown.
     * @param userId the ID of the user for whom to retrieve Bugemons
     * @return a list of UserBugemonDTO objects representing the UserBugemons owned by the specified
     *         user
     */
    public List<UserBugemonDTO> getUserBugemons(int userId) {
        List<UserBugemonDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("GetUserBugemons"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserBugemonDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                                              rs.getString(DatabaseColumns.COL_BUGEMON_ID),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK_POWER),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                                              rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserBugemons failed", e);
        }
        return result;
    }

    // ─── TEAMS ────────────────────────────────────────────────────────────────

    /**
     * Create a new team for a user in the database. This method takes the user ID and the name of
     * the team to be created, and executes an SQL statement to insert a new team record into the
     * database associated with the specified user. The team name is provided as a parameter, and
     * the method ensures that the new team is linked to the correct user in the database. If an
     * error occurs during the database operation, an IllegalStateException is thrown.
     * @param userId the ID of the user for whom to create a team
     * @param teamName the name of the team to be created
     */
    public void createTeam(int userId, String teamName) {
        try (PreparedStatement ps =
                     this.dbConnection.prepareStatement(this.dbRepository.getSql("CreateTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createTeam failed", e);
        }
    }

    /**
     * Delete an existing team for a user from the database. This method takes the user ID and the
     * name of the team to be deleted, and executes an SQL statement to remove the corresponding
     * team record from the database. The method identifies the team to be deleted based on the
     * provided user ID and team name, ensuring that only the specified team associated with the
     * correct user is removed from the database. If an error occurs during the database operation,
     * an IllegalStateException is thrown.
     * @param userId the ID of the user for whom to delete a team
     * @param teamName the name of the team to be deleted
     */
    public void deleteTeamMembers(int userId, String teamName) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("DeleteTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeamMembers failed", e);
        }
    }

    public void deleteTeam(int userId, String teamName) {
        try (PreparedStatement psMembers = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("DeleteTeamMembers"));
             PreparedStatement psTeam =
                     this.dbConnection.prepareStatement(this.dbRepository.getSql("DeleteTeam"))) {
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

    /**
     * Retrieve a list of TeamDTO objects representing the teams owned by a specific user from the
     * database. This method executes an SQL query to search for all team records associated with
     * the given user ID and constructs a list of TeamDTO objects containing the details of each
     * team found. The details include the user ID and the name of each team. If an error occurs
     * during the database query, an IllegalStateException is thrown.
     * @param userId the ID of the user for whom to retrieve teams
     * @return a list of TeamDTO objects representing the teams owned by the specified user
     */
    public List<TeamDTO> getUserTeams(int userId) {
        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps =
                     this.dbConnection.prepareStatement(this.dbRepository.getSql("GetUserTeams"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                                       rs.getString(DatabaseColumns.COL_NAME)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserTeams failed", e);
        }
        return result;
    }

    // ─── TEAM MEMBERS ─────────────────────────────────────────────────────────

    /**
     * Add a member to a team in the database. This method takes a TeamMemberDTO object containing
     * the details of the team member to be added, including the user ID, team name, Bugemon ID, and
     * slot position. It executes an SQL statement to insert a new record into the database
     * representing this team member, associating it with the specified team and user. The method
     * ensures that the new team member is correctly linked to the appropriate team and user in the
     * database. If an error occurs during the database operation, an IllegalStateException is
     * thrown.
     * @param dto the TeamMemberDTO object containing the details of the team member to be added
     */
    public void addTeamMember(TeamMemberDTO dto) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("AddTeamMember"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.teamName());
            ps.setString(3, dto.bugemonId());
            ps.setInt(4, dto.slotPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("addTeamMember failed", e);
        }
    }

    /**
     * Remove a member from a team in the database. This method takes the user ID, team name, and
     * Bugemon ID as parameters to identify the specific team member to be removed. It executes an
     * SQL statement to delete the corresponding record from the database, effectively removing the
     * specified team member from the team. The method ensures that only the team member matching
     * the provided user ID, team name, and Bugemon ID is removed from the database. If an error
     * occurs during the database operation, an IllegalStateException is thrown.
     * @param userId the ID of the user whose team member is to be removed
     * @param teamName the name of the team from which to remove the member
     * @param bugemonId the ID of the Bugemon to be removed from the team
     */
    public void removeTeamMember(int userId, String teamName, String bugemonId) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("RemoveTeamMember"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("removeTeamMember failed", e);
        }
    }

    /**
     * Retrieve a list of TeamMemberDTO objects representing the members of a specific team for a
     * user from the database. This method executes an SQL query to search for all team member
     * records associated with the given user ID and team name, and constructs a list of
     * TeamMemberDTO objects containing the details of each team member found. The details include
     * the user ID, team name, Bugemon ID, and slot position of each team member. If an error occurs
     * during the database query, an IllegalStateException is thrown.
     * @param userId the ID of the user for whom to retrieve team members
     * @param teamName the name of the team for which to retrieve members
     * @return a list of TeamMemberDTO objects representing the members of the specified team for
     *         the user
     */
    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(
                     this.dbRepository.getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                                             rs.getString(DatabaseColumns.COL_TEAM_NAME),
                                             rs.getString(DatabaseColumns.COL_BUGEMON_ID),
                                             rs.getInt(DatabaseColumns.COL_SLOT_POSITION)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getTeamMembers failed", e);
        }
        return result;
    }

    public void renameTeam(int userId, String oldTeamName, String newTeamName) {
        try (PreparedStatement psRenameTeam =
                     this.dbConnection.prepareStatement(this.dbRepository.getSql("RenameTeam"))) {
            psRenameTeam.setString(1, newTeamName);
            psRenameTeam.setInt(2, userId);
            psRenameTeam.setString(3, oldTeamName);
            psRenameTeam.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("renameTeam failed", e);
        }
    }
}
