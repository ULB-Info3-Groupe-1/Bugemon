package bugemon.server.repositories.postgres;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import bugemon.server.repositories.DatabaseConnection;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * PostgreSQL implementation of {@link DatabaseConnection} backed by a HikariCP connection pool.
 *
 * <p>
 * Reads connection credentials ({@code DB_URL}, {@code DB_USER}, {@code DB_PASSWORD}) from a {@code .env} file located
 * in the working directory or next to the JAR, falling back to environment variables when no file is found. The pool
 * lets the many virtual threads serving concurrent clients each borrow their own physical connection, which a single
 * shared {@link Connection} could not safely provide.
 */
public class PostgresDatabaseConnection implements DatabaseConnection {

    private static final Dotenv DOTENV = loadDotenv();
    private static final int MAX_POOL_SIZE = 10;

    private final HikariDataSource dataSource;

    public PostgresDatabaseConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DOTENV.get("DB_URL"));
        config.setUsername(DOTENV.get("DB_USER"));
        config.setPassword(DOTENV.get("DB_PASSWORD"));
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setPoolName("bugemon-pool");
        this.dataSource = new HikariDataSource(config);
    }

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

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
