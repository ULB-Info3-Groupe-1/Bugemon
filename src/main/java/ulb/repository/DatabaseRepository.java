package ulb.repository;

import ulb.repository.dto.UserBugemonDTO;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseRepository {

    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    public DatabaseRepository() {
        // Load all SQL queries from files
        String[] sqlFiles = {
            "/sql/00_delete_tables.sql",
            "/sql/01_create_schema.sql",
            "/sql/02_queries_users.sql",
            "/sql/03_queries_user_bugemons.sql",
            "/sql/04_queries_teams.sql"
        };
        for (String file : sqlFiles) {
            loadQueriesFromFile(file);
        }
    }

    private void loadQueriesFromFile(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new RuntimeException("SQL file not found: " + filePath);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            String currentQueryName = null;
            StringBuilder currentSql = new StringBuilder();

            // see rules.md for the format of the SQL files
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-- Query")) {
                    if (currentQueryName != null && !currentSql.isEmpty()) {
                        queries.put(currentQueryName, currentSql.toString().trim());
                        currentSql.setLength(0); // On vide le buffer
                    }
                    currentQueryName = null;
                } else if (currentQueryName == null && line.startsWith("-- ")) {
                    currentQueryName = line.substring(3).trim();
                } else if (currentQueryName != null) {
                    currentSql.append(line).append("\n");
                } else {
                    // Ignorer les lignes qui ne font pas partie d'une requête
                }
            }
            // Ne pas oublier d'enregistrer la toute dernière requête du fichier
            if (currentQueryName != null && !currentSql.isEmpty()) {
                queries.put(currentQueryName, currentSql.toString().trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des requêtes depuis " + filePath, e);
        }
    }

    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    // 
    private String getSql(String queryName) {
        String sql = queries.get(queryName);
        if (sql == null) {
            throw new IllegalArgumentException("Requête SQL introuvable dans la Map : " + queryName);
        }
        return sql;
    }
    // ─── CREATION ─────────────────────────────────────────────────────────────

    public void createSchema() {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("CreateSchema"))) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("createSchema failed", e);
        }
    }

    // ─── USERS ────────────────────────────────────────────────────────────────

    public int createUser(String username) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("CreateUser"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
        throw new RuntimeException("createUser returned no id");
    }

    public Optional<Integer> getUserIdByUsername(String username) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("GetUserByUsername"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(rs.getInt("id"));
        } catch (SQLException e) {
            throw new RuntimeException("getUserIdByUsername failed", e);
        }
        return Optional.empty();
    }

    // ─── USER BUGEMONS ────────────────────────────────────────────────────────

    public void saveUserBugemon(UserBugemonDTO dto) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("SaveUserBugemon"))) {
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
            throw new RuntimeException("saveUserBugemon failed", e);
        }
    }

    public void updateUserBugemon(UserBugemonDTO dto) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("UpdateUserBugemon"))) {
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
            throw new RuntimeException("updateUserBugemon failed", e);
        }
    }

    public List<UserBugemonDTO> getUserBugemons(int userId) {
        List<UserBugemonDTO> result = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(getSql("GetUserBugemons"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserBugemonDTO(
                    rs.getInt("user_id"),
                    rs.getString("bugemon_id"),
                    rs.getInt("current_defense"),
                    rs.getInt("current_attack_power"),
                    rs.getInt("current_initiative"),
                    rs.getInt("current_max_hp"),
                    rs.getInt("current_xp"),
                    rs.getInt("current_level")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getUserBugemons failed", e);
        }
        return result;
    }

    // ─── TEAMS ────────────────────────────────────────────────────────────────

    public void createTeam(int userId, String teamName) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("CreateTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("createTeam failed", e);
        }
    }

    public void deleteTeam(int userId, String teamName) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("DeleteTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteTeam failed", e);
        }
    }

    public List<TeamDTO> getUserTeams(int userId) {
        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(getSql("GetUserTeams"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamDTO(rs.getInt("user_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getUserTeams failed", e);
        }
        return result;
    }

    // ─── TEAM MEMBERS ─────────────────────────────────────────────────────────

    public void addTeamMember(TeamMemberDTO dto) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("AddTeamMember"))) {
            ps.setInt(1, dto.userId());
            ps.setString(2, dto.teamName());
            ps.setString(3, dto.bugemonId());
            ps.setInt(4, dto.slotPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("addTeamMember failed", e);
        }
    }

    public void removeTeamMember(int userId, String teamName, String bugemonId) {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("RemoveTeamMember"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("removeTeamMember failed", e);
        }
    }

    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(
                    rs.getInt("user_id"),
                    rs.getString("team_name"),
                    rs.getString("bugemon_id"),
                    rs.getInt("slot_position")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getTeamMembers failed", e);
        }
        return result;
    }

    // ─── CLEAR DATABASE METHOD NEEDED FOR THE TESTS ────

    /**
     * Clear the database by deleting all entries from all tables. This is useful for ensuring a clean state before each test.
     * Note: this method doesn't drop the tables, it just deletes the data. The SQL for this is located in 00_delete_tables.sql 
     * and is executed at the beginning of the test suite.
     */
    public void clearDatabase() {
        try (PreparedStatement ps = getConn().prepareStatement(getSql("ClearDatabase"))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }
}
