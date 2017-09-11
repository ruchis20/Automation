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