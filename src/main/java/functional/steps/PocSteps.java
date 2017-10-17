package functional.steps;

import org.jbehave.core.annotations.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import functional.utils.*;
import org.junit.Assert;

public class PocSteps {
	private List requestsBeforeJob = null;
    private List requestsAfterJob = null;
    private List dbQueryResult = null;
    private List smcm6201d = null;
    private List requests = null;
    private DbUtil dbUtil = new DbUtil();
    private BatchUtil batchUtil = new BatchUtil();
    private Helpers helpers = new Helpers();
    private Unix unix = new Unix();
    private boolean comparisonResult = false;
    private List xmlValues = null;

	@Given("I have saved precondition for query $query")
    public void savePrecondtions(String query) {
        String sql = dbUtil.getQueryString(query);
        requestsBeforeJob = dbUtil.queryDb(sql);
        Assert.assertTrue(requestsAfterJob.size() > 0);
    }
    @When("I run job $job")
    public void runJob(String job) {
	    String output = unix.runJob(job);
	    System.out.println("**********************************************");
        System.out.println(output);
        System.out.println("**********************************************");
        Assert.assertTrue(output.contains("some text printed on screen by the job"));
    }

    @When("I compare the result of query $query with precondition")
    public void compareQueryresults(String query) {
        String sql = dbUtil.getQueryString(query);
        requestsAfterJob = dbUtil.queryDb(sql);
        Assert.assertTrue(requestsAfterJob.size() > 0);
    }

    @Then("then the number of records should match in both cases")
    public void resultsShouldMatch() {
        boolean comparisonResult = helpers.compareDbRowCounts(requestsBeforeJob,requestsAfterJob);
        Assert.assertTrue(comparisonResult);
    }

    @Given("I have run job $job")
    public void givenIhaveRunJob(@Named("job") String job) {
        String output = batchUtil.runJob(job);
        Assert.assertTrue(output.contains("cobuma"));
    }
    @When("I query the database by running query $query")
    public void queryDatabase(String query) {
        String sql = dbUtil.getQueryString(query);
        dbQueryResult = dbUtil.queryDb(sql);
        Assert.assertTrue(dbQueryResult.size() > 0);
    }

    @When("I compare value of db column $column  with column number $field in $log")
    public void compareDbColumnWithFieldInLogFile(String column, int field, String log) {
        int locator = field - 1;
        comparisonResult = helpers.compareDbAndValuesFromLog(dbQueryResult,column,log,locator," ");
    }

    @Then("then the two sets should match")
    public void dbColumnValuesAndLogFieldValuesShouldMatch() {
        Assert.assertTrue(comparisonResult);
    }

    @Given("I have run queries $smcm6201d and $requests")
    public void givenIhaveTwoDbTables(String smcm6201dSql, String requestsSql) {
        String sql = dbUtil.getQueryString(smcm6201dSql);
        smcm6201d = dbUtil.queryDb(sql);
        sql = dbUtil.getQueryString(requestsSql);
        requests = dbUtil.queryDb(sql);
        Assert.assertTrue(smcm6201d.size() > 0);
        Assert.assertTrue(requests.size() > 0);
    }
    @When("I compare columns $request_id with columns $request_id")
    public void compareTwoDbTables(String column_1,String column_2) {
        comparisonResult = helpers.compareTwoDbTables(smcm6201d,column_1,requests,column_2);
    }

    @Then("then the two query results should match")
    public void thenTheTwoQueryResultsShouldMatch() {
        Assert.assertTrue(comparisonResult);
    }

    @Given("I have file $file")
    public void givenIhaveAFileBeforeJob(String file) {
        String job = "echo 'test' > " + file;
        batchUtil.runJob(job);
    }
    @When("I run job mv $source $destination")
    public void iRunAJobThatMovesTheFile(String source,String destination) {
        String job = "mv " + source + " " + destination;
        batchUtil.runJob(job);
    }

    @Then("the file $file should be moved from $source to $destination")
    public void thenTheFileShouldBeMovedToDestinationFolder(String file, String source, String destination) {
        comparisonResult = batchUtil.checkFileMoved(file,source,destination);
        Assert.assertTrue(comparisonResult);
    }

    @Then("the log file $file should be created in $path")
    public void thenTheFileShouldBeCreatedInDestinationFolder(String file, String destination) {
        comparisonResult = batchUtil.checkFileExists(file,destination);
        Assert.assertTrue(comparisonResult);
    }

    @When("I compare number of records in the two tables")
    public void compareNumberOfRecordsInTwoDbTables() {
        comparisonResult = helpers.compareDbRowCounts(smcm6201d,requests);
    }

    @Then("then they should match")
    public void thenTheTwoTablesShouldMatch() {
        Assert.assertTrue(comparisonResult);
    }

    @Given("I have run query $query")
    public void givenIhaveTwoDbTables(String query) {
        String sql = dbUtil.getQueryString(query);
        smcm6201d = dbUtil.queryDb(sql);
    }

    @When("I compare number of records with that of log file $log")
    public void compareCountsOfDbWithLogfile(String log) {
        comparisonResult = helpers.compareCountsOfDBAndLog(smcm6201d,log);
    }

    @When("I look for keyword $keyword in the log file $log")
    public void lookForKeywordInLogfile(@Named("log") String keyword, @Named("log") String log) {
        comparisonResult = helpers.verifyJobSuccessInLogFile(keyword,log);
    }

    @Then("I should see the job has succeeded")
    public void ishouldSeeTheJobSucceeded() {
        Assert.assertTrue(comparisonResult);
    }
    
    @Given("I have read values of attributes $attributes in node $node from XML file $xmlfile")
    public void givenIhaveReadXmlValues(@Named("attributes") String attributes, @Named("node") String node, @Named("xmlfile") String xmlfile){

        /**
         * for testing purpose only, we are going to read xml from resources directory.
         * in read world, you use FileUtil to get contents from remote Unix machine, e.g.
         * String xml = getFileContents(xmlfile);
         * xmlValues = helpers.readXML(xml,node,attributes);
         */
        try{
            String xml = new String(Files.readAllBytes(Paths.get("src/main/resources/" + xmlfile)));
            xmlValues = helpers.readXML(xml,node,attributes);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @When("I compare values of xml attributes $attributes with db query columns $columns")
    public void whenIcompareXmlWithDbResults(@Named("attributes") String attributes, @Named("columns") String columns) {

        /**
         * Initializing dbQuery for testing purpose only. In real world you would query db and store the result in a list
         */
        List<Map<String,String>> dbQuery = new ArrayList();

        try {
            HashMap firstMap = new HashMap();
            firstMap.put("record_id", "117991558");
            dbQuery.add(firstMap);
        }catch(Exception e){
            e.printStackTrace();
        }
        comparisonResult = helpers.compareDbTableWithXmlValues(dbQuery,columns,xmlValues,attributes);
    }
}
