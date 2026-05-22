package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateless utility methods for reading values from a {@link java.sql.ResultSet}.
 */
public class DatabaseHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    private DatabaseHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Reads a string column and converts it to the matching enum constant, returning {@code null} when the column is
     * {@code NULL}, blank, or contains an unrecognised value.
     *
     * @param <E>
     *            the enum type
     * @param rs
     *            the result set positioned at the current row
     * @param columnName
     *            the column to read
     * @param enumClass
     *            the enum class to use for {@link Enum#valueOf} lookup
     * @return the matching constant, or {@code null} if the value is absent or invalid
     * @throws SQLException
     *             if the column cannot be read
     */
    public static <E extends Enum<E>> E getEnumOrNull(ResultSet rs, String columnName, Class<E> enumClass)
            throws SQLException {
        String value = rs.getString(columnName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            LOG.error("Enum Value invalid for column {}: {}", columnName, value);
            return null;
        }
    }
}
