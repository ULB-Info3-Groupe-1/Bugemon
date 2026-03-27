package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private Connection connection;

    public DatabaseConnection() {
        this.getConnection();
    }

    private void getConnection() {
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reconnect to database", e);
        }
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
