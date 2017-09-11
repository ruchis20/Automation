package functional.steps;

import org.jbehave.core.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import functional.utils.*;

public class PocSteps {
	private List requestsBeforeJob = null;
    private List requestsAfterJob = null;
    private List dbQueryResult = null;
    private DbUtil dbUtil = new DbUtil();
    private BatchUtil batchUtil = new BatchUtil();
    private Helpers helpers = new Helpers();
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
    public void givenIhaveRunJob(String job) {
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
}
