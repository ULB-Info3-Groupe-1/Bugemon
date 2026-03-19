package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String PRODUCTION_URL = dotenv.get("PRODUCTION_DB_URL");

    private String currentUrl = PRODUCTION_URL;

    private Connection connection;

    public DatabaseManager() {
        getConnection();
    }

    public DatabaseManager(String testUrl) {
        this.currentUrl = testUrl;
        getConnection();
    }

    public void getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                this.connection = DriverManager.getConnection(currentUrl);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reconnect to database", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
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

    /**
     * Checks if the database connection is currently active.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check connection status", e);
        }
    }

    /**
     * Provides direct access to the underlying Connection object for advanced operations.
     * @return The active Connection object
     */
    public Connection getConnectionObject() {
        return this.connection;
    }
}
