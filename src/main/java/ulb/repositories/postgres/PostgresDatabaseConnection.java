package ulb.repositories.postgres;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ulb.repositories.DatabaseConnection;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * PostgreSQL implementation of {@link DatabaseConnection}.
 *
 * <p>
 * Reads connection credentials ({@code DB_URL}, {@code DB_USER}, {@code DB_PASSWORD}) from a {@code .env} file located
 * in the working directory or next to the JAR, falling back to environment variables when no file is found.
 */
public class PostgresDatabaseConnection implements DatabaseConnection {
    private static final Dotenv DOTENV = loadDotenv();

    private static Dotenv loadDotenv() {
        if (new File(System.getProperty("user.dir"), ".env").exists()) {
            return Dotenv.configure().directory(System.getProperty("user.dir")).load();
        }

        try {
            URI location = PostgresDatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File source = new File(location);
            if (source.isFile() && new File(source.getParent(), ".env").exists()) {
                return Dotenv.configure().directory(source.getParent()).load();
            }
        } catch (URISyntaxException e) {
            // ignore, fall through
        }

        return Dotenv.configure().ignoreIfMissing().load();
    }

    private Connection connection;

    public PostgresDatabaseConnection() {
        this.connect();
    }

    private void connect() {
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

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
}
