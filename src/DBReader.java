/** DBReader.java
 *
 * Uses JDBC to interface with the DB
 *
 *
 *
 */


import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DBReader {

    private static final String IP = "68.105.140.178";
    private static final String URL = "jdbc:sqlserver://" + IP + ":1433;DatabaseName=Ascension;encrypt=true;trustServerCertificate=true;";
    private static final String username = "sa";
    private static final String password = "22436";

    private Connection connection = null;

    private long nanoTimeStart;

    public DBReader() {
        try {
            // register the jdbc driver
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver()); // throws exception
        } catch (Exception e) {
            System.out.println("[DBReader] Failed to register JDBC driver");
            System.exit(-1);
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            System.out.println("[DBReader] SQL error in isConnected()");
            System.exit(-1);
        }

        return false;
    }

    public void connect() {
        nanoTimeStart = System.nanoTime();

        try {
            Connection connection = DriverManager.getConnection(URL, username, password);
            System.out.println("[DBReader] Connected after " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTimeStart) + " ms");
            this.connection = connection;
            nanoTimeStart = System.nanoTime();
        } catch (Exception e) {
            System.out.println("[DBReader] Failed to connect to the database");
            System.exit(-1);
        }
    }

    public ResultSet doQuery(String query) {
        if (!isConnected()) {
            System.out.println("[DBReader] Must connect before querying");
            return null;
        }

        Statement stmt = null;
        ResultSet result = null;
        try {
            stmt = connection.createStatement();
            result = stmt.executeQuery(query);
        } catch (Exception e) {
            System.out.println("[DBReader] Failed to perform query");
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public ArrayList<Field> getFields(String tableName) {
        ArrayList<Field> fields = new ArrayList<Field>();

        ResultSet result = doQuery("" +
                        "    SELECT\n" +
                        "        COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE\n" +
                        "    FROM\n" +
                        "        INFORMATION_SCHEMA.COLUMNS\n" +
                        "    WHERE\n" +
                        "        TABLE_NAME = 'offensive'\n" +
                        "    ORDER BY 2");//\n" +
                        //"GO");

        try {
            while (result.next()) {
                Field thisField = new Field(result.getString("COLUMN_NAME"), result.getString("DATA_TYPE"));
                fields.add(thisField);
            }
        } catch (Exception e) {
            System.out.println("[DBReader] Failed to retrieve field names");
            return null;
        }

        return fields;

    }

    // this is really bad because i didnt know how to do sql
    public String buildQueryFromFields(ArrayList<Field> fields, String table) {
        String query = "SELECT TOP (1000)\n";

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            query +=  "[" + field.getName() + "]";

            if (i < fields.size()-1) {
                query += ",\n";
            }
        }

        query += "\nFROM [Ascension]." + table;

        return query;
    }

    private String getTableName(String table) {
        String[] s = table.split("\\[");
        String name = s[s.length-1].replace("]", "");

        return name;
    }

    public String buildQueryForTable(String table) {
        ArrayList<Field> fields = getFields(getTableName(table));
        String query = buildQueryFromFields(fields, table);

        return query;
    }

    public ResultSet retrieveRecords(String table) {
        String query = buildQueryForTable(table);
        ResultSet result = doQuery(query);

        return result;
    }

    public FlexResultSet retrieveFlex(String table) {
        ArrayList<Field> fields = getFields(getTableName(table));
        String query = buildQueryFromFields(fields, table);

        ResultSet result = doQuery(query);

        FlexResultSet flexResult = new FlexResultSet(result, fields);

        return flexResult;
    }

    public void close() {
        try {
            connection.close();
            this.connection = null;
            System.out.println("[DBReader] Closed connection after " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTimeStart) + " ms");
        } catch (Exception e) {
            System.out.println("[DBReader] Error occurred when attempting to close connection");
            System.exit(-1);
        }


    }

}
