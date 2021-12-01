Feature: API Info

  Scenario: Show API info
    When I send query to get info message
    Then I receive info message "RankMe GraphQL API 0.53-SNAPSHOT"
