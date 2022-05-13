import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

public class TestConnector {
    public Statement createTempDatabaseStatement() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:./src/test/resources/.tmp.db");
        Statement stat = con.createStatement();
        return stat;
    }
    @Test
    public void test_create_temp_mock_database() throws SQLException{
        Statement stat = createTempDatabaseStatement();
        Path path = Paths.get("./src/test/resources/.tmp.db");
        assertTrue(Files.exists(path));
    }

    @After
    public void test_remove_temp_database_file(){
        File tmpData = new File("./src/test/resources/.tmp.db");
        tmpData.delete();
    }
}
