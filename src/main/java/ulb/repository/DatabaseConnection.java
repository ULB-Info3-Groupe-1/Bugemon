package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String[] DB_URL_KEYS = {"PRODUCTION_DB_URL", "DATABASE_URL", "DB_URL"};

    private Connection connection;
    private final String url;

    public DatabaseConnection() {
        this.url = resolveDatabaseUrl();

        try {
            this.connection = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to the database using URL '"
                                                    + sanitizeUrl(this.url) + "'",
                                            e);
        }
    }

    public void getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(this.url);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reconnect to database", e);
        }
    }

    private String resolveDatabaseUrl() {
        for (String key : DB_URL_KEYS) {
            String fromDotenv = dotenv.get(key);
            if (fromDotenv != null && !fromDotenv.isBlank()) {
                return fromDotenv;
            }

            String fromEnv = System.getenv(key);
            if (fromEnv != null && !fromEnv.isBlank()) {
                return fromEnv;
            }
        }

        throw new IllegalStateException(
                "Database URL is missing. Set one of: PRODUCTION_DB_URL, DATABASE_URL, DB_URL.");
    }

    private String sanitizeUrl(String rawUrl) {
        return rawUrl.replaceAll("password=[^&]*", "password=****");
    }

    private String resolveDatabaseUrl() {
        for (String key : DB_URL_KEYS) {
            String fromDotenv = dotenv.get(key);
            if (fromDotenv != null && !fromDotenv.isBlank()) {
                return fromDotenv;
            }

            String fromEnv = System.getenv(key);
            if (fromEnv != null && !fromEnv.isBlank()) {
                return fromEnv;
            }
        }

        throw new IllegalStateException(
                "Database URL is missing. Set one of: PRODUCTION_DB_URL, DATABASE_URL, DB_URL.");
    }

    private String sanitizeUrl(String rawUrl) {
        return rawUrl.replaceAll("password=[^&]*", "password=****");
    }

    // TODO: this is unused -> concerning
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to close connection", e);
        }
    }

    /**
     * Helper method to prepare a SQL statement using the current database connection.
     * @param sql The SQL query to prepare
     * @return A PreparedStatement ready to be executed
     * @throws SQLException if the preparation fails
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
}
