package test;

//import com.microsoft.sqlserver.jdbc.SQLServerStatement;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;

public class TestJDBCStream {
    public static void main(String[] args) {
        /*SQLClient client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:sqlserver://192.168.166.11:1433;SelectMethod=cursor;DatabaseName=BXX_SAPTA11;")
                //.put("driver_class", "microsoft.sqlserver.jdbc.SQLServerDriver")
                .put("user", "sa")
                .put("password", "suny$123")
                .put("max_pool_size", 30));*/
        String SQL = "select \n" +
                "\t--convert(varchar(9),UniqId) + '|' +   \n" +
                "\tconvert(varchar(13),isnull(AccountNo,'null')) + '|' +\n" +
                "\tconvert(varchar(10),isnull(Enable,'null')) + '|' +\n" +
                "\tconvert(varchar(10),isnull(CustomerId,'null')) + '|' +\n" +
                "\tconvert(varchar(100),isnull(TransGroupsStr,'null')) + '|' +\n" +
                "\tconvert(varchar(110),isnull(Phones,'null')) + '|' +\n" +
                "\tconvert(varchar(10),isnull(AutoRenewRegister,'null')) + '|' +\n" +
                "\tconvert(varchar(60),isnull(RemainAmount,'null')) + '|' +\n" +
                "\tconvert(varchar(50),isnull(RemainDays,'null')) + '|' +\n" +
                "\tisnull(convert(varchar(10),DueDate, 102),'null') + '|' +\n" +
                "\tconvert(varchar(10),isnull(LowLimitCredit,'null')) + '|' +\n" +
                "\tconvert(varchar(10),isnull(LowLimitDebit,'null')) + '|' +\n" +
                "\tisnull(convert(varchar(10),ExpireDate, 102) , 'null')+ '|' +\n" +
                "\tisnull(convert(varchar(10),NearExpireNotifyDate, 102),'null')+ '|' +\n" +
                "\tisnull(convert(varchar(10),ExpiredNotifyDate, 102) , 'null') + '|' +\n" +
                "\t--convert(varchar(23),isnull(EditDT,'null')) + '|' +\n" +
                "\tisnull(convert(varchar(10),AutoRenewRegCancelNotifyDate, 102) , 'null') +'\\n'\n" +
                "\t--convert(varchar(10),isnull(Flag,'null'))\n" +
                "\tfrom [TblAccounts3-brf] for XML PATH('')";
        //SQL = "select 'behnam'";
        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://192.168.166.11:1433;DatabaseName=BXX_SAPTA11;user=sa;password=suny$123";

        try (Connection con = DriverManager.getConnection(connectionUrl);
             Statement stmt = con.createStatement();) {

            // In adaptive mode, the application does not have to use a server cursor
            // to avoid OutOfMemoryError when the SELECT statement produces very large
            // results.

            // Display the response buffering mode.
            //SQLServerStatement SQLstmt = (SQLServerStatement) stmt;
            //System.out.println("Response buffering mode is: " + SQLstmt.getResponseBuffering());
            //SQLstmt.close();
            //System.out.println("SQL: " + SQL);
            // Get the updated data from the database and display it.
            ResultSet rs = stmt.executeQuery(SQL);
            //"0400618111007|0|0001341080|TG00;TG99|9151606146|0|0|0|2021.04.20|0|0|13971023|null|null|2020.04.20\\n0100000205008|0|4000872343|T".split("\\n");
            while (rs.next()) {
                Reader reader = rs.getCharacterStream(1);
                if (reader != null) {
                    char[] ac3 = new char[128];
                    StringBuilder remainder = new StringBuilder();
                    while (reader.read(ac3) != -1) {
                        remainder.append(String.valueOf(ac3));
                        //System.out.println(remainder);
                        String[] rows = remainder.toString().split("\\\\n");
                        int len = rows.length - 1;
                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                System.out.println(rows[i]);
                            }
                            remainder.setLength(0);
                            remainder.append(rows[len]);
                        }
                        //output = new char[128];
                    }

                    //System.out.println(rs.getString(1) + " has been accessed for the summary column.");
                    // Close the stream.
                    reader.close();
                }
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }
       /* client.getConnection(res -> {
            if (res.succeeded()) {

                SQLConnection connection = res.result();

                connection.queryStream(query, stream -> {
                    if (stream.succeeded()) {
                        System.out.println("Stream result-set returned successfully");
                        stream.result().handler(row -> {
                            accountCount++;
                            // System.out.println(row.toString());
                            // LOGGER.debug( row.toString());
                            var accountInfo = row.getString(0);
                            var split = accountInfo.split("\\|");
                            //var accountNo = accountInfo.substring(0, 13);
                            var accountNo = split[0];
                            accounts.put(accountNo, accountInfo);
                            var customerId = split[2];
                            if (isNotBlank(customerId)) {
                                var accountNos = customers.getOrDefault(customerId, "");
                                if (!accountNos.contains(accountNo)) {
                                    accountNos += "|" + accountNo;
                                    customers.put(customerId, accountNos);
                                }
                            }
                        });
                    }
                });
            } else {
                // Failed to get connection - deal with it
            }
        });*/
    }
}
