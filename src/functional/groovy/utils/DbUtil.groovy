package utils

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import javax.sql.DataSource
import groovy.sql.Sql
import static utils.Configuration.getPropertyValue

class DbUtil {
    Sql sql
    def dbServer = getPropertyValue("oracle_server")
    def dbUser = getPropertyValue("oracle_username")
    def dbPassword = getPropertyValue("oracle_password")
    def dbDriver = 'oracle.jdbc.driver.OracleDriver'
    def port = getPropertyValue("oracle_port")
    def service = getPropertyValue("oracle_service")
    def dbUrl = 'jdbc:oracle:thin:@' + dbServer + ':' + port + "/" + service

    DbUtil() {
        this.sql = new Sql(
                new MysqlDataSource(
                        url: getPropertyValue("mysql_url"),
                        user: getPropertyValue("mysql_username"),
                        password: getPropertyValue("mysql_password"),
                        allowMultiQueries: true)
        )
    }

//    DbUtil() {
//        this.sql = Sql.newInstance( dbUrl, dbUser, dbPassword, dbDriver )
//    }

    def execute(String query) {
        sql.execute(query)
    }

    def List queryDb(String query) {
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

    def String prepareQuery(String file, def binding=null) {
        def path = "src/functional/resources/sql/" + file
        def text = new File(path).text
        if(binding) {
            def engine = new groovy.text.SimpleTemplateEngine()
            engine.createTemplate(text).make(binding).toString()
        }else{
            return text
        }
    }

    def deleteData(String query) {
        sql.executeInsert(query)
    }

    def insertData(String query) {
        def result = sql.executeInsert(query)
        return result.last()[0] as int
    }
}

