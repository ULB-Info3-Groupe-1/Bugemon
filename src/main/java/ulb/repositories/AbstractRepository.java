package ulb.repositories;

import java.util.Map;

public abstract class AbstractRepository {

    private final Map<String, String> queries;

    protected AbstractRepository(Map<String, String> queries) {
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
}
