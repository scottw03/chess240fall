package dataaccess;

public class SQLUserDAO extends SQLDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureTable();
    }

    private void configureTable() throws DataAccessException {
        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) NOT NULL PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                    )
                """
        };
        configureDatabase(statements);
    }

}
