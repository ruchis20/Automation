package functional.utils;

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
    private Configuration conf = new Configuration();

    private Connection getConnection(){
        Connection conn = null;
        Map<String,String> properties = conf.getProperties();
        String serverName = properties.get("oracle_server");
        String portNumber = properties.get("oracle_port");
        String service = properties.get("oracle_service");
        String username = properties.get("oracle_username");
        String password = properties.get("oracle_password");
        String oracleUrl = "jdbc:oracle:thin:@//" + serverName + ':' + portNumber + "/" + service;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch(Exception e){
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return null;
        }
        try{
            conn = DriverManager.getConnection(oracleUrl, username, password);
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("Exception creating oracle connection:" + e.getMessage());
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
        Connection conn = getConnection();
        Statement stmt;
        ResultSet resultSet;

        try {
            stmt =  conn.createStatement();
            resultSet = stmt.executeQuery(query);
        }catch(Exception e){
            System.out.println("Exception running query: " + e.toString());
            System.out.println(query);
            e.printStackTrace();
            return null;
        }

        try {
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
            System.out.println("Exception processing resultset: " + e.toString());
            System.out.println(query);
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                System.out.println("Exception closing statement: " + e.toString());
            }
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Exception closing connection: " + e.toString());
            }
        }
        return results;
    }

    public String getQueryString(String query) {
        try {
            Map<String,String> queries = conf.getQueries();
            return queries.get(query);
        } catch (Exception e) {
            System.out.println("ERROR getting query from file: " + e.toString());
            return null;
        }
    }
}
