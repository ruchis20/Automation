package steps

import utils.*
import static org.junit.Assert.assertTrue

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

List requestsBeforeJob
List requestsAfterJob
List billingData
def helpers = new Helpers()
def dbUtil = new DbUtil()
def fileUtil = new FileUtil()
def batchUtil = new BatchUtil()
boolean comparisonResult
List dbQuery

Given(~/^I have saved precondition for query "([^"]*)"$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    requestsBeforeJob = dbUtil.queryDb(query)
}

And(~/^I compare the result of query "([^"]*)" with precondition$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    requestsAfterJob = dbUtil.queryDb(query)
    comparisonResult = helpers.compareDbRowCounts(requestsBeforeJob,requestsAfterJob)
}

When(~/^then the number of records should match in both cases$/) { ->
    assertTrue(comparisonResult)
}

Given(~/^I have file "([^"]*)"$/) { String file ->
    String job = "echo 'test' > " + file
    batchUtil.runBatch(job)
}

Given(~/^I have run job "([^"]*)"$/) { String job ->
    batchUtil.runBatch(job)
}

Given(~/^I have queried the database by running query "([^"]*)"$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    dbQuery = dbUtil.queryDb(query)
}

When(~/^I compare value of db column "([^"]*)" with column number "([^"]*)" in "([^"]*)"$/) { String column, String field, String logFile ->
    int locator = field.toInteger() - 1
    comparisonResult = helpers.compareDbAndValuesFromLog(dbQuery,column,logFile,locator," ")
}

Then(~/^then the two sets should match$/) { ->
    assert comparisonResult
}

Given(~/^I have run queries "([^"]*)" and "([^"]*)"$/) { String queryFile_1, String queryFile_2 ->
    def query = dbUtil.prepareQuery(queryFile_1, "")
    requestsBeforeJob = dbUtil.queryDb(query)
    query = dbUtil.prepareQuery(queryFile_2, "")
    requestsAfterJob = dbUtil.queryDb(query)
}

When(~/^I compare columns "([^"]*)" with with columns "([^"]*)"$/) { String leftColumns, String rightColumns ->
    List tables = [requestsBeforeJob,requestsAfterJob]
    List columns = [[leftColumns],[rightColumns]]
    comparisonResult = helpers.compareTwoDbTables(tables,columns)
}

Then(~/^then the two query results should match$/) { ->
    assert comparisonResult
}

Then(~/^the file "([^"]*)" should be moved from "([^"]*)" to "([^"]*)"$/) { String file, String source, String destination ->
    assert fileUtil.checkFileMoved(file, source, destination)
}

When(~/^I compare number of records in the two tables/) { ->
    comparisonResult = helpers.compareDbRowCounts(requestsBeforeJob,requestsAfterJob)
}

Then(~/^then they should match$/) { ->
    assert comparisonResult
}

Given(~/^I have run query "([^"]*)"$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    requestsBeforeJob = dbUtil.queryDb(query)
}

When(~/^I compare number of records with that of log file "([^"]*)"$/) { String logFile ->
    comparisonResult = helpers.compareCountsOfDBAndLog(requestsBeforeJob,logFile)
}

When(~/^I look for keyword "([^"]*)" in the log file "([^"]*)"$/) { String keyword, String logFile ->
    comparisonResult = helpers.verifyJobSuccessInLogFile(logFile,keyword)
}

Then(~/^I should see the job has succeeded$/) { ->
    assert comparisonResult
}

When(~/^I delete test data by running query "([^"]*)"$/) { String queryFile ->

    def query = dbUtil.prepareQuery(queryFile, "")
    dbUtil.deleteData(query)
}

Then(~/^the data should be deleted from db$/) { ->
    def query = "SELECT * FROM BILLING"
    billingData = dbUtil.queryDb(query)
    assert billingData.size() == 0
}
When(~/^I insert test data in to db by running query "([^"]*)"$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    dbUtil.insertData(query)
}

Then(~/^the data should be insert into db$/) { ->
    def query = "SELECT * FROM BILLING"
    billingData = dbUtil.queryDb(query)
    assert billingData.size() == 12
}
