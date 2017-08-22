package utils

import groovy.sql.Sql
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource

import static utils.Configuration.getPropertyValue
import static utils.Configuration.getPropertyValue
import static utils.Configuration.getPropertyValue
import static utils.Configuration.getPropertyValue

class DbUtil {
    Sql sql
    def dbSchema = getPropertyValue("oracle_schema")
    def dbServer = getPropertyValue("oracle_server")
    def dbUser = getPropertyValue("oracle_username")
    def dbPassword = getPropertyValue("oracle_password")
    def dbDriver = 'oracle.jdbc.driver.OracleDriver'
    def dbUrl = 'jdbc:oracle:thin:@' + dbServer + ':' + dbSchema

    DbUtil(){}

    DbUtil(MysqlDataSource dataSource) {
        this.sql = new Sql(dataSource: dataSource)
    }

//    DbUtil() {
//        this.sql = Sql.newInstance( dbUrl, dbUser, dbPassword, dbDriver )
//    }

    def execute(String query) {
        sql.execute(query)
    }

    def List executeSelect(String query) {
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

    def String getQuery(String file, def binding=null) {
        def path = "src/functional/resources/sql/" + file
        def text = new File(path).text
        if(binding) {
            def engine = new groovy.text.SimpleTemplateEngine()
            engine.createTemplate(text).make(binding).toString()
        }else{
            return text
        }
    }

    def delete(String query) {
        sql.executeInsert(query)
    }

    def insert(String query) {
        def result = sql.executeInsert(query)
        return result.last()[0] as int
    }
}

