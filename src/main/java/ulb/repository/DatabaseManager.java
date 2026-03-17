package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    // TODO: Better configuration
    private static String URL =
            "jdbc:postgresql://ep-twilight-frog-alrfqjg7-pooler.c-3.eu-central-1.aws.neon.tech/"
            + "bugemon?user=bugemon&password=npg_vbus4D2Yltdf&sslmode=require&channelBinding="
            + "require";

    public DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect to database", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }

    /**
     * Helper method to prepare a SQL statement using the current database connection.
     * @param sql The SQL query to prepare
     * @return A PreparedStatement ready to be executed
     * @throws SQLException if the preparation fails
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    // --- METHOD NEEDED TO CONNECT TO THE TEST DATABASE BRANCH ---

    public void setTestMode(String testUrl) {
        URL = testUrl;
        closeConnection();
        getConnection();
    }
}
