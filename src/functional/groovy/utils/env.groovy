package utils

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import static Configuration.*

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

World() {
    new SharedWorld()
}

class SharedWorld {
    def mysqlDataSource = {
        new MysqlDataSource(
                url: getPropertyValue("mysql_url"),
                user: getPropertyValue("mysql_username"),
                password: getPropertyValue("mysql_password"),
                allowMultiQueries: true)
    }

    def dbUtil = new DbUtil(mysqlDataSource())
    def fileUtil = new FileUtil()
    def batchUtil = new BatchUtil()
    def dir = getDestinationFolder()
}
