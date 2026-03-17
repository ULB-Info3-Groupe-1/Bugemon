package ulb.repository;

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

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;
import ulb.utils.Parser;

public class DatabaseRepository {
    private static DatabaseRepository instance;

    private final DatabaseManager dbManager = new DatabaseManager();

    // Utility method to get a connection from the manager
    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    private DatabaseRepository() {
        // Load all SQL queries from files
        String[] sqlFiles = {"/sql/delete_tables.sql",
                             "/sql/create_schema.sql",
                             "/sql/queries_users.sql",
                             "/sql/queries_user_bugemons.sql",
                             "/sql/queries_teams.sql",
                             "/sql/check_db_status.sql",
                             "/sql/queries_save_default_game_data.sql",
                             "/sql/queries_default_bugemons.sql"};
        for (String file : sqlFiles) {
            loadQueriesFromFile(file);
        }
        prepareDatabase();
    }

    public static DatabaseRepository getInstance() {
        if (instance == null) {
            instance = new DatabaseRepository();
        }
        return instance;
    }

    private void loadQueriesFromFile(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new RuntimeException("SQL file not found: " + filePath);
            }
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            String currentQueryName = null;
            StringBuilder currentSql = new StringBuilder();

            // see rules.md for the format of the SQL files
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-- Query")) {
                    if (currentQueryName != null && !currentSql.isEmpty()) {
                        queries.put(currentQueryName, currentSql.toString().trim());
                        currentSql.setLength(0); // We reset the StringBuilder for the next query
                    }
                    currentQueryName = null;
                } else if (currentQueryName == null && line.startsWith("-- ")) {
                    currentQueryName = line.substring(3).trim();
                } else if (currentQueryName != null) {
                    currentSql.append(line).append("\n");
                }
            }
            // Don't forget to save the last query after the loop
            if (currentQueryName != null && !currentSql.isEmpty()) {
                queries.put(currentQueryName, currentSql.toString().trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading queries from " + filePath, e);
        }
    }

    //
    private String getSql(String queryName) {
        String sql = queries.get(queryName);
        if (sql == null) {
            throw new IllegalArgumentException("SQL query not found in Map : " + queryName);
        }
        return sql;
    }
    // ─── CREATION ─────────────────────────────────────────────────────────────

    public void createSchema() {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("CreateSchema"))) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("createSchema failed", e);
        }
    }

    // ─── USERS ────────────────────────────────────────────────────────────────

    public int createUser(String username) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("CreateUser"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
        throw new RuntimeException("createUser returned no id");
    }

    public Optional<Integer> getUserIdByUsername(String username) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserByUsername"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(rs.getInt("id"));
        } catch (SQLException e) {
            throw new RuntimeException("getUserIdByUsername failed", e);
        }
        return Optional.empty();
    }

    // ─── USER BUGEMONS ────────────────────────────────────────────────────────

    public void saveUserBugemon(UserBugemonDTO dto) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("SaveUserBugemon"))) {
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
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("UpdateUserBugemon"))) {
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
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserBugemons"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserBugemonDTO(
                        rs.getInt("user_id"), rs.getString("bugemon_id"),
                        rs.getInt("current_defense"), rs.getInt("current_attack_power"),
                        rs.getInt("current_initiative"), rs.getInt("current_max_hp"),
                        rs.getInt("current_xp"), rs.getInt("current_level")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getUserBugemons failed", e);
        }
        return result;
    }

    // ─── TEAMS ────────────────────────────────────────────────────────────────

    public void createTeam(int userId, String teamName) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("CreateTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("createTeam failed", e);
        }
    }

    public void deleteTeam(int userId, String teamName) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("DeleteTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteTeam failed", e);
        }
    }

    public List<TeamDTO> getUserTeams(int userId) {
        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserTeams"))) {
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
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("AddTeamMember"))) {
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
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("RemoveTeamMember"))) {
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
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt("user_id"), rs.getString("team_name"),
                                             rs.getString("bugemon_id"),
                                             rs.getInt("slot_position")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getTeamMembers failed", e);
        }
        return result;
    }

    // ──────── CHECK DATABASE TO SET GAME DATA IF NEEDED ─────────

    /**
     * Prepare the database by checking if the necessary tables and data are present. This method is
     * called during the initialization of the repository to ensure that the database is in a
     * consistent state before any operations are performed. It first checks if the critical tables
     * exist, and if not, it creates the schema and adds the default game data. Then it checks if
     * the static game data is present by looking for entries in the main tables (bugemons, attacks,
     * effects), and if they are empty, it adds the default game data. This ensures that the game
     * can function properly even if it's run on a fresh database without any pre-existing schema or
     * data.
     */
    private void prepareDatabase() {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("isTablesPresent"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // If at least one table is missing, we consider that the schema is not created and
                // we create it and add the default game data
                // TODO: remove magic number
                if (rs.getInt("existing_critical_tables") < 7) {
                    createSchema();
                    addDefaultGameData();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("isTablesPresent failed", e);
        }

        try (PreparedStatement ps = dbManager.prepareStatement(getSql("IsDataEmpty"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Check if the static game data is present by looking if there are data in the main
                // tables (bugemons, attacks, effects)
                if (rs.getInt("total_rows") == 0) {
                    addDefaultGameData();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("IsDataEmpty failed", e);
        }
    }

    /**
     * Add the default game data to the database. This method is used during the game initialization
     * to load the static data of the game. It uses a Parser to read the default game data from a
     * source (e.g., JSON files) and then saves this data to the database using helper methods for
     * attacks and Bugemons.
     */
    public void addDefaultGameData() {
        Parser parser = new Parser();
        parser.parse();
        saveGameDataAttacks(parser.getAttacks());
        saveGameDataBugemon(parser.getBugemons());
    }

    /**
     * Save the default attacks to the database. This method is used during the game initialization
     * to load the static data of the game. It takes a map of Attack objects and saves them to the
     * database. Each attack's effects are also saved as part of this process, ensuring that all
     * necessary data is present in the database.
     * @param attacks a map of Attack objects representing the default attacks to be saved in the
     *         database. The key of the map is the attack's ID, which is used as a reference when
     *         saving the Bugemons that use these attacks.
     */
    private void saveGameDataAttacks(Map<String, Attack> attacks) {
        for (Attack attack : attacks.values()) {
            try {
                // Begin by inserting the attack itself
                try (PreparedStatement psAttack =
                             dbManager.prepareStatement(getSql("SaveAttack"))) {
                    psAttack.setString(1, attack.id());
                    psAttack.setString(2, attack.name());
                    if (attack.type() != null) {
                        psAttack.setString(3, attack.type().name());
                    } else {
                        psAttack.setNull(3, Types.VARCHAR);
                    }
                    psAttack.setString(4, attack.description());
                    psAttack.setInt(5, attack.power());
                    psAttack.executeUpdate();
                }

                // Then insert its effects if it has any
                if (attack.effects() != null && !attack.effects().isEmpty()) {
                    try (PreparedStatement psEffect =
                                 dbManager.prepareStatement(getSql("SaveEffect"))) {
                        for (Effect effect : attack.effects()) {
                            psEffect.setString(1, attack.id()); // Foreign key to the attack
                            psEffect.setString(2, effect.getTypeEffect().name());
                            psEffect.setString(3, effect.getTarget().name());
                            if (effect.getStat() != null) {
                                psEffect.setString(4, effect.getStat().name());
                            } else {
                                psEffect.setNull(4, Types.VARCHAR);
                            }
                            psEffect.setInt(5, effect.getModifier());
                            psEffect.setString(6, effect.getDuration());
                            psEffect.addBatch();
                        }
                        psEffect.executeBatch();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error occurred while saving the default game data for attack: "
                                + attack.id(),
                        e);
            }
        }
    }

    /**
     * Save the default Bugemons to the database. This method is used during the game initialization
     * to load the static data of the game. It takes a list of Bugemon objects and saves them to the
     * database using a batch insert for efficiency. Each Bugemon's attacks are also saved as part
     * of this process, ensuring that all necessary data is present in the database.
     * @param bugemons a list of Bugemon objects representing the default Bugemons to be saved in
     *         the database. Each Bugemon should have its attacks already defined and linked by
     *         their IDs, as this method assumes that the attacks have been saved beforehand.
     */
    private void saveGameDataBugemon(List<Bugemon> bugemons) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("SaveBugemon"))) {
            for (Bugemon bugemon : bugemons) {
                ps.setString(1, bugemon.getId());
                ps.setString(2, bugemon.getName());
                ps.setString(3, bugemon.getType().name());
                ps.setString(4, bugemon.getSpriteURL());
                ps.setInt(5, bugemon.getDefense());
                ps.setInt(6, bugemon.getAttack());
                ps.setInt(7, bugemon.getInitiative());
                ps.setInt(8, bugemon.getMaxHp());
                ps.setBoolean(9, bugemon.isStarter());
                // Assuming each Bugemon has exactly 3 attacks, we insert them in the order they
                // appear in the list
                ps.setString(10, bugemon.getListAttacksId().get(0));
                ps.setString(11, bugemon.getListAttacksId().get(1));
                ps.setString(12, bugemon.getListAttacksId().get(2));
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("saveGameDataBugemon failed", e);
        }
    }

    // ─── UTILS FOR CLASS USING THIS REPO ──

    /**
     * Retrieve all default Bugemons from the database. This is useful for the game initialization
     * to load the static data of the game.
     * @return a list of Bugemon objects representing all the default Bugemons stored in the
     *         database.
     */
    public List<Bugemon> getAllDefaultBugemons() {
        List<Bugemon> result = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetAllDefaultBugemons"))) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BugemonBuilder builder = new BugemonBuilder();
                builder.id(rs.getString("id"))
                        .name(rs.getString("name"))
                        .type(BugemonType.valueOf(rs.getString("type")))
                        .sprite(rs.getString("sprite"))
                        .defense(rs.getInt("base_defense"))
                        .attack(rs.getInt("base_attack_power"))
                        .initiative(rs.getInt("base_initiative"))
                        .hp(rs.getInt("base_max_hp"))
                        .isStarter(rs.getBoolean("is_starter"));

                result.add(builder.build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("getAllDefaultBugemons failed", e);
        }
        return result;
    }

    // ─── CLEAR DATABASE METHOD NEEDED FOR THE TESTS ────

    /**
     * Clear the database by deleting all entries from all tables. This is useful for ensuring a
     * clean state before each test. Note: this method doesn't drop the tables, it just deletes the
     * data. The SQL for this is located in 00_delete_tables.sql and is executed at the beginning of
     * the test suite.
     */
    public void clearDatabase() {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("ClearDatabase"))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    /**
     * Activate the test mode of the DatabaseManager by providing a specific test database URL.
     * This allows the tests to run on a separate database instance, ensuring that the production
     * data remains unaffected.
     * @param testUrl the JDBC URL of the test database to connect to.
     */
    public void activateTestMode(String testUrl) {
        dbManager.setTestMode(testUrl);
    }

    public Connection getConnection() {
        return dbManager.getConnection();
    }
}
