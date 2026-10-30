package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureTable();
    }

    private void configureTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS authToken (
                    authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                    )
                """
        };
        configureDatabase(statements);
    }
    @Override
    public void createAuth(AuthData auth)
            throws DataAccessException {
        if (auth == null) {
            throw new DataAccessException("bad request");
        }
        String sql = """
                INSERT INTO authTokens (authToken, username)
                VALUES (?, ?)
                """;
        try {
            executeUpdate(
                    sql,
                    auth.authToken(),
                    auth.username()
            );

        } catch (DataAccessException e) {

            if (e.getMessage()
                    .toLowerCase()
                    .contains("duplicate")) {
                throw new DataAccessException("already taken");
            }

            throw e;
        }
    }


}
