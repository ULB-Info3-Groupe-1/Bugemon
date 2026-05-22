package ulb.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstraction over a live database connection that produces {@link PreparedStatement} instances.
 *
 * <p>
 * Implementations are responsible for managing the underlying connection lifecycle (pooling, reconnection, etc.).
 * Callers own the returned {@link PreparedStatement} and must close it.
 */
public interface DatabaseConnection {

    /**
     * Creates a {@link PreparedStatement} for the given SQL string.
     *
     * @param sql
     *            the parameterised SQL to compile
     * @return a new prepared statement bound to the current connection
     * @throws SQLException
     *             if the connection is closed or the SQL is invalid
     */
    PreparedStatement prepareStatement(String sql) throws SQLException;
}
