package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = dotenv.get("PRODUCTION_DB_URL");

    private Connection connection;

    public DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to the database", e);
        }
    }

    public void getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
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
}
