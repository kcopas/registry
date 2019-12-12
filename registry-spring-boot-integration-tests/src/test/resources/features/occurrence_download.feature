@OccurrenceDownload
Feature: Occurrence Download functionality

  Scenario: create and get instance download
    Given instance predicate download
    When create download
    Then response status should be 201
    And extract doi from download
    When get download
    Then response status should be 200
    And download assertions passed
      | predicateType | predicateKey | predicateValue | sendNotification | format | status    | downloadLink | size | totalRecords | numberDatasets |
      | equals        | TAXON_KEY    | 212            | true             | DWCA   | PREPARING | testUrl      | 0    | 0            | 0              |
    When get download by doi
    Then response status should be 200
    And download assertions passed
      | predicateType | predicateKey | predicateValue | sendNotification | format | status    | downloadLink | size | totalRecords | numberDatasets |
      | equals        | TAXON_KEY    | 212            | true             | DWCA   | PREPARING | testUrl      | 0    | 0            | 0              |
