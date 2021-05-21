Feature: League

  Background: I init and cleanup database
    Given I create league "To init league scheme"
    Given I create player "To init player scheme" in league "To init league scheme"
    And I cleanup database

  Scenario: Create new league and change league data
    Given I create league "Star Wars"
    When I change league "Star Wars" settings to allow draws true and max score 5
    And I rename league "Star Wars" to "Transformers"
    Then I should have league "Transformers" with allow draws true and max score 5

  Scenario: Create league with players
    Given I create league "Transformers"
    When I create player "Optimus Prime" in league "Transformers"
    And I create player "Bumblebee" in league "Transformers"
    Then I have players in league "Transformers":
      | name          | deviation | rating |
      | Optimus Prime | 350       | 1500   |
      | Bumblebee     | 350       | 1500   |