Feature: League

  Background: I init and cleanup database
    Given I create league "To init league scheme"
    And I cleanup database

  Scenario: Manage new league
    Given I create league "Star Wars"
    When I change league "Star Wars" settings to allow draws true and max score 5
    And I rename league "Star Wars" to "Transformers"
    Then I should have league "Transformers" with allow draws true and max score 5
