package Utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {
    String host = "25.42.126.130:3050/C:/AKINSOFT/Wolvox7/Database_FB/yagmur2017/2017/WOLVOX.FDB";
    String user = "SYSDBA";
    String password = "masterkey";

    public Statement createFirebirdStatement() throws SQLException{
        // FOR TEST
        Connection con = DriverManager.getConnection("jdbc:sqlite:./src/test/resources/mock.db");
        //Connection con = DriverManager.getConnection("jdbc:firebirdsql://" + host, user, password);
        Statement stat = con.createStatement();
        return stat;
    }
}
