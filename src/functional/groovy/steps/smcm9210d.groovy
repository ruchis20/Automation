package steps

import utils.Helpers
import static org.junit.Assert.assertTrue

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

List dbChargeBackRecords
List dbBillingRecords
def helpers = new Helpers()
String logDirectory
String logFileName
String localFile
def fileHandler
def remoteFile

Given(~/^there is no data in the billing table for amount "([^"]*)", code "([^"]*)" and date "([^"]*)"$/) {
    String amount, String code, String date ->

        //these next 2 lines are purely for development purpose, please remove in production
        dbUtil.execute("delete from BILLING;")
        dbUtil.execute("ALTER TABLE BILLING AUTO_INCREMENT = 1;")

        def binding = ["amount":amount, "code":code, "date":date]
        def query = dbUtil.getQuery("billing_01.sql", binding)
        List data = dbUtil.executeSelect(query)
        assert data.size() == 0
}

When(~/^I run batch job "([^"]*)" on server "([^"]*)" with parameters "([^"]*)"$/) {
    String job, String server, String parameters ->
        //these next 2 lines are purely for development purpose, please remove in production
        def query = dbUtil.getQuery("insert_billing.sql")
        dbUtil.execute(query)

        batchUtil.runBatch(job)
        //if you know the exit value of your job, you can assign it to varialble and
        //do assertion here. otherwise, you will need to rely on the log file to determine success
        //which is next step
}

Then(~/the log file "([^"]*)" in "([^"]*)" should show it completes successfully$/) {
    String file, String directory ->
        logDirectory = directory
        logFileName = file
        remoteFile = directory + "/" + file
        localFile = dir + "/" + file
        fileHandler = new File(localFile)
        fileHandler.delete()
        fileUtil.getFile(remoteFile)
        assert fileHandler.exists() : "file not found"
        String fileContents = fileHandler.text
        assert fileContents.contains("successful")
}

Then(~/^data in table matches records in log file "([^"]*)" in directory "([^"]*)"$/) {
    String file, String directory ->
        remoteFile = directory + "/" + file
        localFile = dir + "/" + file
        fileHandler = new File(localFile)
        fileHandler.delete()
        fileUtil.getFile(remoteFile)

        def query = dbUtil.getQuery("clearing_chrgbk.sql")
        dbChargeBackRecords = dbUtil.executeSelect(query)
        println "\nComparing charge back table with log entries"
        println "=============================================="
        println "charge back : log file"
        assertTrue(helpers.compareDbTableAndLog(dbChargeBackRecords,localFile))
}

Then(~/^D031_ACQ_REF_DATA column in IPM_CLEARING_CHGBK table should equal BILLING_MEMO_TEXT column of the BILLING table$/) { ->
    def binding = ["amount":"20", "code":"3501", "minId":"1", "maxId":"12"]
    def query = dbUtil.getQuery("billing_02.sql", binding)
    dbBillingRecords = dbUtil.executeSelect(query)

    def data = [:]
    data.put("firstTable",dbChargeBackRecords)
    data.put("secondTable",dbBillingRecords)
    data.put("firstColumn","d031_acq_ref_data")
    data.put("secondColumn","billing_memo_text")

    println "\nComparing charge back table with billing table"
    println "=============================================="
    println "d031_acq_ref_data vs billing_memo_text"

    assertTrue(helpers.compareTwoDbTables(data))
}
