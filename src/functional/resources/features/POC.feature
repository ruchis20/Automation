@poc
Feature: Proof of concept
  As a QA
  I want a set of automation functionality
  So that I can implement automated tests

  # Requirement 1
  Scenario: Verify comparison of query results with preconditions
    Given I have saved precondition for query "smcm6201d_mysql.sql"
    When I run job "ls"
    And I compare the result of query "smcm6201d_mysql.sql" with precondition
    Then then the number of records should match in both cases

  # Requirements 2, 4, 5
  Scenario: Verify comparison of query results text file
    Given I have run job "ls"
    And I have queried the database by running query "clearing_chrgbk.sql"
    When I compare value of db column "count" with column number "2" in "/logs/job.log"
    Then then the two sets should match

  # Requirement 3
  Scenario: Verify comparison of two query results
    Given I have run queries "smcm6201d_mysql.sql" and "requests.sql"
    When I compare columns "request_id" with with columns "request_id"
    Then then the two query results should match

  # Requirement 7
  Scenario: Verify file has moved from one location to another
    Given I have file "/logs/job1.log"
    When I run job "mv /logs/job1.log /tmp"
    Then the file "job1.log" should be moved from "/logs" to "/tmp"

  # Requirement 8
  Scenario: Verify a specific file have been created after batch
    When I run job "ls"
    Then the log file "HistoryUpdate.log" in "/logs" should be created
  # Requirement 9
  Scenario: Verify number of records in two tables
    Given I have run queries "smcm6201d_mysql.sql" and "requests.sql"
    When I compare number of records in the two tables
    Then then they should match
  # Requirement 10
  Scenario: Verify number of records in table and file
    Given I have run query "clearing_chrgbk.sql"
    When I compare number of records with that of log file "/logs/job.log"
    Then then they should match

  # Requirement 11
  Scenario: Verify job success from log file
    Given I have run job "ls -la"
    When I look for keyword "SMCM4095D_. LPRUS FINISHED" in the log file "/logs/batch.log"
    Then I should see the job has succeeded

  # Requirement 12
  Scenario: Manage test data in database
    When I delete test data by running query "delete_billing.sql"
    Then the data should be deleted from db
    When I insert test data in to db by running query "insert_billing.sql"
    Then the data should be insert into db