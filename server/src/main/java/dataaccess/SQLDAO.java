package dataaccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDAO {
    protected void configureDatabase(String[] statements)
            throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (String statement : statements) {
                try (PreparedStatement ps =
                        connection.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }

}
