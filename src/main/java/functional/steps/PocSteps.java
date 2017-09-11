package functional.steps;

import org.jbehave.core.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import functional.utils.*;

public class PocSteps {
	private List requestsBeforeJob = null;
    private List requestsAfterJob = null;
    private List dbQueryResult = null;
    private List smcm6201d = null;
    private List requests = null;
    private DbUtil dbUtil = new DbUtil();
    private BatchUtil batchUtil = new BatchUtil();
    private Helpers helpers = new Helpers();
    private FileUtil fileUtil = new FileUtil();
    private Configuration conf = new Configuration();
    private Map<String,String> queries = new HashMap<String, String>();
    boolean comparisonResult = false;
	@Given("I have saved precondition for query $query")
    public void savePrecondtions(String query) {
	    queries = conf.getQueries();
        String sql = queries.get(query);
        requestsBeforeJob = dbUtil.queryDb(sql);
    }
    @When("I run job $job")
    public void runJob(String job) {
        batchUtil.runBatch(job);
    }

    @When("I compare the result of query $query with precondition")
    public void compareQueryresults(String query) {
        String sql = queries.get(query);
        requestsAfterJob = dbUtil.queryDb(sql);
    }

    @Then("then the number of records should match in both cases")
    public void resultsShouldMatch() {
        boolean comparisonResult = helpers.compareDbRowCounts(requestsBeforeJob,requestsAfterJob);
        assert comparisonResult;
    }

    @Given("I have run job $job")
    public void givenIhaveRunJob(@Named("job") String job) {
        batchUtil.runBatch(job);
    }
    @When("I query the database by running query $query")
    public void queryDatabase(String query) {
        String sql = queries.get(query);
        dbQueryResult = dbUtil.queryDb(sql);
    }

    @When("I compare value of db column $column  with column number $field in $log")
    public void compareDbColumnWithFieldInLogFile(String column, int field, String log) {
        int locator = field - 1;
        comparisonResult = helpers.compareDbAndValuesFromLog(dbQueryResult,column,log,locator," ");
    }

    @Then("then the two sets should match")
    public void dbColumnValuesAndLogFieldValuesShouldMatch() {
        assert comparisonResult;
    }

    @Given("I have run queries $smcm6201d and $requests")
    public void givenIhaveTwoDbTables(String smcm6201dSql, String requestsSql) {
        String sql = queries.get(smcm6201dSql);
        smcm6201d = dbUtil.queryDb(sql);
        sql = queries.get(requestsSql);
        requests = dbUtil.queryDb(sql);
    }
    @When("I compare columns $request_id with columns $request_id")
    public void compareTwoDbTables(String column_1,String column_2) {
        comparisonResult = helpers.compareTwoDbTables(smcm6201d,column_1,requests,column_2);
    }

    @Then("then the two query results should match")
    public void thenTheTwoQueryResultsShouldMatch() {
        assert comparisonResult;
    }

    @Given("I have file $file")
    public void givenIhaveAFileBeforeJob(String file) {
        String job = "echo 'test' > " + file;
        batchUtil.runBatch(job);
    }
    @When("I run job mv $source $destination")
    public void iRunAJobThatMovesTheFile(String source,String destination) {
        String job = "mv " + source + " " + destination;
        batchUtil.runBatch(job);
    }

    @Then("the file $file should be moved from $source to $destination")
    public void thenTheFileShouldBeMovedToDestinationFolder(String file, String source, String destination) {
        comparisonResult = fileUtil.checkFileMoved(file,source,destination);
        assert comparisonResult;
    }

    @Then("the log file $file should be created in $path")
    public void thenTheFileShouldBeCreatedInDestinationFolder(String file, String destination) {
        comparisonResult = fileUtil.checkFileExists(file,destination);
        assert comparisonResult;
    }

    @When("I compare number of records in the two tables")
    public void compareNumberOfRecordsInTwoDbTables() {
        comparisonResult = helpers.compareDbRowCounts(smcm6201d,requests);
    }

    @Then("then they should match")
    public void thenTheTwoTablesShouldMatch() {
        assert comparisonResult;
    }

    @Given("I have run query $query")
    public void givenIhaveTwoDbTables(String query) {
        String sql = queries.get(query);
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
        assert comparisonResult;
    }
}
