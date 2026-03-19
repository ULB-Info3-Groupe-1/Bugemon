package ulb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    public static <E extends Enum<E>> E getEnumOrNull(ResultSet rs, String columnName,
                                                      Class<E> enumClass) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            System.err.println("Valeur Enum invalide pour la colonne '" + columnName
                               + "': " + value);
            return null;
        }
    }
}