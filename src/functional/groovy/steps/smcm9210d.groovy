package steps

import utils.*
import static org.junit.Assert.assertTrue

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

List requestsBeforeJob
List requestsAfterJob
def helpers = new Helpers()
def dbUtil = new DbUtil()
def fileUtil = new FileUtil()
def batchUtil = new BatchUtil()
String historyUpdateResult
String setMcomEnvResult
boolean comparisonResult

Given(~/^I have saved the result of query "([^"]*)"$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    requestsBeforeJob = dbUtil.queryDb(query)
}

When(~/^I run job "([^"]*)"$/) { String job ->
    setMcomEnvResult = batchUtil.runBatch(job)
}

When(~/^I run job "([^"]*)" with parameter "([^"]*)"$/) { String shell, String param ->
    def job = shell + " " + param
    historyUpdateResult = batchUtil.runBatch(job)
}

Then(~/^the job jobs should complete successfully$/) { ->
    assert setMcomEnvResult.contains("awesomeqa")
    assert historyUpdateResult.contains("awesomeqa")
}

Then(~/^the log file "([^"]*)" in "([^"]*)" should be created$/) { String file, String dir ->
    String localFile = dir + "/" + file
    boolean result = fileUtil.checkFileExists(localFile)
    assertTrue(result)
}

When(~/^I compare the result of query "([^"]*)" with previous data$/) { String queryFile ->
    def query = dbUtil.prepareQuery(queryFile, "")
    requestsAfterJob = dbUtil.queryDb(query)
    comparisonResult = helpers.compareDbRowCounts(requestsBeforeJob,requestsAfterJob)
}

When(~/^then the number of records should match$/) { ->
    assertTrue(comparisonResult)
}
