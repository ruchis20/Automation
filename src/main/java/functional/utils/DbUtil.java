package functional.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * utility class that contains functions for database
 * actions
 */

public class DbUtil {
    Configuration conf = new Configuration();
    String dbType = conf.getDb();
    Statement stmt;
    /**
     * initializes the object either with Oracle or MySql
     * based on command line argument -Pdb=mysql or -Pdb=oracle
     * If parameter is missing then it defaults to oracle
     */
    private Statement getStatement() {
        try {
            if (dbType.equalsIgnoreCase("mysql")) {
                return getMySQLConnection().createStatement();
            } else {
                return getOracleConnection().createStatement();
            }
        }catch (Exception e){
            System.out.println("Exception creatin statement:" + e.toString());
            return null;
        }
    }

    private Connection getMySQLConnection() {
        Connection conn = null;
        MysqlDataSource ds = new MysqlConnectionPoolDataSource();
        Map<String,String> properties = conf.getProperties();
        ds.setURL(properties.get("mysql_url"));
        ds.setUser(properties.get("mysql_username"));
        ds.setPassword(properties.get("mysql_password"));
        try{
            conn = ds.getConnection();
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return conn;
    }

    private Connection getOracleConnection() throws Exception {
        Connection conn = null;
        Map<String,String> properties = conf.getProperties();
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String serverName = properties.get("oracle_server");
        String portNumber = properties.get("oracle_port");
        String service = properties.get("oracle_service");
        String oracleUrl = "jdbc:oracle:thin:@" + serverName + ':' + portNumber + "/" + service;
        String username = properties.get("oracle_username");
        String password = properties.get("oracle_password");
        try{
            conn = DriverManager.getConnection(oracleUrl, username, password);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return conn;
    }
    /**
     * Method to run query against db.
     * @param query string
     * @return List of maps with table column names as keys and column values as values
     */
    public List queryDb(String query) {
        List<Object> results = new ArrayList<Object>();
        stmt = getStatement();
        try {
            ResultSet resultSet = stmt.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> columns = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++ ) {
                 columns.add(rsmd.getColumnName(i));
            }
            while (resultSet.next()) {
                for (String columnName : columns) {
                    Map<String,String> data = new HashMap<String,String>();
                    data.put(columnName, resultSet.getString(columnName));
                    results.add(data);
                }
            }
        }catch(Exception e){
            System.out.println("Exception: " + e.toString());
        }
        return results;
    }

    /**
     * Deletes data from table
     * @param  query
     * @return true or false
     */
    public boolean executeQuery(String query) {
        if(query.length() <= 0) {
            System.out.println("You tried to execute empty query");
            return false;
        }
        stmt = getStatement();
        try{
            return stmt.execute(query);
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
    }

    public String getQueryString(String query) {
        Map<String,String> queries = conf.getQueries();
        return queries.get(query);
    }
}
