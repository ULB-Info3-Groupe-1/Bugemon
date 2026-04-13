package ulb.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Manages the single PostgreSQL connection.
 * Falls back to embedded Postgres if no {@code .env} file is found.
 */
public class DatabaseConnection {
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String envUrl = dotenv.get("DB_URL");

        if (envUrl != null) {
            DB_URL = envUrl;
            DB_USER = dotenv.get("DB_USER");
            DB_PASSWORD = dotenv.get("DB_PASSWORD");
        } else {
            String[] credentials = startEmbeddedPostgres();
            DB_URL = credentials[0];
            DB_USER = credentials[1];
            DB_PASSWORD = credentials[2];
        }
    }

    private static String[] startEmbeddedPostgres() {
        try {
            EmbeddedPostgres pg = EmbeddedPostgres.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    pg.close();
                } catch (IOException e) {
                    // ignore on shutdown
                }
            }));
            return new String[]{pg.getJdbcUrl("postgres", "postgres"), "postgres", ""};
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Connection connection;

    public DatabaseConnection() {
        this.getConnection();
    }

    private void getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reconnect to database", e);
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
}
