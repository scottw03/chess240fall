package dataaccess;

public class SQLGameDAO extends SQLDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureTable();
    }

    private void configureTable() throws DataAccessException {
        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS game (
                    gameID INT NOT NULL AUTHO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL
                    )
                """
        };
        configureDatabase(statements);
    }

}
