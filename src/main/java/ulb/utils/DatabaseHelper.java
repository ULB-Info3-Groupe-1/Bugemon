package ulb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for database operations.
 */
public class DatabaseHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    private DatabaseHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the enum value from the result set.
     *
     * @param <E>
     *            the enum type to get the value from
     * @param rs
     *            the result set to get the value from
     * @param columnName
     *            the column name to get the value from
     * @param enumClass
     *            the enum class to get the value from
     * @return the enum value from the result set or null
     * @throws SQLException
     *             if the enum value is invalid
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
