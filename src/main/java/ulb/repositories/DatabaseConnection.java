package ulb.repositories;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    private static final Dotenv DOTENV = loadDotenv();

    private static Dotenv loadDotenv() {
        // 1. Current working directory (where the user runs "java -jar")
        if (new File(System.getProperty("user.dir"), ".env").exists()) {
            return Dotenv.configure().directory(System.getProperty("user.dir")).load();
        }

        // 2. Directory containing the JAR file
        try {
            URI location = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File source = new File(location);
            if (source.isFile() && new File(source.getParent(), ".env").exists()) {
                return Dotenv.configure().directory(source.getParent()).load();
            }
        } catch (Exception e) {
            // ignore, fall through
        }

        // 3. System environment variables only (no .env file)
        return Dotenv.configure().ignoreIfMissing().load();
    }

    private Connection connection;

    public DatabaseConnection() {
        this.getConnection();
    }

    private void getConnection() {
        String url = DOTENV.get("DB_URL");
        String user = DOTENV.get("DB_USER");
        String password = DOTENV.get("DB_PASSWORD");

        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reconnect to database", e);
        }
    }

    /**
     * Helper method to prepare a SQL statement using the current database connection.
     *
     * @param sql
     *            The SQL query to prepare
     * @return A PreparedStatement ready to be executed
     * @throws SQLException
     *             if the preparation fails
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
}
