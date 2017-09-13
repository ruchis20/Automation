Meta:

Narrative:
As a QA
I want a set of automation functionality
So that I can implement automated tests

Scenario: Verify comparison of query results with preconditions
Given I have saved precondition for query smcm6201d_mysql
When I run job ls
When I compare the result of query billing with precondition
Then then the number of records should match in both cases

Scenario: Verify comparison of query results text file
Given I have run job ls
When I query the database by running query clearing_chrgbk
When I compare value of db column count with column number 2 in /logs/job.log
Then then the two sets should match

Scenario: Verify comparison of two query results
Given I have run queries smcm6201d_mysql and requests
When I compare columns request_id with columns request_id
Then then the two query results should match

Scenario: Verify file has moved from one location to another
Given I have file /logs/job1.log
When I run job mv /logs/job1.log /tmp
Then the file job1.log should be moved from /logs to /tmp

Scenario: Verify a specific file have been created after batch
When I run job ls
Then the log file HistoryUpdate.log should be created in /logs


Scenario: Verify number of records in two tables
Given I have run queries smcm6201d_mysql and requests
When I compare number of records in the two tables
Then then they should match


Scenario: Verify number of records in table and file
Given I have run query clearing_chrgbk
When I compare number of records with that of log file /logs/job.log
Then then they should match

Scenario: Verify job success from log file
Given I have run job "ls -la "
When I look for keyword "SMCM4095D_. LPRUS FINISHED" in the log file /logs/batch.log
Then I should see the job has succeeded