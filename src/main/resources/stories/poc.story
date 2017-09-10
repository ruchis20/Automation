Meta:

Narrative:
As a QA
I want a set of automation functionality
So that I can implement automated tests

Scenario: Verify comparison of query results with preconditions
Given I have saved precondition for query "billing"
When I run job "ls"
And I compare the result of query "billing" with precondition
Then then the number of records should match in both cases