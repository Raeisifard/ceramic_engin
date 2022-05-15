package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestJdbcInstance {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        Class.forName(driver);
        //Connection dbConnection = DriverManager.getConnection("jdbc:sqlserver://192.168.166.11:1433;SelectMethod=cursor;DatabaseName=bki_suny", "sa", "suny$123");
        Connection dbConnection = DriverManager.getConnection("jdbc:sqlserver://192.168.166.235;instanceName=SQL_2012;SelectMethod=cursor;DatabaseName=Magfa01", "sa", "suny$123");
        if (dbConnection.isValid(3)) {
            System.out.println("success!");
        }
    }
}
