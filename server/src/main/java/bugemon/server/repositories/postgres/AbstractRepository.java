package bugemon.server.repositories.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bugemon.server.repositories.DatabaseConnection;

/**
 * Base class for all PostgreSQL repository implementations.
 *
 * <p>
 * Provides named-query lookup via an injected query map and three template methods — {@link #executeUpdate},
 * {@link #executeQuery}, and {@link #executeSingleQuery} — that handle {@link PreparedStatement} preparation, parameter
 * binding, and {@link ResultSet} iteration. Subclasses supply domain-specific {@link RowMapper} implementations to
 * convert rows into model objects.
 */
public abstract class AbstractRepository {

    private final Map<String, String> queries;
    protected final DatabaseConnection dbConnection;

    /**
     * Constructs the repository with a shared database connection and the pre-loaded query map.
     *
     * @param dbConnection
     *            the live database connection
     * @param queries
     *            map of query name to SQL string, produced by {@link bugemon.server.repositories.QueryLoader}
     */
    protected AbstractRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        this.dbConnection = dbConnection;
        this.queries = queries;
    }

    /**
     * Returns the SQL string for the given query name.
     *
     * @throws IllegalArgumentException
     *             if the query name is not found
     */
    protected String getSql(String queryName) {
        String sql = this.queries.get(queryName);
        if (sql == null) {
            throw new IllegalArgumentException("SQL query not found in Map : " + queryName);
        }
        return sql;
    }

    /**
     * Executes a named DML statement (INSERT / UPDATE / DELETE) with the supplied positional parameters.
     *
     * @param queryName
     *            the name key used to look up the SQL string
     * @param params
     *            positional parameter values bound left-to-right
     * @throws IllegalStateException
     *             if the query fails or the name is unknown
     */
    protected void executeUpdate(String queryName, Object... params) {
        try (PreparedStatement ps = this.prepare(queryName, params)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(queryName + " failed", e);
        }
    }

    /**
     * Single-row mapping function consumed by {@link #executeQuery} and {@link #executeSingleQuery}.
     *
     * @param <T>
     *            the domain type produced from one {@link ResultSet} row
     */
    @FunctionalInterface
    protected interface RowMapper<T> {
        /**
         * Maps the current row of {@code rs} to a domain object.
         *
         * @param rs
         *            the result set positioned at the current row
         * @return the mapped domain object
         * @throws SQLException
         *             if a column cannot be read
         */
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Executes a named SELECT query and maps every result row to a domain object.
     *
     * @param <T>
     *            the domain type
     * @param queryName
     *            the name key used to look up the SQL string
     * @param mapper
     *            row-to-domain mapping function
     * @param params
     *            positional parameter values
     * @return list of mapped objects, possibly empty
     * @throws IllegalStateException
     *             if the query fails or the name is unknown
     */
    protected <T> List<T> executeQuery(String queryName, RowMapper<T> mapper, Object... params) {
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = this.prepare(queryName, params); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(queryName + " failed", e);
        }
        return result;
    }

    /**
     * Executes a named SELECT query and returns the first row, throwing if no rows are returned.
     *
     * @param <T>
     *            the domain type
     * @param queryName
     *            the name key used to look up the SQL string
     * @param mapper
     *            row-to-domain mapping function
     * @param params
     *            positional parameter values
     * @return the first mapped object
     * @throws IllegalStateException
     *             if the query returns no rows or fails
     */
    protected <T> T executeSingleQuery(String queryName, RowMapper<T> mapper, Object... params) {
        List<T> results = this.executeQuery(queryName, mapper, params);

        if (results.isEmpty()) {
            throw new IllegalStateException(queryName + " returned no results");
        }

        return results.get(0);
    }

    private PreparedStatement prepare(String queryName, Object... params) throws SQLException {
        PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql(queryName));
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }
}
