package ulb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseHelper {
    private static final Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());

    private DatabaseHelper() {
        // Private constructor to prevent instantiation
    }

    public static <E extends Enum<E>> E getEnumOrNull(ResultSet rs, String columnName,
            Class<E> enumClass) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Valeur Enum invalide pour la colonne '" + columnName + "': " + value);
            return null;
        }
    }
}
