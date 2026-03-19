package ulb.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon.effect.EffectType;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;
import ulb.utils.DatabaseHelper;
import ulb.utils.Parser;

public class DatabaseRepository {
    // Number of tables of the critical schema
    private static final int CRITICAL_TABLES_COUNT = 7;

    private static final String COL_USER_ID = "user_id";
    private static final String COL_BUGEMON_ID = "bugemon_id";
    private static final String COL_CURRENT_DEFENSE = "current_defense";
    private static final String COL_CURRENT_ATTACK_POWER = "current_attack_power";
    private static final String COL_CURRENT_INITIATIVE = "current_initiative";
    private static final String COL_CURRENT_MAX_HP = "current_max_hp";
    private static final String COL_CURRENT_XP = "current_xp";
    private static final String COL_CURRENT_LEVEL = "current_level";
    private static final String COL_TEAM_NAME = "team_name";
    private static final String COL_SLOT_POSITION = "slot_position";
    private static final String COL_NAME = "name";

    private final DatabaseManager dbManager;

    // Utility method to get a connection from the manager
    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    public DatabaseRepository() {
        this.dbManager = new DatabaseManager();
        loadSQLQueries();
        prepareDatabase();
    }

    public DatabaseRepository(String testUrl) {
        this.dbManager = new DatabaseManager(testUrl);
        loadSQLQueries();
        prepareDatabase();
    }

    private void loadSQLQueries() {
        // Load all SQL queries from files
        List<String> sqlFiles = getSqlFiles();
        for (String file : sqlFiles) {
            loadQueriesFromFile(file);
        }
    }

