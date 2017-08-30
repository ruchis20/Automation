@feature
Feature: SMCM9210D Job
  Very batch job SMCM9210D can run and process data successfully

  Scenario: Successful process of SMCM9210D job
    Given I have saved the result of query "smcm6201d_mysql.sql"
    When I run job "ls"
    And I run job "ls" with parameter "-la"
    Then  the job jobs should complete successfully
    And the log file "HistoryUpdate.log" in "/logs" should be created
    When I compare the result of query "requests.sql" with previous data
    And then the number of records should match
