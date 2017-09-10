package functional.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
public class Configuration {

    public Map<String,String> getProperties() {
        Map<String,String> properties = new HashMap<String, String>();
        Properties props = new Properties();
        String propertyFile = "src/main/resources/config.properties";
        try{
            File file = new File(propertyFile);
            FileInputStream fileInput = new FileInputStream(file);
            props.load(fileInput);
            for (Map.Entry<Object, Object> entry : props.entrySet())
            {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                properties.put(key, value);
            }
        } catch (Exception e){
            System.out.println("Properties file cannot be loaded!!! Tests might not work!!");
            System.out.println(e.toString());
        }
        return properties;
    }

    public Map<String,String> getQueries() {
        Map<String,String> queries = new HashMap<String, String>();
        Properties sql = new Properties();
        String sqlFile = "src/main/resources/sql.properties";
        try{
            File file = new File(sqlFile);
            FileInputStream fileInput = new FileInputStream(file);
            sql.load(fileInput);
            for (Map.Entry<Object, Object> entry : sql.entrySet())
            {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                queries.put(key, value);
            }
        } catch (Exception e){
            System.out.println("Properties file cannot be loaded!!! Tests might not work!!");
            System.out.println(e.toString());
        }
        return queries;
    }

    public String getUsername() {
        return System.getProperty("username");
    }

    public String getPassword() {
        return System.getProperty("password");
    }

    public String getDb() {
        return System.getProperty("db");
    }
}
