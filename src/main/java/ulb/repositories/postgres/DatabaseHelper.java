package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    private DatabaseHelper() {
        // Private constructor to prevent instantiation
    }

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
