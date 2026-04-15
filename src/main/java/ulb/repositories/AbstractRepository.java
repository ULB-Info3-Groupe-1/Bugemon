package ulb.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository {

    private final Map<String, String> queries;
    protected final DatabaseConnection dbConnection;

    protected AbstractRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.queries = QueryLoader.getQueries();
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

    protected void executeUpdate(String queryName, Object... params) {
        try (PreparedStatement ps = this.prepare(queryName, params)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(queryName + " failed", e);
        }
    }

    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

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

    private PreparedStatement prepare(String queryName, Object... params) throws SQLException {
        PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql(queryName));
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }
}
