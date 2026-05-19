package ulb.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DatabaseConnection {
    PreparedStatement prepareStatement(String sql) throws SQLException;
}
