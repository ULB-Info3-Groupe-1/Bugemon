package ulb.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    // TODO: Better configuration
    private static String URL = "jdbc:postgresql://"
                                + "ep-twilight-frog-alrfqjg7-pooler.c-3.eu-central-1.aws.neon.tech:"
                                + "5432/bugemon?sslmode=require";
    private static final String USER = "bugemon";
    private static final String PASSWORD = "npg_vbus4D2Yltdf";

    private DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
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

    // --- METHOD NEEDED TO CONNECT TO THE TEST DATABASE BRANCH ---

    public static void setTestMode(String testUrl) {
        URL = testUrl;
        if (instance != null) {
            instance.closeConnection();
            instance = null; // Force reinitialization with the new URL
        }
    }
}
