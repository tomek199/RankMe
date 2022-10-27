Feature: API Info

  Scenario: Show API version
    When I send query to get API version
    Then I receive API version "0.78-SNAPSHOT"
