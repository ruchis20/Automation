@feature
Feature: SMCM9210D Job
  As system
  I want SMCM9210D job to insert billing data

  Scenario: Successful process of billing data
    Given there is no data in the billing table for amount "20", code "3501" and date "2017-08-20"
    When I run batch job "/apps_01/mcom/bin/imageBilling.sh" on server "mcm4stl6" with parameters "SMCM9210D DB_CHGBK_IMAGE_BILLING imageBilling.xml"
    Then  the log file "ImageBilling.log" in "/logs" should show it completes successfully
    And data in table matches records in log file "log.txt" in directory "/logs"
    And D031_ACQ_REF_DATA column in IPM_CLEARING_CHGBK table should equal BILLING_MEMO_TEXT column of the BILLING table
