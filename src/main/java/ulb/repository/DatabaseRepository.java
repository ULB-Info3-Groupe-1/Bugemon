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

    // Utility method to get a connection from the manager
    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    public DatabaseRepository() {
        this.dbConnection = new DatabaseConnection();
        this.userRepository = new UserRepository(this, this.dbConnection);
        this.staticDataRepository = new StaticDataRepository(this, this.dbConnection);

        this.loadSQLQueries();
        this.prepareDatabase();
    }

    /**
     * Load all SQL queries from files
     */
    private void loadSQLQueries() {
        List<String> sqlFiles = this.getSqlFiles();
        for (String file : sqlFiles) {
            this.loadQueriesFromFile(file);
        }
    }

    /**
     * Load SQL queries from a given file path and store them in the queries Map.
     *
     * @param filePath
     *            the path to the SQL file from which to load the queries, relative to the classpath (e.g.,
     *            "/sql/queries.sql")
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

    /**
     * Helper method to retrieve the list of SQL file paths from the resources.
     *
     * @return a List of Strings representing the paths to the SQL files, relative to the classpath (e.g.,
     *         "/sql/queries.sql")
     */
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

    /**
     * Helper method to get or create a FileSystem for a given URI. This is necessary to read files from a JAR file, as
     * the default FileSystem does not support the "jar" scheme.
     *
     * @param uri
     *            the URI of the resource for which to get or create a FileSystem
     * @return FileSystem that can be used to access the resource
     * @throws IOException
     *             if an I/O error occurs while creating the FileSystem
     */
    private FileSystem getOrCreateFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    /**
     * Helper method to walk through a directory and add all SQL files to the result list.
     *
     * @param path
     *            the starting path to walk through
     * @param result
     *            the list to which the paths of found SQL files will be added
     * @throws IOException
     *             if an I/O error occurs while walking the file tree
     */
    private void walkAndAddFiles(Path path, List<String> result) throws IOException {
        try (Stream<Path> walk = Files.walk(path, 1)) {
            walk.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(p -> result.add("/sql/" + p.getFileName().toString()));
        }
    }

    /**
     * Helper method to retrieve the SQL query string associated with a given query name.
     *
     * @param queryName
     *            the name of the query to retrieve, as defined in the SQL files
     * @return the SQL query string associated with the given query name
     */
    public String getSql(String queryName) {
        String sql = this.queries.get(queryName);
        if (sql == null) {
            throw new IllegalArgumentException("SQL query not found in Map : " + queryName);
        }
        return sql;
    }

    // ─── CREATION ─────────────────────────────────────────────────────────────

    /**
     * Create the database schema by executing the SQL query associated with the "CreateSchema" key.
     */
    private void createSchema() {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("CreateSchema"))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createSchema failed", e);
        }
    }

    // ──────── CHECK DATABASE TO SET GAME DATA IF NEEDED ─────────

    /**
     * Prepare the database by checking if the necessary tables and data are present. This method is called during the
     * initialization of the repository to ensure that the database is in a consistent state before any operations are
     * performed. It first checks if the critical tables exist, and if not, it creates the schema and adds the default
     * game data. Then it checks if the static game data is present by looking for entries in the main tables (bugemons,
     * attacks, effects), and if they are empty, it adds the default game data. This ensures that the game can function
     * properly even if it's run on a fresh database without any pre-existing schema or data.
     */
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

    /**
     * Get all default bugemons from the database. This method retrieves the list of all default bugemons that are
     * available in the game, which are stored in the database.
     *
     * @return a List of Bugemon objects representing all the default bugemons available in the game
     */
    public List<Bugemon> getAllDefaultBugemons() {
        return this.staticDataRepository.getAllDefaultBugemons();
    }

    /**
     * Get the list of UserBugemonDTO objects representing the bugemons owned by a specific user, identified by their
     * userId.
     *
     * @param userId
     *            the ID of the user for whom to retrieve the owned bugemons
     * @return a List of UserBugemonDTO objects representing the bugemons owned by the specified user
     */
    public List<UserBugemonDTO> getUserBugemons(int userId) {
        return this.userRepository.getUserBugemons(userId);
    }

    /**
     * Get the user ID associated with a given username.
     *
     * @param username
     *            the username for which to retrieve the user ID
     * @return an Optional containing the user ID if a user with the specified username exists, or an empty Optional if
     *         no such user exists
     */
    public Optional<Integer> getUserIdByUsername(String username) {
        return this.userRepository.getUserIdByUsername(username);
    }

    /**
     * Get the list of TeamDTO objects representing the teams owned by a specific user, identified by their userId.
     *
     * @param userId
     *            the ID of the user for whom to retrieve the owned teams
     * @return a List of TeamDTO objects representing the teams owned by the specified user
     */
    public List<TeamDTO> getUserTeams(int userId) {
        return this.userRepository.getUserTeams(userId);
    }

    /**
     * Get the list of TeamMemberDTO objects representing the members of a specific team, identified by the teamId and
     * teamName.
     *
     * @param teamId
     *            the ID of the team for which to retrieve the members
     * @param teamName
     *            the name of the team for which to retrieve the members
     * @return
     */
    public List<TeamMemberDTO> getTeamMembers(int teamId, String teamName) {
        return this.userRepository.getTeamMembers(teamId, teamName);
    }

    // -── ACTIONS TO PERFORM ON THE DATABASE ───

    /**
     * Create a new user in the database with the specified username and return the generated user ID.
     *
     * @param username
     *            the username of the new user to be created in the database
     * @return the ID of the newly created user in the database
     */
    public int createUser(String username) {
        return this.userRepository.createUser(username);
    }

    /**
     * Create a new team in the database for a specific user, identified by their userId, with the specified team name.
     *
     * @param userId
     *            the ID of the user for whom to create the new team
     * @param teamName
     *            the name of the new team to be created in the database for the specified user
     */
    public void createTeam(int userId, String teamName) {
        this.userRepository.createTeam(userId, teamName);
    }

    /**
     * Save the state of a user's bugemon in the database. This method takes a UserBugemonDTO object containing the
     * details of the user's bugemon, such as its current stats and level, and updates the corresponding entry in the
     * database to reflect these details.
     *
     * @param dto
     *            the UserBugemonDTO object containing the details of the user's bugemon to be saved
     */
    public void saveUserBugemon(UserBugemonDTO dto) {
        this.userRepository.saveUserBugemon(dto);
    }

    /**
     * Update the state of a user's bugemon in the database. This method takes a UserBugemonDTO object containing the
     * updated details of the user's bugemon, such as its current stats and level, and updates the corresponding entry
     * in the database to reflect these new details.
     *
     * @param dto
     *            the UserBugemonDTO object containing the updated details of the user's bugemon to be updated
     */
    public void updateUserBugemon(UserBugemonDTO dto) {
        this.userRepository.updateUserBugemon(dto);
    }

    /**
     * Add a new member to a team in the database. This method takes a TeamMemberDTO object containing the details of
     * the team member to be added, such as the user ID, team name, bugemon ID, and slot position, and inserts a new
     * entry in the database to represent this team member.
     *
     * @param dto
     *            the TeamMemberDTO object containing the details of the team member to be added to the database
     */
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
     * @param bugemonId
     *            the ID of the bugemon that represents the team member to be removed from the specified team in the
     *            database
     */
    public void removeTeamMember(int userId, String teamName, String bugemonId) {
        this.userRepository.removeTeamMember(userId, teamName, bugemonId);
    }

    /**
     * Delete a team from the database. This method takes the user ID and team name of the team to be deleted, and
     * removes the corresponding entry from the database to reflect that this team no longer exists for the specified
     * user.
     *
     * @param userId
     *            the ID of the user who owns the team to be deleted
     * @param teamName
     *            the name of the team to be deleted from the database for the specified user
     */
    public void deleteTeam(int userId, String teamName) {
        this.userRepository.deleteTeam(userId, teamName);
    }

    public void deleteTeamMembers(int userId, String teamName) {
        this.userRepository.deleteTeamMembers(userId, teamName);
    }
}
