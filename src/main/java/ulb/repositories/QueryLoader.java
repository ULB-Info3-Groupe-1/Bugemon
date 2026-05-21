package ulb.repositories;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ulb.Configuration;

public class QueryLoader {

    private static final String QUERY_NAME_PREFIX = "-- ";
    private static final String QUERY_START_PREFIX = "-- Query";
    private static final int MAX_FILE_WALK_DEPTH = 1;

    // Queries Map (Request Name -> SQL Code)
    private final Map<String, String> queries = new HashMap<>();

    /**
     * Loads SQL queries, creates the schema if absent, and bootstraps static game data.
     */
    public QueryLoader() {
        this.loadSQLQueries();
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
        try (InputStream is = QueryLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("SQL file not found: " + filePath);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            String currentQueryName = null;
            StringBuilder currentSql = new StringBuilder();

            // see rules.md for the format of the SQL files
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(QUERY_START_PREFIX)) {
                    if (currentQueryName != null && !currentSql.isEmpty()) {
                        this.queries.put(currentQueryName, currentSql.toString().trim());
                        currentSql.setLength(0); // We reset the StringBuilder for the next query
                    }
                    currentQueryName = null;
                } else if (currentQueryName == null && line.startsWith(QUERY_NAME_PREFIX)) {
                    currentQueryName = line.substring(QUERY_NAME_PREFIX.length()).trim();
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
            URL url = QueryLoader.class.getResource(Configuration.Paths.SQL_BASE_PATH);
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
     * Handles both regular filesystem and JAR filesystem for resource loading.
     */
    private FileSystem getOrCreateFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private void walkAndAddFiles(Path path, List<String> result) throws IOException {
        try (Stream<Path> walk = Files.walk(path, MAX_FILE_WALK_DEPTH)) {
            walk.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(p -> result.add(Configuration.Paths.SQL_BASE_PATH + p.getFileName().toString()));
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

    public Map<String, String> getQueries() {
        return this.queries;
    }
}
