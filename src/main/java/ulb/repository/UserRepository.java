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
            ps.setInt(2, dto.bugemonId());
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
            ps.setInt(8, dto.bugemonId());
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
                        rs.getInt(DatabaseColumns.COL_BUGEMON_ID), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK_POWER),
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

    // ─── TEAM MEMBERS ─────────────────────────────────────────────────────────

    public void addTeamMember(TeamMemberDTO dto) {
        LOG.debug("Adding team member: {}", dto);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("AddTeamMember"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.teamName());
            ps.setString(3, dto.bugemonId());
            ps.setInt(4, dto.slotPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("addTeamMember failed", e);
        }
    }

    public void removeTeamMember(int userId, String teamName, String bugemonId) {
        LOG.debug("Removing team member for userId: {} and teamName: {}", userId, teamName);

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("RemoveTeamMember"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("removeTeamMember failed", e);
        }
    }

    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        LOG.debug("Getting team members for userId: {} and teamName: {}", userId, teamName);

        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt(DatabaseColumns.COL_USER_ID),
                        rs.getString(DatabaseColumns.COL_TEAM_NAME), rs.getString(DatabaseColumns.COL_BUGEMON_ID),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getTeamMembers failed", e);
        }
        return result;
    }
}
