package dataaccess;

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
}
