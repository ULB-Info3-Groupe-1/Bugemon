package bugemon.server.repositories;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstraction over a database connection source.
 *
 * <p>
 * Implementations are backed by a connection pool so that the many virtual threads serving concurrent clients each
 * obtain their own {@link Connection} instead of sharing one (a single JDBC {@code Connection} is not thread-safe).
 * Callers <strong>must</strong> close the returned connection — for a pooled implementation, closing returns it to the
 * pool rather than tearing down the physical connection — ideally via try-with-resources.
 */
public interface DatabaseConnection {

    /**
     * Borrows a connection from the pool.
     *
     * @return a ready-to-use connection that the caller must close when done
     * @throws SQLException
     *             if no connection can be obtained
     */
    Connection getConnection() throws SQLException;
}
