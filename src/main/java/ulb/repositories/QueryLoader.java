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

public class QueryLoader {

    // Queries Map (Request Name -> SQL Code)
    private static final Map<String, String> QUERIES = new HashMap<>();
    private static boolean isLoaded = false;

    private QueryLoader() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns a Map of SQL queries that have been loaded from the SQL files and cannot be modified.
     *
     * @return the Map of SQL queries
     */
    public static synchronized Map<String, String> getQueries() {
        if (!isLoaded) {
            loadSQLQueries();
            isLoaded = true;
        }
        return Collections.unmodifiableMap(QUERIES);
    }

    private static void loadSQLQueries() {
        List<String> sqlFiles = getSqlFiles();
        for (String file : sqlFiles) {
            loadQueriesFromFile(file);
        }
    }

    /**
     * Parses a SQL file and populates {@code queries}. See {@code team/rules.md} for the required file format
     * ({@code -- Query ...} / {@code -- QueryName} / SQL body).
     */
    private static void loadQueriesFromFile(String filePath) {
        try (InputStream is = QueryLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("SQL file not found: " + filePath);
            }
            saveQueriesFromFile(is, filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Error loading queries from " + filePath, e);
        }
    }

    private static void saveQueriesFromFile(InputStream is, String filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            String currentQueryName = null;
            StringBuilder currentSql = new StringBuilder();

            // see rules.md for the format of the SQL files
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-- Query")) {
                    saveCurrentQuery(currentQueryName, currentSql);
                    currentQueryName = null;
                } else if (currentQueryName == null && line.startsWith("-- ")) {
                    currentQueryName = line.substring(3).trim();
                } else if (currentQueryName != null) {
                    currentSql.append(line).append("\n");
                }
            }
            saveCurrentQuery(currentQueryName, currentSql);
        } catch (IOException e) {
            throw new IllegalStateException("Error loading queries from " + filePath, e);
        }
    }

    private static void saveCurrentQuery(String name, StringBuilder sql) {
        if (name != null && !sql.isEmpty()) {
            if (QUERIES.containsKey(name)) {
                throw new IllegalStateException("Duplicate query name: " + name);
            }
            QUERIES.put(name, sql.toString().trim());
            sql.setLength(0);
        }
    }

    private static List<String> getSqlFiles() {
        List<String> result = new ArrayList<>();
        try {
            URL url = QueryLoader.class.getResource("/sql/");
            if (url == null) {
                throw new IllegalStateException("SQL directory not found");
            }
            URI uri = url.toURI();
            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fs = getOrCreateFileSystem(uri)) {
                    walkAndAddFiles(fs.getPath("/sql"), result);
                }
            } else {
                walkAndAddFiles(Paths.get(uri), result);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error loading SQL files", e);
        }
        return result;
    }

    /**
     * Handles both regular filesystem and JAR filesystem for resource loading.
     */
    private static FileSystem getOrCreateFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private static void walkAndAddFiles(Path path, List<String> result) throws IOException {
        try (Stream<Path> walk = Files.walk(path, 1)) {
            walk.filter(p -> p.toString().endsWith(".sql"))
                    .forEach(p -> result.add("/sql/" + p.getFileName().toString()));
        }
    }
}
