package utils

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import groovy.sql.Sql
import static utils.Configuration.getDb
import static utils.Configuration.getPropertyValue

/**
 * utility class that contains functions for database
 * actions
 */

class DbUtil {
    Sql sql
    String dbType = getDb()
    def dbServer = getPropertyValue("oracle_server")
    def dbUser = getPropertyValue("oracle_username")
    def dbPassword = getPropertyValue("oracle_password")
    def dbDriver = 'oracle.jdbc.driver.OracleDriver'
    def port = getPropertyValue("oracle_port")
    def service = getPropertyValue("oracle_service")
    def dbUrl = 'jdbc:oracle:thin:@' + dbServer + ':' + port + "/" + service

    /**
     * Contructor that initializes the object either with Oracle or MySql
     * based on command line argument -Pdb=mysql or -Pdb=oracle
     * If parameter is missing then it defaults to oracle
     */
    DbUtil() {
        if(dbType.equalsIgnoreCase("mysql")) {
            this.sql = new Sql(
                    new MysqlDataSource(
                            url: getPropertyValue("mysql_url"),
                            user: getPropertyValue("mysql_username"),
                            password: getPropertyValue("mysql_password"),
                            allowMultiQueries: true
                    )
            )
        }else {
            this.sql = Sql.newInstance( dbUrl, dbUser, dbPassword, dbDriver )
        }
    }

    /**
     * Method to run query against db.
     * @param query string
     * @return List of maps with table column names as keys and column values as values
     */
    List queryDb(String query) {
        def data = []
        def rowResults = sql.rows(query)
        try {
            def headers = rowResults[0].keySet()
            rowResults.each { row ->
                def singleRow = [:]
                headers.each { column ->
                    singleRow.put(column, row[column.toString()])
                }
                data.add(singleRow)
            }
        }catch(Exception e){}
        return data
    }
    /**
     * This method prepares sql query from a file content
     * Uses groovy template to replace any variables in the file
     * @param file containing the query
     * @param binding - list of variae/value pairs to replace, e.g. ["username":"Ruchi", "age":21]
     * @return plain query string
     */

    String prepareQuery(String file, def binding=null) {
        def path = "src/functional/resources/sql/" + file
        def text = new File(path).text
        if(binding) {
            def engine = new groovy.text.SimpleTemplateEngine()
            engine.createTemplate(text).make(binding).toString()
        }else{
            return text
        }
    }

    /**
     * Deletes data from table
     * @param delete query
     * @return true or false
     */
    def deleteData(String query) {
        sql.executeInsert(query)
    }

    /**
     * inserts data into table
     * @param insert query
     * @return true or false
     */
    def insertData(String query) {
        def result = sql.executeInsert(query)
        return result.last()[0] as int
    }
}