    private void loadQueriesFromFile(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("SQL file not found: " + filePath);
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
            throw new IllegalStateException("Error loading queries from " + filePath, e);
        }
    }

    private List<String> getSqlFiles() {
        List<String> result = new ArrayList<>();
        try {
            URL url = getClass().getResource("/sql/");
            if (url == null) {
                throw new RuntimeException("SQL directory not found");
            }
            URI uri = url.toURI();
            Path path;

            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                }
                path = fileSystem.getPath("/sql");
            } else {
                path = Paths.get(uri);
            }
            try (Stream<Path> walk = Files.walk(path, 1)) {
                walk.filter(p -> p.toString().endsWith(".sql"))
                        .forEach(p -> result.add("/sql/" + p.getFileName().toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading SQL files", e);
        }
        return result;
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
        try (Statement st = dbManager.getConnectionObject().createStatement()) {
            st.executeUpdate(getSql("CreateSchema"));
        } catch (SQLException e) {
            throw new IllegalStateException("createSchema failed", e);
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
            throw new IllegalStateException("createUser failed", e);
        }
        throw new IllegalStateException("createUser returned no id");
    }

    public Optional<Integer> getUserIdByUsername(String username) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserByUsername"))) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(rs.getInt("id"));
        } catch (SQLException e) {
            throw new IllegalStateException("getUserIdByUsername failed", e);
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
            throw new IllegalStateException("saveUserBugemon failed", e);
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
            throw new IllegalStateException("updateUserBugemon failed", e);
        }
    }

    public List<UserBugemonDTO> getUserBugemons(int userId) {
        List<UserBugemonDTO> result = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserBugemons"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserBugemonDTO(
                        rs.getInt(COL_USER_ID), rs.getString(COL_BUGEMON_ID),
                        rs.getInt(COL_CURRENT_DEFENSE), rs.getInt(COL_CURRENT_ATTACK_POWER),
                        rs.getInt(COL_CURRENT_INITIATIVE), rs.getInt(COL_CURRENT_MAX_HP),
                        rs.getInt(COL_CURRENT_XP), rs.getInt(COL_CURRENT_LEVEL)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserBugemons failed", e);
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
            throw new IllegalStateException("createTeam failed", e);
        }
    }

    public void deleteTeam(int userId, String teamName) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("DeleteTeam"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("deleteTeam failed", e);
        }
    }

    public List<TeamDTO> getUserTeams(int userId) {
        List<TeamDTO> result = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetUserTeams"))) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamDTO(rs.getInt(COL_USER_ID), rs.getString(COL_NAME)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getUserTeams failed", e);
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
            throw new IllegalStateException("addTeamMember failed", e);
        }
    }

    public void removeTeamMember(int userId, String teamName, String bugemonId) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("RemoveTeamMember"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ps.setString(3, bugemonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("removeTeamMember failed", e);
        }
    }

    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        List<TeamMemberDTO> result = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetTeamMembers"))) {
            ps.setInt(1, userId);
            ps.setString(2, teamName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TeamMemberDTO(rs.getInt(COL_USER_ID), rs.getString(COL_TEAM_NAME),
                                             rs.getString(COL_BUGEMON_ID),
                                             rs.getInt(COL_SLOT_POSITION)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getTeamMembers failed", e);
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
        // Verify if the critical tables exist in the database. If not, we create the schema and add
        // the default game data
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("isTablesPresent"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("existing_critical_tables") < CRITICAL_TABLES_COUNT) {
                createSchema();
                addDefaultGameData();
                return;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("isTablesPresent failed", e);
        }

        // If the tables exist, we check if they contain the static game data. If not, we add the
        // static game data
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("IsDataEmpty"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("total_rows") == 0) {
                addDefaultGameData();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("IsDataEmpty failed", e);
        }
    }

    /**
     * Add the default game data to the database. This method is used during the game initialization
     * to load the static data of the game. It uses a Parser to read the default game data from a
     * source (e.g., JSON files) and then saves this data to the database using helper methods for
     * attacks and Bugemons.
     */
    private void addDefaultGameData() {
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
                    psAttack.setObject(3, attack.type() != null ? attack.type().name() : null,
                                       Types.VARCHAR);
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
                            psEffect.setObject(
                                    4, effect.getStat() != null ? effect.getStat().name() : null,
                                    Types.VARCHAR);
                            psEffect.setInt(5, effect.getModifier());
                            psEffect.setString(6, effect.getDuration());
                            psEffect.addBatch();
                        }
                        psEffect.executeBatch();
                    }
                }
            } catch (SQLException e) {
                throw new IllegalStateException(
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
            throw new IllegalStateException("saveGameDataBugemon failed", e);
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
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, "type", BugemonType.class);
                Attack attack1 = getAttackById(rs.getString("attack_1_id"));
                Attack attack2 = getAttackById(rs.getString("attack_2_id"));
                Attack attack3 = getAttackById(rs.getString("attack_3_id"));
                BugemonBuilder builder = new BugemonBuilder();
                builder.id(rs.getString("id"))
                        .name(rs.getString("name"))
                        .type(type)
                        .sprite(rs.getString("sprite"))
                        .defense(rs.getInt("base_defense"))
                        .attack(rs.getInt("base_attack_power"))
                        .initiative(rs.getInt("base_initiative"))
                        .hp(rs.getInt("base_max_hp"))
                        .addAttack(attack1)
                        .addAttack(attack2)
                        .addAttack(attack3)
                        .isStarter(rs.getBoolean("is_starter"));

                result.add(builder.build());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getAllDefaultBugemons failed", e);
        }
        return result;
    }

    public Attack getAttackById(String attackId) {
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetAttackById"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<Effect> effects = getEffectByAttackId(attackId);
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, "type", BugemonType.class);
                return new Attack(rs.getString("id"), rs.getString("name"), type,
                                  rs.getString("description"), rs.getInt("power"), effects);
            }
        } catch (SQLException e) {
            throw new RuntimeException("getAttackById failed for id: " + attackId, e);
        }
        throw new RuntimeException("Attack not found for id: " + attackId);
    }

    public List<Effect> getEffectByAttackId(String attackId) {
        List<Effect> effects = new ArrayList<>();
        try (PreparedStatement ps = dbManager.prepareStatement(getSql("GetEffectByAttackId"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EffectType type = DatabaseHelper.getEnumOrNull(rs, "type", EffectType.class);
                EffectTarget target =
                        DatabaseHelper.getEnumOrNull(rs, "target", EffectTarget.class);
                EffectStat stat = DatabaseHelper.getEnumOrNull(rs, "stat", EffectStat.class);
                String duration =
                        (rs.getString("duration") != null) ? rs.getString("duration") : "0_tour";

                effects.add(new Effect(type, target, stat, rs.getInt("modifier"), duration));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getEffectByAttackId failed for attack id: " + attackId, e);
        }
        return effects;
    }

    // ─── CLEAR DATABASE METHOD NEEDED FOR THE TESTS ────

    /**
     * Check if the database connection is currently active.
     * @return true if the connection is active, false otherwise.
     */
    public boolean isConnected() {
        return this.dbManager.isConnected();

    }

    public void clearDatabase() {
        try (Statement st = dbManager.getConnectionObject().createStatement()) {
            st.executeUpdate(getSql("ClearDatabase"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    /**
     * Provides direct access to the underlying Connection object for advanced operations.
     * @return The active Connection object from the DatabaseManager, allowing for direct SQL
     *         operations if needed.
     */
    public Connection getConnection() {
        return this.dbManager.getConnectionObject();
    }
}
