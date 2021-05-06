Feature: League

  Scenario: Manage new league
    Given I create league "Star Wars"
    When I change league "Star Wars" settings to allow draws true and max score 5
    And I rename league "Star Wars" to "Transformers"
    Then I should have league "Transformers" with allow draws true and max score 5
