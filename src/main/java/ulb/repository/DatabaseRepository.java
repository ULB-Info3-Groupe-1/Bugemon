package ulb.repository;

import java.io.BufferedReader;
import java.io.IOException;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import ulb.models.bugemon.Bugemon;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class DatabaseRepository {
    // Number of tables of the critical schema
    private static final int CRITICAL_TABLES_COUNT = 7;

    private final DatabaseConnection dbConnection;

    private final UserRepository userRepository;
    private final StaticDataRepository staticDataRepository;

    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    /** Loads SQL queries, creates the schema if absent, and bootstraps static game data. */
    public DatabaseRepository() {
        this.dbConnection = new DatabaseConnection();
        this.userRepository = new UserRepository(this, this.dbConnection);
        this.staticDataRepository = new StaticDataRepository(this, this.dbConnection);

        this.loadSQLQueries();
        this.prepareDatabase();
    }

    private void loadSQLQueries() {
        List<String> sqlFiles = this.getSqlFiles();
        for (String file : sqlFiles) {
            this.loadQueriesFromFile(file);
        }
    }

    /**
     * Parses a SQL file and populates {@code queries}. See {@code team/rules.md} for the required file format
     * ({@code -- Query ...} / {@code -- QueryName} / SQL body).
     */
    private void loadQueriesFromFile(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("SQL file not found: " + filePath);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            String currentQueryName = null;
            StringBuilder currentSql = new StringBuilder();

            // see rules.md for the format of the SQL files
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-- Query")) {
                    if (currentQueryName != null && !currentSql.isEmpty()) {
                        this.queries.put(currentQueryName, currentSql.toString().trim());
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
                this.queries.put(currentQueryName, currentSql.toString().trim());
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
                throw new IllegalStateException("SQL directory not found");
            }
            URI uri = url.toURI();
            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fs = this.getOrCreateFileSystem(uri)) {
                    this.walkAndAddFiles(fs.getPath("/sql"), result);
                }
            } else {
                this.walkAndAddFiles(Paths.get(uri), result);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error loading SQL files", e);
        }
        return result;
    }

    /** Handles both regular filesystem and JAR filesystem for resource loading. */
    private FileSystem getOrCreateFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private void walkAndAddFiles(Path path, List<String> result) throws IOException {
        try (Stream<Path> walk = Files.walk(path, 1)) {
            walk.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(p -> result.add("/sql/" + p.getFileName().toString()));
        }
    }

    /**
     * Returns the SQL string for the given query name.
     *
     * @throws IllegalArgumentException
     *             if the query name is not found
     */
    public String getSql(String queryName) {
        String sql = this.queries.get(queryName);
        if (sql == null) {
            throw new IllegalArgumentException("SQL query not found in Map : " + queryName);
        }
        return sql;
    }

    private void createSchema() {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("CreateSchema"))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createSchema failed", e);
        }
    }

    private void prepareDatabase() {
        // Verify if the critical tables exist in the database. If not, we create the schema and add
        // the default game data
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("isTablesPresent"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("existing_critical_tables") < CRITICAL_TABLES_COUNT) {
                this.createSchema();
                this.staticDataRepository.addDefaultGameData();
                return;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("isTablesPresent failed", e);
        }

        // If the tables exist, we check if they contain the static game data. If not, we add the
        // static game data
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("IsDataEmpty"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("total_rows") == 0) {
                this.staticDataRepository.addDefaultGameData();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("IsDataEmpty failed", e);
        }
    }

    // ─── GETTERS ───

    public List<Bugemon> getAllDefaultBugemons() {
        return this.staticDataRepository.getAllDefaultBugemons();
    }

    public List<UserBugemonDTO> getUserBugemons(int userId) {
        return this.userRepository.getUserBugemons(userId);
    }

    /** @return empty Optional if no user with that username exists */
    public Optional<Integer> getUserIdByUsername(String username) {
        return this.userRepository.getUserIdByUsername(username);
    }

    public List<TeamDTO> getUserTeams(int userId) {
        return this.userRepository.getUserTeams(userId);
    }

    public List<TeamMemberDTO> getTeamMembers(int userId, String teamName) {
        return this.userRepository.getTeamMembers(userId, teamName);
    }

    // ─── ACTIONS ───

    public int createUser(String username) {
        return this.userRepository.createUser(username);
    }

    public void createTeam(int userId, String teamName) {
        this.userRepository.createTeam(userId, teamName);
    }

    public void saveUserBugemon(UserBugemonDTO dto) {
        this.userRepository.saveUserBugemon(dto);
    }

    public void updateUserBugemon(UserBugemonDTO dto) {
        this.userRepository.updateUserBugemon(dto);
    }

    public void addTeamMember(TeamMemberDTO dto) {
        this.userRepository.addTeamMember(dto);
    }

    public void renameTeam(int userId, String oldTeamName, String newTeamName) {
        this.userRepository.renameTeam(userId, oldTeamName, newTeamName);
    }

    /**
     * Remove a member from a team in the database. This method takes the user ID, team name, and bugemon ID of the team
     * member to be removed, and deletes the corresponding entry from the database to reflect that this team member is
     * no longer part of the specified team.
     *
     * @param userId
     *            the ID of the user who is a member of the team from which to remove the member
     * @param teamName
     *            the name of the team from which to remove the member
     * @param bugemonName
     *            the name of the bugemon that represents the team member to be removed from the specified team in the
     *            database
     */
    public void removeTeamMember(int userId, String teamName, String bugemonName) {
        this.userRepository.removeTeamMember(userId, teamName, bugemonName);
    }

    public void deleteTeam(int userId, String teamName) {
        this.userRepository.deleteTeam(userId, teamName);
    }

    public void deleteTeamMembers(int userId, String teamName) {
        this.userRepository.deleteTeamMembers(userId, teamName);
    }
}
