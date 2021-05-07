Feature: Player

  Background: I init and cleanup database
    Given I create league "To init league scheme"
    Given I create player "To init player scheme" in league "To init league scheme"
    And I cleanup database

  Scenario: Create new player
    Given I create league "Transformers"
    When I create player "Optimus Prime" in league "Transformers"
    Then I should have player "Optimus Prime" with deviation 350 and rating 1500

  Scenario: Play draw game
    Given I create league "Marvel"
    And I create player "Batman" in league "Marvel"
    And I create player "Superman" in league "Marvel"
    When I play game between "Batman" and "Superman" with result 1 : 1
#    Then I should have player "Batman" with deviation 290 and rating 1500 TODO: Implement consumer for player-played-game
#    Then I should have player "Superman" with deviation 290 and rating 1500